package es.pryades.erp.dashboard.tabs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;

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
import es.pryades.erp.configuration.modals.ModalNewPurchase;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Operation;
import es.pryades.erp.dto.Purchase;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.User;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.dto.query.OperationQuery;
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

	private ComboBox comboOperations;
	private ComboBox comboProviders;
	private ComboBox comboStatus;
	private ComboBox comboBuyer;
	private ComboBox comboType;
	private CheckBox checkForPayment;
	private Button bttnApply;
	
	private Label labelTotalNetPrice;
	private Label labelTotalTaxes;
	private Label labelTotalGrossPrice;
	private Label labelTotalPayed;
	private Label labelTotalPending;

	private List<Operation> operations;
	private List<Company> providers;
	private List<User> buyers;

	private UserDefault default_from;
	private UserDefault default_to;
	private UserDefault default_provider;
	private UserDefault default_operation;
	private UserDefault default_buyer;
	private UserDefault default_status;
	private UserDefault default_type;

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
		return new String[]{ "purchase_date", "number", "title", "operation_title", "provider_name", "invoice_number", "status", "net_price", "net_tax", "for_payment" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "purchase_date", "number", "title", "operation_title", "invoice_number", "provider_name", "status" };
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

		Button bttnPdf = new Button();
		bttnPdf.setCaption( getContext().getString( "purchasesTab.list.pdf" ) );
		bttnPdf.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -819877665197234072L;

			public void buttonClick( ClickEvent event )
			{
				onShowList();
			}
		} );
		ops.add( bttnPdf );

		Button bttnXls = new Button();
		bttnXls.setCaption( getContext().getString( "purchasesTab.list.xls" ) );
		ops.add( bttnXls );
		
		FileDownloader fileDownloaderXls = new FileDownloader( getXlsResource() );
        fileDownloaderXls.setOverrideContentType( true );
        fileDownloaderXls.extend( bttnXls );

		Button bttnZip = new Button();
		bttnZip.setCaption( getContext().getString( "purchasesTab.download.zip" ) );
		ops.add( bttnZip );

        FileDownloader fileDownloaderZip = new FileDownloader( getZipResource() );
        fileDownloaderZip.extend( bttnZip );
		
		return ops;
	}
	
	@Override
	public Component getTotalsComponent()
	{
		HorizontalLayout rowTotals = new HorizontalLayout();
		rowTotals.setWidth( "100%" );
		rowTotals.setSpacing( true );
		rowTotals.setMargin( new MarginInfo( false, true, true, true ) );
		
		labelTotalNetPrice = new Label();
		labelTotalNetPrice.setStyleName( "centered border" );

		labelTotalTaxes = new Label();
		labelTotalTaxes.setStyleName( "centered border" );
		
		labelTotalGrossPrice = new Label();
		labelTotalGrossPrice.setStyleName( "centered border" );

		labelTotalPayed = new Label();
		labelTotalPayed.setStyleName( "centered border" );

		labelTotalPending = new Label();
		labelTotalPending.setStyleName( "centered border" );

		rowTotals.addComponent( labelTotalNetPrice );
		rowTotals.addComponent( labelTotalTaxes );
		rowTotals.addComponent( labelTotalGrossPrice );
		rowTotals.addComponent( labelTotalPayed );
		rowTotals.addComponent( labelTotalPending );
		
		return rowTotals;
	}

	@Override
	public Component getQueryComponent()
	{
		loadOperations();
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
		
		comboOperations = new ComboBox(getContext().getString( "modalNewPurchase.comboOperation" ));
		comboOperations.setWidth( "200px" );
		comboOperations.setNullSelectionAllowed( true );
		comboOperations.setTextInputAllowed( true );
		comboOperations.setImmediate( true );
		fillComboOperations();
		comboOperations.setValue( getDefaultOperation() );
		comboOperations.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -180226785141609420L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});

		comboProviders = new ComboBox(getContext().getString( "modalNewPurchase.comboProviders" ));
		comboProviders.setWidth( "200px" );
		comboProviders.setNullSelectionAllowed( true );
		comboProviders.setTextInputAllowed( true );
		comboProviders.setImmediate( true );
		fillComboProviders();
		comboProviders.setValue( getDefaultProvider() );
		comboProviders.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -6032292646221912144L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});

		comboStatus = new ComboBox(getContext().getString( "modalNewPurchase.comboStatus" ));
		comboStatus.setWidth( "160px" );
		comboStatus.setNullSelectionAllowed( true );
		comboStatus.setTextInputAllowed( false );
		comboStatus.setImmediate( true );
		fillComboStatus();
		comboStatus.setValue( getDefaultStatus() );
		comboStatus.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -6032292646221912145L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});
		
		comboBuyer = new ComboBox(getContext().getString( "modalNewPurchase.comboBuyer" ));
		comboBuyer.setWidth( "200px" );
		comboBuyer.setNullSelectionAllowed( true );
		comboBuyer.setTextInputAllowed( true );
		comboBuyer.setImmediate( true );
		fillComboUsers();
		comboBuyer.setValue( getDefaultBuyer() );
		comboBuyer.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -7091563748538562850L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});
		
		comboType = new ComboBox( getContext().getString( "modalNewPurchase.comboType" ) );
		comboType.setWidth( "200px" );
		comboType.setNullSelectionAllowed( true );
		comboType.setTextInputAllowed( true );
		comboType.setImmediate( true );
		fillComboType();
		comboType.setValue( getDefaultType() );
		comboType.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -6012034844546515312L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});
		
		checkForPayment = new CheckBox( getContext().getString( "purchasesTab.checkForPayment" ) );
		checkForPayment.setValue( false );
		checkForPayment.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = 5861836221760229703L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				refreshVisibleContent( true );
			}
		});
		
		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		addButtonApplyFilterClickListener();

		HorizontalLayout rowQuery = new HorizontalLayout();
		//rowQuery.setWidth( "100%" );
		rowQuery.setSpacing( true );
		rowQuery.addComponent( fromDateField );
		rowQuery.addComponent( toDateField );
		rowQuery.addComponent( comboBuyer );
		rowQuery.addComponent( comboOperations );
		rowQuery.addComponent( comboProviders );
		rowQuery.addComponent( comboStatus );
		rowQuery.addComponent( comboType );
		rowQuery.addComponent( checkForPayment );
		rowQuery.addComponent( bttnApply );

		rowQuery.setComponentAlignment( checkForPayment, Alignment.BOTTOM_LEFT );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		PurchaseQuery query = new PurchaseQuery();
		
		query.setFrom_date( fromDateField.getValue() != null ? CalendarUtils.getDayAsLong( fromDateField.getValue() ) : null );
		query.setTo_date( toDateField.getValue() != null ? CalendarUtils.getDayAsLong( toDateField.getValue() ) : null );
		
		if ( comboOperations.getValue() != null )
			query.setRef_operation( (Long)comboOperations.getValue() );
		
		if ( comboProviders.getValue() != null )
			query.setRef_provider( (Long)comboProviders.getValue() );
		
		if ( comboStatus.getValue() != null )
			query.setStatus( (Integer)comboStatus.getValue() );
		
		if ( comboBuyer.getValue() != null )
			query.setRef_buyer( (Long)comboBuyer.getValue() );

		if ( comboType.getValue() != null )
			query.setPurchase_type( (Integer)comboType.getValue() );
		
		if ( checkForPayment.getValue().booleanValue()  )
			query.setFor_payment( true );
			
		saveUserDefaults();
		
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
		double totalPayed = 0;
		double totalPending = 0;
		
		for ( BaseDto row : rows )
		{
			Purchase quotation = (Purchase)row;
			
			totalNetPrice += quotation.getNet_price();
			totalTaxes += quotation.getNet_tax();
			totalGrossPrice += quotation.getGrossPrice();
			totalPayed += quotation.getPayed();
			totalPending += quotation.getForPayment();
		}
		
		labelTotalNetPrice.setValue( getContext().getString( "purchasesTab.totalNetPrice" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalNetPrice ) ) );
		labelTotalTaxes.setValue( getContext().getString( "purchasesTab.totalTaxes" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalTaxes ) ) );
		labelTotalGrossPrice.setValue( getContext().getString( "purchasesTab.totalGrossPrice" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalGrossPrice ) ) );
		labelTotalPayed.setValue( getContext().getString( "purchasesTab.totalPayed" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalPayed ) ) );
		labelTotalPending.setValue( getContext().getString( "purchasesTab.totalPending" ).replaceAll( "%total%" , Utils.getFormattedCurrency( totalPending ) ) );

		labelTotalPending.addStyleName( totalPending > 0 ? "red" : "green" );
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
			query.setType_company( Company.TYPE_PROVIDER );

			providers = IOCManager._CompaniesManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			providers = new ArrayList<Company>();
		}
	}
	
	private void fillComboProviders()
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
	
	public void refreshProviders()
	{
		loadCustomers();
		fillComboProviders();
	}

	private void loadUserDefaults()
	{
		default_from = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.PURCHASES_FROM );
		default_to = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.PURCHASES_TO );
		default_operation= IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.PURCHASES_OPERATION );
		default_provider = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.PURCHASES_PROVIDER );
		default_buyer = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.PURCHASES_BUYER );
		default_status = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.PURCHASES_STATUS );
		default_type = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.PURCHASES_TYPE);
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_from, fromDateField.getValue() != null ? Long.toString( CalendarUtils.getDayAsLong( fromDateField.getValue() ) ) : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_to, toDateField.getValue() != null ? Long.toString( CalendarUtils.getDayAsLong( toDateField.getValue() ) ) : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_provider, comboProviders.getValue() != null ? comboProviders.getValue().toString() : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_buyer, comboBuyer.getValue() != null ? comboBuyer.getValue().toString() : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_operation, comboOperations.getValue() != null ? comboOperations.getValue().toString() : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_status, comboStatus.getValue() != null ? comboStatus.getValue().toString() : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_type, comboType.getValue() != null ? comboType.getValue().toString() : null );
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
	
	private Long getDefaultProvider() 
	{
		try
		{
			return Long.parseLong( default_provider.getData_value() );
		}
		catch ( Throwable e )
		{
		}
		
		return null;
	}

	private Long getDefaultOperation() 
	{
		try
		{
			return Long.parseLong( default_operation.getData_value() );
		}
		catch ( Throwable e )
		{
		}
		
		return null;
	}

	private Long getDefaultBuyer() 
	{
		try
		{
			return Long.parseLong( default_buyer.getData_value() );
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

	private Integer getDefaultType() 
	{
		try
		{
			return Integer.parseInt( default_type.getData_value() );
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
			comboBuyer.setItemCaption( user.getId(), user.getLogin() );
		}
	}	

	private void fillComboType()
	{
		for ( int i = Purchase.TYPE_SELL; i <= Purchase.TYPE_OTHER; i++ )
		{
			comboType.addItem( i );
			comboType.setItemCaption( i, getContext().getString( "purchase.type." + i ) );
		}
	}

	@SuppressWarnings("unchecked")
	private void loadOperations()
	{
		try
		{
			OperationQuery query = new OperationQuery();

			operations = IOCManager._OperationsManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			operations = new ArrayList<Operation>();
		}
	}
	
	private void fillComboOperations()
	{
		comboOperations.removeAllItems();
		for ( Operation operation : operations )
		{
			comboOperations.addItem( operation.getId() );
			comboOperations.setItemCaption( operation.getId(), operation.getTitle() );
		}
	}

	public void refreshOperations()
	{
		loadOperations();
		fillComboOperations();
	}

	private void onShowList()
	{
		try
		{
			long ts = CalendarUtils.getTodayAsLong( "UTC" );
			
			String pagesize = "A4";
			String template = "list-purchases-template";
			String timeout = "0";
			
			PurchaseQuery query = (PurchaseQuery)getQueryObject();
			
			String extra = "ts=" + ts + 
					"&query=" + Utils.getUrlEncoded( Utils.toJson( query ) ) + 
					"&pagesize=" + pagesize + 
					"&language=" + getContext().getLanguage() +
					"&template=" + template +
					"&name=" + getContext().getString( "purchasesTab.purchases" ) + "-" + query.getPeriodToString() +
					"&url=" + getContext().getData( "Url" ) +
					"&timeout=" + timeout;
			
			String user = getContext().getUser().getLogin();
			String password = getContext().getUser().getPwd();
			
			String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
			String code = "code=" + Authorization.encrypt( extra, password ) ;

			String url = getContext().getData( "Url" ) + "/services/purchases" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
			
			String caption = getContext().getString( "purchasesTab.purchases" );

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

	private StreamResource getZipResource() 
	{
		StreamResource res = new StreamResource( 
			new StreamSource() 
			{
				private static final long serialVersionUID = -4073608207577608599L;

				@Override
	            public InputStream getStream() 
	            {
					try
					{
						return new ByteArrayInputStream( IOCManager._PurchasesManager.generateListZip( getContext(), (PurchaseQuery)getQueryObject() ) );
					}
					catch ( Throwable e )
					{
						e.printStackTrace();
					}
					
					return null;
	            }
	        }, 
	        Utils.getUUID() + ".zip" );
		
		res.setCacheTime( 0 );
		
		return res;
    }

	private StreamResource getXlsResource() 
	{
		StreamResource res =
			new StreamResource( 
				new StreamSource() 
				{
					private static final long serialVersionUID = -995277148978022912L;

					@Override
		            public InputStream getStream() 
		            {
						try
						{
							return new ByteArrayInputStream( IOCManager._PurchasesManager.exportListXls( getContext(), (PurchaseQuery)getQueryObject() ) );
						}
						catch ( Throwable e )
						{
							e.printStackTrace();
						}
						
						return null;
		            }
		        }, 
		        Utils.getUUID() + ".xls" );
		
			res.setCacheTime( 0 );
			
			return res;
	    }
}

