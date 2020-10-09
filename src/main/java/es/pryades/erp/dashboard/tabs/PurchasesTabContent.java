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

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD.OperationCRUD;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.Utils;
import es.pryades.erp.configuration.modals.ModalNewPurchase;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Purchase;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.User;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.dto.query.PurchaseQuery;
import es.pryades.erp.dto.query.UserQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.PurchaseVto;
import es.pryades.erp.vto.controlers.PurchaseControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class PurchasesTabContent extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -1902287089851682611L;

	private static final Logger LOG = Logger.getLogger( PurchasesTabContent.class );

	private PopupDateField fromDateField;
	private PopupDateField toDateField;

	private ComboBox comboProviders;
	private ComboBox comboStatus;
	private ComboBox comboBuyer;

	private Button bttnApply;
	
	private Label labelTotalNetPrice;
	private Label labelTotalTaxes;
	private Label labelTotalGrossPrice;

	private List<Company> providers;
	private List<User> buyers;

	private UserDefault default_from;
	private UserDefault default_to;
	private UserDefault default_customer;
	private UserDefault default_status;

	public PurchasesTabContent( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "purchase_date" );
		setOrder( "desc" );

		loadUserDefaults();
	}

	@Override
	public String getResourceKey()
	{
		return "purchasesTab";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return false; }
	public boolean hasDelete() 		{ return false; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "purchase_date", "register_date", "number", "title", "provider_name", "status", "net_price", "net_tax", "gross_price", "payed" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "purchase_date", "register_date", "number", "title", "provider_name", "status" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		BaseTable table = new BaseTable( PurchaseVto.class, this, getContext() );
		
		/*table.getTable().setData( table );
		table.getTable().setCellStyleGenerator( new Table.CellStyleGenerator() 
		{
			private static final long serialVersionUID = 7788851530339856486L;

			@Override
			public String getStyle( Table source, Object itemId, Object propertyId )
			{
				if ( propertyId == null )
					return null;
				
				Object item = source.getItem( itemId );
				
				Purchase purchase = (Purchase)((BaseTable)source.getData()).getRawTableContent().get( item );
				
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
					else if ( quotation.getStatus().equals( Quotation.STATUS_DISCARDED ) )
						return "red";
					
					return "black";
				}

				return null;
			}
		});
		 */
		return table;
	}

	public List<Component> getCustomOperations()
	{
		List<Component> ops = new ArrayList<Component>();

		HorizontalLayout rowTotals = new HorizontalLayout();
		rowTotals.setWidth( "100%" );
		rowTotals.setSpacing( true );
		rowTotals.setMargin( new MarginInfo( false, true, false, true ) );
		
		labelTotalNetPrice = new Label();
		labelTotalNetPrice.setWidth( "300px" );
		labelTotalTaxes = new Label();
		labelTotalTaxes.setWidth( "300px" );
		labelTotalGrossPrice = new Label();
		labelTotalGrossPrice.setWidth( "300px" );
		
		rowTotals.addComponent( labelTotalNetPrice );
		rowTotals.addComponent( labelTotalTaxes );
		rowTotals.addComponent( labelTotalGrossPrice );
		
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
		//fromDateField.setValue( getDefaultDate( default_from.getData_value() ) );
		fromDateField.setWidth( "160px" );
		
		toDateField = new PopupDateField( getContext().getString( "words.to" ) );
		toDateField.setResolution( Resolution.DAY );
		toDateField.setDateFormat( "dd-MM-yyyy" );
		//toDateField.setValue( getDefaultDate( default_to.getData_value() ) );
		toDateField.setWidth( "160px" );
		
		comboProviders = new ComboBox(getContext().getString( "modalNewPurchase.comboProviders" ));
		comboProviders.setWidth( "100%" );
		comboProviders.setNullSelectionAllowed( true );
		comboProviders.setTextInputAllowed( true );
		comboProviders.setImmediate( true );
		fillComboCustomers();
//		comboProviders.setValue( getDefaultCustomer() );
		comboProviders.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -180226785141609420L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});

		comboStatus = new ComboBox(getContext().getString( "modalNewPurchase.comboStatus" ));
		comboStatus.setWidth( "100%" );
		comboStatus.setNullSelectionAllowed( true );
		comboStatus.setTextInputAllowed( false );
		comboStatus.setImmediate( true );
		fillComboStatus();
