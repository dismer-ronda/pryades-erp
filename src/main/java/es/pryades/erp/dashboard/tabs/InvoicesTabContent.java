package es.pryades.erp.dashboard.tabs;

import java.util.ArrayList;
import java.util.Date;
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

import es.pryades.erp.application.ShowExternalUrlDlg;
import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Authorization;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD.OperationCRUD;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.Utils;
import es.pryades.erp.configuration.modals.ModalNewInvoice;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dashboard.Dashboard;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.UserDefault;
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

	private UserDefault default_from;
	private UserDefault default_to;
	private UserDefault default_customer;
	private UserDefault default_reference_request;
	private UserDefault default_reference_order;

	public InvoicesTabContent( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "invoice_date" );
		setOrder( "desc" );

		loadUserDefaults();
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
		return new String[]{ "invoice_date", "number", "title", "customer_name", "quotation_number", "reference_order", "total_price", "total_taxes", "total_invoice", "collected" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "invoice_date", "number", "title", "customer_name", "quotation_number", "reference_order" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new BaseTable( InvoiceVto.class, this, getContext() );//, getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	public List<Component> getCustomOperations()
	{
		List<Component> ops = new ArrayList<Component>();
		
		Button bttnPdf = new Button();
		bttnPdf.setCaption( getContext().getString( "invoicesConfig.pdf" ) );
		bttnPdf.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -819877665197234072L;

			public void buttonClick( ClickEvent event )
			{
				onShowPdf();
			}
		} );

		ops.add( bttnPdf );

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
		fromDateField.setValue( getDefaultDate( default_from.getData_value() ) );

		toDateField = new PopupDateField( getContext().getString( "words.to" ) );
		toDateField.setResolution( Resolution.DAY );
		toDateField.setDateFormat( "dd-MM-yyyy" );
		toDateField.setWidth( "160px" );
		toDateField.setValue( getDefaultDate( default_to.getData_value() ) );
		
		comboCustomers = new ComboBox(getContext().getString( "invoicesConfig.comboCustomer" ));
		comboCustomers.setWidth( "100%" );
		comboCustomers.setNullSelectionAllowed( true );
		comboCustomers.setTextInputAllowed( true );
		comboCustomers.setImmediate( true );
		fillComboCustomers();
		comboCustomers.setValue( getDefaultCustomer() );
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
		editReference_request.setValue( default_reference_request.getData_value() );
		
		editReference_order = new TextField( getContext().getString( "invoicesConfig.editReference_order" ) );
		editReference_order.setWidth( "100%" );
		editReference_order.setNullRepresentation( "" );
		editReference_order.setValue( default_reference_order.getData_value() );
		
		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
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
		
		saveUserDefaults();

		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewInvoice( getContext(), OperationCRUD.OP_ADD, null, InvoicesTabContent.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewInvoice( getContext(), OperationCRUD.OP_MODIFY, (Invoice)dto, InvoicesTabContent.this ).showModalWindow();
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

	private void loadUserDefaults()
	{
		default_from = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.INVOICES_FROM );
		default_to = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.INVOICES_TO );
		default_customer = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.INVOICES_CUSTOMER );
		default_reference_request = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.INVOICES_REFERENCE_REQUEST );
		default_reference_order = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.INVOICES_REFERENCE_ORDER );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_from, fromDateField.getValue() != null ? Long.toString( CalendarUtils.getDateAsLong( fromDateField.getValue() ) ) : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_to, toDateField.getValue() != null ? Long.toString( CalendarUtils.getDateAsLong( toDateField.getValue() ) ) : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_customer, comboCustomers.getValue() != null ? comboCustomers.getValue().toString() : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_reference_request, editReference_request.getValue() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_reference_order, editReference_order.getValue() );
	}
	
	private Date getDefaultDate( String date )
	{
		try
		{
			return CalendarUtils.getServerCalendarFromDateLong( Long.parseLong( date ) ).getTime();
		}
		catch ( Throwable e )
		{
		}
		
		return null;
	}
	
	private Long getDefaultCustomer() 
	{
		try
		{
			return Long.parseLong( default_customer.getData_value() );
		}
		catch ( Throwable e )
		{
		}
		
		return null;
	}
	
	private void onShowPdf()
	{
		try
		{
			long ts = CalendarUtils.getTodayAsLong( "UTC" );
			
			String pagesize = "A4";
			String template = "list-invoices-template";
			String timeout = "0";
			
			InvoiceQuery query = (InvoiceQuery)getQueryObject();
			
			String extra = "ts=" + ts + 
					"&query=" + Utils.getUrlEncoded( Utils.toJson( query ) ) + 
					"&pagesize=" + pagesize + 
					"&language=" + getContext().getLanguage() +
					"&template=" + template +
					"&name=" + getContext().getString( "invoicesConfig.invoices" ) + "-" + query.getPeriodToString() +
					"&url=" + getContext().getData( "Url" ) +
					"&timeout=" + timeout;
			
			String user = getContext().getUser().getLogin();
			String password = getContext().getUser().getPwd();
			
			String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
			String code = "code=" + Authorization.encrypt( extra, password ) ;

			String url = getContext().getData( "Url" ) + "/services/invoices" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
			
			String caption = getContext().getString( "invoicesConfig.list" );

			ShowExternalUrlDlg dlg = new ShowExternalUrlDlg(); 
	
			dlg.setContext( getContext() );
			dlg.setUrl( url );
			dlg.setCaption( caption );
			dlg.createComponents();
			
			getUI().addWindow( dlg );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
}

