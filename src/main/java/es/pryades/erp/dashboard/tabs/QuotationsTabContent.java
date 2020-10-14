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
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
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
import es.pryades.erp.configuration.modals.ModalNewQuotation;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.Quotation;
import es.pryades.erp.dto.User;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.dto.query.QuotationQuery;
import es.pryades.erp.dto.query.UserQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.QuotationVto;
import es.pryades.erp.vto.controlers.QuotationControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class QuotationsTabContent extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = 1192294998132880196L;

	private static final Logger LOG = Logger.getLogger( QuotationsTabContent.class );

	private PopupDateField fromDateField;
	private PopupDateField toDateField;

	private ComboBox comboCustomers;
	private ComboBox comboStatus;
	private ComboBox comboUsers;

	private TextField editReference_request;
	private TextField editReference_order;

	private Button bttnApply;
	
	private Label labelTotalCost;
	private Label labelTotalPrice;
	private Label labelTotalMargin;

	private List<Company> customers;
	private List<User> users;

	private UserDefault default_from;
	private UserDefault default_to;
	private UserDefault default_customer;
	private UserDefault default_status;
	private UserDefault default_reference_request;
	private UserDefault default_reference_order;

	public QuotationsTabContent( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "quotation_date" );
		setOrder( "desc" );

		loadUserDefaults();
	}

	@Override
	public String getResourceKey()
	{
		return "quotationsConfig";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return false; }
	public boolean hasDelete() 		{ return false; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "quotation_date", "number", "title", "customer_name", "status", "reference_request", "reference_order", "total_cost", "total_price", "total_profit", "total_quotation" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "quotation_date", "number", "title", "customer_name", "status", "reference_request", "reference_order" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		BaseTable table = new BaseTable( QuotationVto.class, this, getContext() );//, getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
		
		table.getTable().setData( table );
		table.getTable().setCellStyleGenerator( new Table.CellStyleGenerator() 
		{
			private static final long serialVersionUID = -2722780059322176413L;

			@Override
			public String getStyle( Table source, Object itemId, Object propertyId )
			{
				if ( propertyId == null )
					return null;
				
				Object item = source.getItem( itemId );
				
				Quotation quotation = (Quotation)((BaseTable)source.getData()).getRawTableContent().get( item );
				
				if ( propertyId.equals( "quotation_date" ) )
				{
					if ( quotation.isExpired() && quotation.getStatus().equals( Quotation.STATUS_SENT ) )
						return "red";
					
					return "green";
				}
				else if ( propertyId.equals( "status" ) )
				{
					if ( quotation.getStatus().equals( Quotation.STATUS_APPROVED ) )
						return "green";
					else if ( quotation.getStatus().equals( Quotation.STATUS_READY ) )
						return "yellow";
					else if ( quotation.getStatus().equals( Quotation.STATUS_SENT ) )
						return "blue";
					else if ( quotation.getStatus().equals( Quotation.STATUS_FINISHED ) )
						return "purple";
					else if ( quotation.getStatus().equals( Quotation.STATUS_DISCARDED ) )
						return "red";
					
					return "black";
				}

				return null;
			}
		});

		return table;
	}

	public List<Component> getCustomOperations()
	{
		List<Component> ops = new ArrayList<Component>();

		HorizontalLayout rowTotals = new HorizontalLayout();
		rowTotals.setWidth( "100%" );
		rowTotals.setSpacing( true );
		rowTotals.setMargin( new MarginInfo( false, true, false, true ) );
		
		labelTotalCost = new Label();
		labelTotalCost.setWidth( "300px" );
		labelTotalPrice = new Label();
		labelTotalPrice.setWidth( "300px" );
		labelTotalMargin = new Label();
		labelTotalMargin.setWidth( "300px" );
		labelTotalMargin.addStyleName( "green" );
		
		rowTotals.addComponent( labelTotalCost );
		rowTotals.addComponent( labelTotalPrice );
		rowTotals.addComponent( labelTotalMargin );

		ops.add( rowTotals );
		
		return ops;
	}

	@Override
	public Component getQueryComponent()
	{
		loadCustomers();
		loadUsers();

		fromDateField = new PopupDateField( getContext().getString( "words.from" ) );
		fromDateField.setResolution( Resolution.DAY );
		fromDateField.setDateFormat( "dd-MM-yyyy" );
		fromDateField.setValue( getDefaultDate( default_from.getData_value() ) );
		fromDateField.setWidth( "160px" );
		
		toDateField = new PopupDateField( getContext().getString( "words.to" ) );
		toDateField.setResolution( Resolution.DAY );
		toDateField.setDateFormat( "dd-MM-yyyy" );
		toDateField.setValue( getDefaultDate( default_to.getData_value() ) );
		toDateField.setWidth( "160px" );
		
		/*HorizontalLayout rowDates = new HorizontalLayout();
		rowDates.setSpacing( true );
		//rowDates.setMargin( new MarginInfotrue );
		rowDates.addComponent( fromDateField );
		rowDates.addComponent( toDateField );*/
		
		comboCustomers = new ComboBox(getContext().getString( "modalNewQuotation.comboCustomer" ));
		comboCustomers.setWidth( "200px" );
		comboCustomers.setNullSelectionAllowed( true );
		comboCustomers.setTextInputAllowed( true );
		comboCustomers.setImmediate( true );
		fillComboCustomers();
		comboCustomers.setValue( getDefaultCustomer() );
		comboCustomers.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = 6697358072742454601L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});

		comboStatus = new ComboBox(getContext().getString( "modalNewQuotation.comboStatus" ));
		comboStatus.setWidth( "160px" );
		comboStatus.setNullSelectionAllowed( true );
		comboStatus.setTextInputAllowed( false );
		comboStatus.setImmediate( true );
		fillComboStatus();
		comboStatus.setValue( getDefaultStatus() );
		comboStatus.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = 6697358072742454602L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});
		
		comboUsers = new ComboBox(getContext().getString( "modalNewQuotation.comboUser" ));
		comboUsers.setWidth( "200px" );
		comboUsers.setNullSelectionAllowed( true );
		comboUsers.setTextInputAllowed( true );
		comboUsers.setImmediate( true );
		fillComboUsers();
		comboUsers.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = 8939182068419542880L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});
		
		editReference_request = new TextField( getContext().getString( "modalNewQuotation.editReference_request" ) );
		editReference_request.setWidth( "200px" );
		editReference_request.setNullRepresentation( "" );
		editReference_request.setValue( default_reference_request.getData_value() );

		editReference_order = new TextField( getContext().getString( "modalNewQuotation.editReference_order" ) );
		editReference_order.setWidth( "200px" );
		editReference_order.setNullRepresentation( "" );
		editReference_order.setValue( default_reference_order.getData_value() );
		
		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		addButtonApplyFilterClickListener();

		HorizontalLayout rowQuery = new HorizontalLayout();
		//rowQuery.setWidth( "100%" );
		rowQuery.setSpacing( true );
		rowQuery.addComponent( fromDateField );
		rowQuery.addComponent( toDateField );
		rowQuery.addComponent( comboUsers );
		rowQuery.addComponent( comboCustomers );
		rowQuery.addComponent( comboStatus );
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
		
		query.setFrom_date( fromDateField.getValue() != null ? CalendarUtils.getDayAsLong( fromDateField.getValue() ) : null );
		query.setTo_date( toDateField.getValue() != null ? CalendarUtils.getDayAsLong( toDateField.getValue() ) : null );
		
		if ( !editReference_request.getValue().isEmpty() ) 
			query.setReference_request( "%" + editReference_request.getValue() + "%");
		
		if ( !editReference_order.getValue().isEmpty() ) 
			query.setReference_order( "%" + editReference_order.getValue()  + "%");
		
		if ( comboCustomers.getValue() != null )
			query.setRef_customer( (Long)comboCustomers.getValue() );
		
		if ( comboStatus.getValue() != null )
			query.setStatus( (Integer)comboStatus.getValue() );
		
		if ( comboUsers.getValue() != null )
			query.setRef_seller( (Long)comboUsers.getValue() );

		query.setRef_user( getContext().getUser().getId() );
			
		saveUserDefaults();
		
		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewQuotation( getContext(), OperationCRUD.OP_ADD, null, QuotationsTabContent.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewQuotation( getContext(), OperationCRUD.OP_MODIFY, (Quotation)dto, QuotationsTabContent.this ).showModalWindow();
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
		double totalCost = 0;
		double totalPrice = 0;
		double totalMargin = 0;
		
		for ( BaseDto row : rows )
		{
			Quotation quotation = (Quotation)row;
			
			totalCost += quotation.getTotalCost();
			totalPrice += quotation.getTotalPrice();
			totalMargin += quotation.getTotalMargin();
		}
		
		labelTotalCost.setValue(  getContext().getString( "quotationsConfig.totalCost" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalCost ) ) );
		labelTotalPrice.setValue(  getContext().getString( "quotationsConfig.totalPrice" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalPrice ) ) );
		labelTotalMargin.setValue(  getContext().getString( "quotationsConfig.totalMargin" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalMargin ) ) );
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
			private static final long serialVersionUID = -1609767125624817355L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				if ( CalendarUtils.checkValidPeriod( fromDateField.getValue(), toDateField.getValue() ) )
					refreshVisibleContent( true );
				else
					Utils.showNotification( getContext(), getContext().getString( "error.invalid.period" ), Notification.Type.ERROR_MESSAGE );
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
			comboCustomers.setItemCaption( company.getId(), company.getAlias() );
		}
	}

	private void fillComboStatus()
	{
		comboStatus.addItem( Quotation.STATUS_CREATED );
		comboStatus.setItemCaption( Quotation.STATUS_CREATED, getContext().getString( "quotation.status." + Quotation.STATUS_CREATED ) );

		comboStatus.addItem( Quotation.STATUS_READY );
		comboStatus.setItemCaption( Quotation.STATUS_READY, getContext().getString( "quotation.status." + Quotation.STATUS_READY ) );

		comboStatus.addItem( Quotation.STATUS_SENT );
		comboStatus.setItemCaption( Quotation.STATUS_SENT, getContext().getString( "quotation.status." + Quotation.STATUS_SENT ) );

		comboStatus.addItem( Quotation.STATUS_APPROVED );
		comboStatus.setItemCaption( Quotation.STATUS_APPROVED, getContext().getString( "quotation.status." + Quotation.STATUS_APPROVED ) );

		comboStatus.addItem( Quotation.STATUS_FINISHED );
		comboStatus.setItemCaption( Quotation.STATUS_FINISHED, getContext().getString( "quotation.status." + Quotation.STATUS_FINISHED ) );

		comboStatus.addItem( Quotation.STATUS_DISCARDED );
		comboStatus.setItemCaption( Quotation.STATUS_DISCARDED, getContext().getString( "quotation.status." + Quotation.STATUS_DISCARDED ) );
	}

	public void onShowPdf( Long id )
	{
		try
		{
			Quotation quotation = (Quotation)getTable().getRowValue( id );
			
			long ts = CalendarUtils.getTodayAsLong( "UTC" );
			
			String pagesize = "A4";
			String template = quotation.getCustomer().getTaxable().booleanValue() ? "national-quotation-template" : "international-quotation-template";
			String timeout = "60";
			
			String extra = "ts=" + ts + 
					"&id=" + quotation.getId() + 
					"&name=QT-" + quotation.getFormattedNumber() + "-" +  quotation.getReference_request() + ".pdf" + 
					"&pagesize=" + pagesize + 
					"&template=" + template +
					"&timeout=" + timeout;
			
			String user = getContext().getUser().getLogin();
			String password = getContext().getUser().getPwd();
			
			String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
			String code = "code=" + Authorization.encrypt( extra, password ) ;

			String url = getContext().getData( "Url" ) + "/services/quotation" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
			
			String caption = getContext().getString( "template.quotation.quotation" ) + " " + quotation.getFormattedNumber() ;

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
	
	public void refreshCustomers()
	{
		loadCustomers();
		fillComboCustomers();
	}

	private void loadUserDefaults()
	{
		default_from = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATIONS_FROM );
		default_to = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATIONS_TO );
		default_customer = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATIONS_CUSTOMER );
		default_status = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATIONS_STATUS );
		default_reference_request = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATIONS_REFERENCE_REQUEST );
		default_reference_order = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATIONS_REFERENCE_ORDER );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_from, fromDateField.getValue() != null ? Long.toString( CalendarUtils.getDayAsLong( fromDateField.getValue() ) ) : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_to, toDateField.getValue() != null ? Long.toString( CalendarUtils.getDayAsLong( toDateField.getValue() ) ) : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_customer, comboCustomers.getValue() != null ? comboCustomers.getValue().toString() : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_status, comboStatus.getValue() != null ? comboStatus.getValue().toString() : null );
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

	private Integer getDefaultStatus() 
	{
		try
		{
			return Integer.parseInt( default_status.getData_value() );
		}
		catch ( Throwable e )
		{
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private void loadUsers()
	{
		try
		{
			UserQuery query = new UserQuery();

			users = IOCManager._UsersManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			users = new ArrayList<User>();
		}
	}
	
	private void fillComboUsers()
	{
		for ( User user: users )
		{
			comboUsers.addItem( user.getId() );
			comboUsers.setItemCaption( user.getId(), user.getLogin() );
		}
	}	
}