//		comboStatus.setValue( getDefaultStatus() );
		comboStatus.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -6032292646221912144L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});
		
		comboBuyer = new ComboBox(getContext().getString( "modalNewPurchase.comboBuyer" ));
		comboBuyer.setWidth( "100%" );
		comboBuyer.setNullSelectionAllowed( true );
		comboBuyer.setTextInputAllowed( true );
		comboBuyer.setImmediate( true );
		fillComboUsers();
		comboBuyer.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -7091563748538562850L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});
		
		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		addButtonApplyFilterClickListener();

		HorizontalLayout rowQuery = new HorizontalLayout();
		rowQuery.setSpacing( true );
		rowQuery.addComponent( fromDateField );
		rowQuery.addComponent( toDateField );
		rowQuery.addComponent( comboBuyer );
		rowQuery.addComponent( comboProviders );
		rowQuery.addComponent( comboStatus );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		PurchaseQuery query = new PurchaseQuery();
		
		/*query.setFrom_date( fromDateField.getValue() != null ? CalendarUtils.getDateAsLong( fromDateField.getValue() ) : null );
		query.setTo_date( toDateField.getValue() != null ? CalendarUtils.getDateAsLong( toDateField.getValue() ) : null );
		
		if ( comboProviders.getValue() != null )
			query.setRef_provider( (Long)comboProviders.getValue() );
		
		if ( comboStatus.getValue() != null )
			query.setStatus( (Integer)comboStatus.getValue() );
		
		if ( comboBuyer.getValue() != null )
			query.setRef_buyer( (Long)comboBuyer.getValue() );

		saveUserDefaults();
		*/
		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewPurchase( getContext(), OperationCRUD.OP_ADD, null, PurchasesTabContent.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewPurchase( getContext(), OperationCRUD.OP_MODIFY, (Purchase)dto, PurchasesTabContent.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new PurchaseControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Purchase();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._PurchasesManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
		double totalNetPrice = 0;
		double totalTaxes = 0;
		double totalGrossPrice = 0;
		
		for ( BaseDto row : rows )
		{
			Purchase quotation = (Purchase)row;
			
			totalNetPrice += quotation.getNet_price();
			totalTaxes += quotation.getNet_tax();
			totalGrossPrice += quotation.getGrossPrice();
		}
		
		labelTotalNetPrice.setValue(  getContext().getString( "purchasesTab.totalNetPrice" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalNetPrice ) ) );
		labelTotalTaxes.setValue(  getContext().getString( "purchasesTab.totalTaxes" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalTaxes ) ) );
		labelTotalGrossPrice.setValue(  getContext().getString( "purchasesTab.totalGrossPrice" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalGrossPrice ) ) );
	}
	
	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.purchases.add" );
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
			private static final long serialVersionUID = 4803823213706637810L;

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

			providers = IOCManager._CompaniesManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			providers = new ArrayList<Company>();
		}
	}
	
	private void fillComboCustomers()
	{
		comboProviders.removeAllItems();
		for ( Company company : providers )
		{
			comboProviders.addItem( company.getId() );
			comboProviders.setItemCaption( company.getId(), company.getAlias() );
		}
	}

	private void fillComboStatus()
	{
		comboStatus.addItem( Purchase.STATUS_CREATED );
		comboStatus.setItemCaption( Purchase.STATUS_CREATED, getContext().getString( "purchase.status." + Purchase.STATUS_CREATED ) );

		comboStatus.addItem( Purchase.STATUS_ORDERED );
		comboStatus.setItemCaption( Purchase.STATUS_ORDERED, getContext().getString( "purchase.status." + Purchase.STATUS_ORDERED ) );

		comboStatus.addItem( Purchase.STATUS_RECEIVED );
		comboStatus.setItemCaption( Purchase.STATUS_RECEIVED, getContext().getString( "purchase.status." + Purchase.STATUS_RECEIVED ) );
	}

	public void onShowPdf( Long id )
	{
		/*try
		{
			Purchase quotation = (Purchase)getTable().getRowValue( id );
			
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
		}*/
	}
	
	public void refreshCustomers()
	{
		loadCustomers();
		fillComboCustomers();
	}

	private void loadUserDefaults()
	{
		/*default_from = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATIONS_FROM );
		default_to = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATIONS_TO );
		default_customer = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATIONS_CUSTOMER );
		default_status = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATIONS_STATUS );
		default_reference_request = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATIONS_REFERENCE_REQUEST );
		default_reference_order = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATIONS_REFERENCE_ORDER );*/
	}

	private void saveUserDefaults()
	{
		/*IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_from, fromDateField.getValue() != null ? Long.toString( CalendarUtils.getDateAsLong( fromDateField.getValue() ) ) : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_to, toDateField.getValue() != null ? Long.toString( CalendarUtils.getDateAsLong( toDateField.getValue() ) ) : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_customer, comboProviders.getValue() != null ? comboProviders.getValue().toString() : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_status, comboStatus.getValue() != null ? comboStatus.getValue().toString() : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_reference_request, editReference_request.getValue() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_reference_order, editReference_order.getValue() );*/
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

			buyers = IOCManager._UsersManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			buyers = new ArrayList<User>();
		}
	}
	
	private void fillComboUsers()
	{
		for ( User user: buyers )
		{
			comboBuyer.addItem( user.getId() );
			comboBuyer.setItemCaption( user.getId(), user.getName() );
		}
	}	
}

