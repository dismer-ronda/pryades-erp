package es.pryades.erp.configuration.tabs;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
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
import es.pryades.erp.common.PagedTable;
import es.pryades.erp.configuration.modals.ModalNewQuotation;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.Quotation;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.dto.query.QuotationQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.QuotationVto;
import es.pryades.erp.vto.controlers.QuotationControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class QuotationsConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -7369095102772137067L;
	
	private PopupDateField fromDateField;
	private PopupDateField toDateField;

	private ComboBox comboCompanies;

	private TextField editReference_request;
	private TextField editReference_order;

	private Button bttnApply;
	
	private List<Company> companies;

	public QuotationsConfig( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "alias" );
		setOrder( "desc" );
	}

	@Override
	public String getResourceKey()
	{
		return "quotationsConfig";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "quotation_date", "quotation_deliveries", "customer_name", "reference_request", "reference_order", "discount", "total_discount", "total_cost", "total_price", "total_profit" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "quotation_date", "customer_name" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( QuotationVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	public List<Component> getCustomOperations()
	{
		return null;
	}

	@Override
	public Component getQueryComponent()
	{
		loadCompanies();

		fromDateField = new PopupDateField( getContext().getString( "words.from" ) );
		fromDateField.setResolution( Resolution.DAY );
		fromDateField.setDateFormat( "dd-MM-yyyy" );
		fromDateField.setWidth( "100px" );
		fromDateField.setValue( null );
		
		toDateField = new PopupDateField( getContext().getString( "words.to" ) );
		toDateField.setResolution( Resolution.DAY );
		toDateField.setDateFormat( "dd-MM-yyyy" );
		toDateField.setWidth( "100px" );
		toDateField.setValue( null );
		
		comboCompanies = new ComboBox(getContext().getString( "modalNewQuotation.comboCustomer" ));
		comboCompanies.setWidth( "100%" );
		comboCompanies.setNullSelectionAllowed( true );
		comboCompanies.setTextInputAllowed( true );
		comboCompanies.setImmediate( true );
		fillComboCompanies();

		editReference_request = new TextField( getContext().getString( "modalNewQuotation.editReference_request" ) );
		editReference_request.setWidth( "100%" );
		editReference_request.setNullRepresentation( "" );
		
		editReference_order = new TextField( getContext().getString( "modalNewQuotation.editReference_order" ) );
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
		rowQuery.addComponent( comboCompanies );
		rowQuery.addComponent( editReference_request );
		rowQuery.addComponent( editReference_order );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		QuotationQuery query = new QuotationQuery();
		
		query.setFrom_date( fromDateField.getValue() != null ? CalendarUtils.getDateAsLong( fromDateField.getValue() ) : null );
		query.setTo_date( toDateField.getValue() != null ? CalendarUtils.getDateAsLong( toDateField.getValue() ) : null );
		
		if ( !editReference_request.getValue().isEmpty() ) 
			query.setReference_request( editReference_request.getValue() );
		
		if ( !editReference_order.getValue().isEmpty() ) 
			query.setReference_order( editReference_order.getValue() );
		
		if ( comboCompanies.getValue() != null )
			query.setRef_customer( (Long)comboCompanies.getValue() );
		
		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewQuotation( getContext(), Operation.OP_ADD, null, QuotationsConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewQuotation( getContext(), Operation.OP_MODIFY, (Quotation)dto, QuotationsConfig.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new QuotationControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Quotation();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._QuotationsManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
	}
	
	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.quotations.add" );
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
			private static final long serialVersionUID = -5887424872001461130L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				refreshVisibleContent( true );
			}
		} );
	}

	@SuppressWarnings("unchecked")
	private void loadCompanies()
	{
		try
		{
			CompanyQuery query = new CompanyQuery();
			query.setType_company( Company.TYPE_CUSTOMER );
			query.setRef_user( getContext().getUser().getId() );

			companies = IOCManager._CompaniesManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			companies = new ArrayList<Company>();
		}
	}
	
	private void fillComboCompanies()
	{
		for ( Company company : companies )
		{
			comboCompanies.addItem( company.getId() );
			comboCompanies.setItemCaption( company.getId(), company.getName() );
		}
	}

}

