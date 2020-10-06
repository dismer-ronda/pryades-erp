package es.pryades.erp.dashboard.tabs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD.Operation;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.Utils;
import es.pryades.erp.configuration.modals.ModalNewInvoice;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.dto.query.InvoiceQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.InvoiceVto;
import es.pryades.erp.vto.controlers.InvoiceControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class InvoicesTabContent extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -6400806879059724780L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( InvoicesTabContent.class );

	private PopupDateField fromDateField;
	private PopupDateField toDateField;

	private ComboBox comboCustomers;

	private TextField editReference_request;
	private TextField editReference_order;

	private Button bttnApply;

	private Label labelTotalPrice;

	private List<Company> customers;

	public InvoicesTabContent( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "invoice_date" );
		setOrder( "desc" );
	}

	@Override
	public String getResourceKey()
	{
		return "invoicesConfig";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "invoice_date", "number", "title", "customer_name", "reference_order", "total_price", "total_invoice" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "invoice_date", "number", "title", "customer_name", "reference_order" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new BaseTable( InvoiceVto.class, this, getContext() );//, getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	public List<Component> getCustomOperations()
	{
		List<Component> ops = new ArrayList<Component>();
		
		HorizontalLayout rowTotals = new HorizontalLayout();
		rowTotals.setWidth( "100%" );
		rowTotals.setSpacing( true );
		rowTotals.setMargin( new MarginInfo( false, true, false, true ) );
		
		labelTotalPrice = new Label();
		labelTotalPrice.setWidth( "300px" );
		labelTotalPrice.addStyleName( "green" );
		
		rowTotals.addComponent( labelTotalPrice );

		ops.add( rowTotals );
		
		return ops;
	}

	@Override
	public Component getQueryComponent()
	{
		loadCustomers();

		fromDateField = new PopupDateField( getContext().getString( "words.from" ) );
		fromDateField.setResolution( Resolution.DAY );
		fromDateField.setDateFormat( "dd-MM-yyyy" );
		fromDateField.setWidth( "160px" );
		fromDateField.setValue( null );
		
		toDateField = new PopupDateField( getContext().getString( "words.to" ) );
		toDateField.setResolution( Resolution.DAY );
		toDateField.setDateFormat( "dd-MM-yyyy" );
		toDateField.setWidth( "160px" );
		toDateField.setValue( null );
		
		comboCustomers = new ComboBox(getContext().getString( "invoicesConfig.comboCustomer" ));
		comboCustomers.setWidth( "100%" );
		comboCustomers.setNullSelectionAllowed( true );
		comboCustomers.setTextInputAllowed( true );
		comboCustomers.setImmediate( true );
		fillComboCustomers();
		comboCustomers.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = 4526456335525954683L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});

		editReference_request = new TextField( getContext().getString( "invoicesConfig.editReference_request" ) );
		editReference_request.setWidth( "100%" );
		editReference_request.setNullRepresentation( "" );
		
		editReference_order = new TextField( getContext().getString( "invoicesConfig.editReference_order" ) );
		editReference_order.setWidth( "100%" );
		editReference_order.setNullRepresentation( "" );
		
		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		//bttnApply.setStyleName( "borderless" );
		//bttnApply.setIcon( new ThemeResource( "images/accept.png" ) );
		addButtonApplyFilterClickListener();

		HorizontalLayout rowQuery = new HorizontalLayout();
		rowQuery.setSpacing( true );
		rowQuery.addComponent( fromDateField );
		rowQuery.addComponent( toDateField );
		rowQuery.addComponent( comboCustomers );
		rowQuery.addComponent( editReference_request );
		rowQuery.addComponent( editReference_order );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		InvoiceQuery query = new InvoiceQuery();
		
		query.setFrom_date( fromDateField.getValue() != null ? CalendarUtils.getDateAsLong( fromDateField.getValue() ) : null );
		query.setTo_date( toDateField.getValue() != null ? CalendarUtils.getDateAsLong( toDateField.getValue() ) : null );
		
		if ( !editReference_request.getValue().isEmpty() ) 
			query.setReference_request( "%" + editReference_request.getValue() + "%");
		
		if ( !editReference_order.getValue().isEmpty() ) 
			query.setReference_order( "%" + editReference_order.getValue()  + "%");
		
		if ( comboCustomers.getValue() != null )
			query.setRef_customer( (Long)comboCustomers.getValue() );
		
		query.setRef_user( getContext().getUser().getId() );
		
		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewInvoice( getContext(), Operation.OP_ADD, null, InvoicesTabContent.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewInvoice( getContext(), Operation.OP_MODIFY, (Invoice)dto, InvoicesTabContent.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new InvoiceControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Invoice();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._InvoicesManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
		double totalPrice = 0;
		
		for ( BaseDto row : rows )
		{
			Invoice quotation = (Invoice)row;
			
			totalPrice += quotation.getGrandTotalInvoice();
		}
		
		labelTotalPrice.setValue(  getContext().getString( "invoicesConfig.totalPrice" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalPrice ) ) );
	}
	
	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.invoices.add" );
	}

	@Override
	public void updateComponent()
	{
	}

	@Override
	public void onFieldEvent( Component component, String column )
	{
	}

	public void addButtonApplyFilterClickListener()
	{
		bttnApply.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 6308412745183922396L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				refreshVisibleContent( true );
			}
		} );
	}

	@SuppressWarnings("unchecked")
	private void loadCustomers()
	{
		try
		{
			CompanyQuery query = new CompanyQuery();
			query.setType_company( Company.TYPE_CUSTOMER );
			query.setRef_user( getContext().getUser().getId() );

			customers = IOCManager._CompaniesManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			customers = new ArrayList<Company>();
		}
	}
	
	private void fillComboCustomers()
	{
		comboCustomers.removeAllItems();
		for ( Company company : customers )
		{
			comboCustomers.addItem( company.getId() );
			comboCustomers.setItemCaption( company.getId(), company.getName() );
		}
	}

	public void refreshCustomers()
	{
		loadCustomers();
		fillComboCustomers();
	}
}

