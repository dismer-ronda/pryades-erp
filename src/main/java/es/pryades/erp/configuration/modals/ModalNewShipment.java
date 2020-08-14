package es.pryades.erp.configuration.modals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

import es.pryades.erp.application.ShowExternalUrlDlg;
import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Authorization;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.configuration.tabs.ShipmentsBoxesConfig;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Shipment;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.ioc.IOCManager;
import lombok.Getter;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewShipment extends ModalWindowsCRUD implements ModalParent
{
	private static final long serialVersionUID = -4459474027851847455L;

	private static final Logger LOG = Logger.getLogger( ModalNewShipment.class );

	private List<Company> customers;
	
	@Getter
	protected Shipment newShipment;

	@Getter	private PopupDateField popupDateShipment;
	@Getter	private PopupDateField popupDateDeparture;
	
	private ComboBox comboConsignee;
	private ComboBox comboNotify;
	
	private TextField editIncoterms;
	private TextField editTitle;
	private TextArea editDescription;
	private TextField editDeparture_port;
	private TextField editArrival_port;
	private TextField editCarrier;
	private TextField editTracking;
	private ComboBox comboStatus;
	
	private UserDefault consignee;
	private UserDefault notify;
	private UserDefault incoterms;
	private UserDefault departure_port;
	private UserDefault arrival_port;
	private UserDefault carrier;
	
	private Panel panelBoxes;
	private ShipmentsBoxesConfig configBoxes;
	
	/**
	 * editReference_order
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewShipment( AppContext context, Operation modalOperation, Shipment orgParameter, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgParameter );

		setWidth( "60%" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		loadUserDefaults();
		
		try
		{
			newShipment = (Shipment) Utils.clone( (Shipment) orgDto );
		}
		catch ( Throwable e1 )
		{
			newShipment = new Shipment();
			newShipment.setShipment_date( CalendarUtils.getTodayAsLong() );
			newShipment.setDeparture_date( CalendarUtils.getTodayAsLong() );

			if ( Utils.getLong( consignee.getData_value(), 0 ) != 0)
				newShipment.setRef_consignee( Utils.getLong( consignee.getData_value(), 0 ) );
			if ( Utils.getLong( notify.getData_value(), 0 ) != 0)
				newShipment.setRef_notify( Utils.getLong( notify.getData_value(), 0 ) );
			newShipment.setIncoterms( incoterms.getData_value() );
			newShipment.setDeparture_port( departure_port.getData_value() );
			newShipment.setArrival_port( arrival_port.getData_value() );
			newShipment.setCarrier( carrier.getData_value() );
			newShipment.setStatus( Shipment.STATUS_CREATED );
		}

		layout.setHeight( "-1px" );
		
		bi = new BeanItem<BaseDto>( newShipment );

		loadCustomers();

		popupDateShipment = new PopupDateField(getContext().getString( "modalNewShipment.popupDateShipment" ));
		popupDateShipment.setResolution( Resolution.DAY );
		popupDateShipment.setDateFormat( "dd-MM-yyyy" );
		popupDateShipment.setWidth( "100%" );
		popupDateShipment.setValue( CalendarUtils.getDateFromString( Long.toString( newShipment.getShipment_date() ), "yyyyMMddHHmmss" ) );
		
		popupDateDeparture= new PopupDateField(getContext().getString( "modalNewShipment.popupDateDeparture" ));
		popupDateDeparture.setResolution( Resolution.DAY );
		popupDateDeparture.setDateFormat( "dd-MM-yyyy" );
		popupDateDeparture.setWidth( "100%" );
		popupDateDeparture.setValue( CalendarUtils.getDateFromString( Long.toString( newShipment.getDeparture_date() ), "yyyyMMddHHmmss" ) );
		
		comboConsignee = new ComboBox(getContext().getString( "modalNewShipment.comboConsignee" ));
		comboConsignee.setWidth( "100%" );
		comboConsignee.setNullSelectionAllowed( false );
		comboConsignee.setTextInputAllowed( true );
		comboConsignee.setImmediate( true );
		fillComboConsignees();
		comboConsignee.setPropertyDataSource( bi.getItemProperty( "ref_consignee" ) );

		comboNotify = new ComboBox(getContext().getString( "modalNewShipment.comboNotify" ));
		comboNotify.setWidth( "100%" );
		comboNotify.setNullSelectionAllowed( false );
		comboNotify.setTextInputAllowed( true );
		comboNotify.setImmediate( true );
		fillComboNotifies();
		comboNotify.setPropertyDataSource( bi.getItemProperty( "ref_notify" ) );

		if ( !getOperation().equals( Operation.OP_ADD ) )
		{
			comboStatus = new ComboBox(getContext().getString( "modalNewShipment.comboStatus" ));
			comboStatus.setWidth( "100%" );
			comboStatus.setNullSelectionAllowed( false );
			comboStatus.setTextInputAllowed( false );
			comboStatus.setImmediate( true );
			fillComboStatus();
			comboStatus.setPropertyDataSource( bi.getItemProperty( "status" ) );
		}
		
		editIncoterms = new TextField( getContext().getString( "modalNewShipment.editIncoterms" ), bi.getItemProperty( "incoterms" ) );
		editIncoterms.setWidth( "100%" );
		editIncoterms.setNullRepresentation( "" );

		editTitle = new TextField( getContext().getString( "modalNewShipment.editTitle" ), bi.getItemProperty( "title" ) );
		editTitle.setWidth( "100%" );
		editTitle.setNullRepresentation( "" );

		editDescription = new TextArea( getContext().getString( "modalNewShipment.editDescription" ), bi.getItemProperty( "description" ) );
		editDescription.setWidth( "100%" );
		editDescription.setNullRepresentation( "" );
		editDescription.setRows( 4 );

		editDeparture_port = new TextField( getContext().getString( "modalNewShipment.editDeparture_port" ), bi.getItemProperty( "departure_port" ) );
		editDeparture_port.setWidth( "100%" );
		editDeparture_port.setNullRepresentation( "" );

		editArrival_port= new TextField( getContext().getString( "modalNewShipment.editArrival_port" ), bi.getItemProperty( "arrival_port" ) );
		editArrival_port.setWidth( "100%" );
		editArrival_port.setNullRepresentation( "" );
		
		editCarrier = new TextField( getContext().getString( "modalNewShipment.editCarrier" ), bi.getItemProperty( "carrier" ) );
		editCarrier.setWidth( "100%" );
		editCarrier.setNullRepresentation( "" );
		
		editTracking = new TextField( getContext().getString( "modalNewShipment.editTracking" ), bi.getItemProperty( "tracking" ) );
		editTracking.setWidth( "100%" );
		editTracking.setNullRepresentation( "" );
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( editTitle );
		row1.addComponent( popupDateShipment );
		row1.addComponent( popupDateDeparture );
		
		HorizontalLayout row4 = new HorizontalLayout();
		row4.setWidth( "100%" );
		row4.setSpacing( true );
		if ( !getOperation().equals( Operation.OP_ADD ) )
			row1.addComponent( comboStatus );
		row4.addComponent( comboConsignee );
		row4.addComponent( comboNotify );
		
		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( editDescription );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( editIncoterms );
		row3.addComponent( editDeparture_port );
		row3.addComponent( editArrival_port );
		row3.addComponent( editCarrier );
		row3.addComponent( editTracking );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row4 );
		componentsContainer.addComponent( row2 );
		componentsContainer.addComponent( row3 );
		
		if ( !getOperation().equals( Operation.OP_ADD ) )
		{
			showShipmentBoxes();
			componentsContainer.addComponent( panelBoxes );
			
			Button btnPackig = new Button();
			btnPackig.setCaption( getContext().getString( "shipmentsConfig.packing" ) );
			btnPackig.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 3880706020996532024L;

				public void buttonClick( ClickEvent event )
				{
					onShowPdf();
				}
			} );
			
			getDefaultOperationsRow().addComponentAsFirst( btnPackig );
			getDefaultOperationsRow().setComponentAlignment( btnPackig, Alignment.MIDDLE_LEFT );
		}
	}

	private void showShipmentBoxes()
	{
		panelBoxes = new Panel( getContext().getString( "modalNewShipment.Boxes" ) );
		panelBoxes.setStyleName( "borderless light" );
		panelBoxes.setHeight( "400px" );

		configBoxes = new ShipmentsBoxesConfig( getContext(), this );
		configBoxes.initializeComponent();
		configBoxes.setSizeFull();
		configBoxes.setSpacing( true );
		configBoxes.getTable().setMargin( false );
		configBoxes.getOppLayout().setMargin( false );

		panelBoxes.setContent( configBoxes );
	}
	
	/*private void showQuotationDeliveries()
	{
		rowDeliveries.removeAllComponents();
		
		List<QuotationDelivery> deliveries = newQuotation.getDeliveries();
		
		Label labelDeliveries = new Label( getContext().getString( "modalNewQuotation.deliveries" ) );
		rowDeliveries.addComponent( labelDeliveries );
		rowDeliveries.setComponentAlignment( labelDeliveries, Alignment.MIDDLE_CENTER );
		
		if ( deliveries != null )
		{
			for ( QuotationDelivery delivery : deliveries )
			{
				Button b = new Button( CalendarUtils.getDateFromLongAsString( delivery.getDeparture_date(), "dd-MM-yyyy" ) );
				b.setData( delivery );
				b.addClickListener( new Button.ClickListener() 
				{

					public void buttonClick(ClickEvent event) 
		            {
						onDeliveryClick( event.getButton() );
		            }
		        });
				rowDeliveries.addComponent( b );
			}
		}
		
		Button buttonAddDelivery = new Button( "+" );
		buttonAddDelivery.addClickListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
            {
				onDeliveryAddClick();
            }
        });
		
		rowDeliveries.addComponent( buttonAddDelivery );
	}

	private void showQuotationAttachments()
	{
		rowAttachments.removeAllComponents();
		
		List<QuotationAttachment> deliveries = newQuotation.getAttachments();
		
		Label labelAttachments = new Label( getContext().getString( "modalNewQuotation.attachments" ) );
		rowAttachments.addComponent( labelAttachments );
		rowAttachments.setComponentAlignment( labelAttachments, Alignment.MIDDLE_CENTER );
		
		if ( deliveries != null )
		{
			for ( QuotationAttachment delivery : deliveries )
			{
				Button b = new Button( delivery.getTitle() );
				b.setData( delivery );
				b.addClickListener( new Button.ClickListener() 
				{
					public void buttonClick(ClickEvent event) 
		            {
						onAttachmentClick( event.getButton() );
		            }
		        });
				rowAttachments.addComponent( b );
			}
		}
		
		Button buttonAddAttachment = new Button( "+" );
		buttonAddAttachment.addClickListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
            {
				onAttachmentAddClick();
            }
        });
		
		rowAttachments.addComponent( buttonAddAttachment );
	}*/

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewShipment";
	}

	@Override
	protected void defaultFocus()
	{
		comboConsignee.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newShipment.setId( null );
			newShipment.setShipment_date( CalendarUtils.getDateAsLong( popupDateShipment.getValue() ) );
			newShipment.setDeparture_date( CalendarUtils.getDateAsLong( popupDateDeparture.getValue() ) );
			
			IOCManager._ShipmentsManager.setRow( getContext(), null, newShipment );
			
			saveUserDefaults();

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onModify()
	{
		try
		{
			newShipment.setShipment_date( CalendarUtils.getDateAsLong( popupDateShipment.getValue() ) );
			newShipment.setDeparture_date( CalendarUtils.getDateAsLong( popupDateDeparture.getValue() ) );

			IOCManager._ShipmentsManager.setRow( getContext(), (Shipment) orgDto, newShipment );

			saveUserDefaults();

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onDelete()
	{
		try
		{
			IOCManager._ShipmentsManager.delRow( getContext(), newShipment );

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
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
	
	private void fillComboConsignees()
	{
		for ( Company company : customers )
		{
			comboConsignee.addItem( company.getId() );
			comboConsignee.setItemCaption( company.getId(), company.getName() );
		}
	}

	private void fillComboNotifies()
	{
		for ( Company company : customers )
		{
			comboNotify.addItem( company.getId() );
			comboNotify.setItemCaption( company.getId(), company.getName() );
		}
	}

	private void fillComboStatus()
	{
		comboStatus.addItem( Shipment.STATUS_CREATED );
		comboStatus.setItemCaption( Shipment.STATUS_CREATED, getContext().getString( "shipment.status." + Shipment.STATUS_CREATED ) );

		comboStatus.addItem( Shipment.STATUS_SENT );
		comboStatus.setItemCaption( Shipment.STATUS_SENT, getContext().getString( "shipment.status." + Shipment.STATUS_SENT ) );

		comboStatus.addItem( Shipment.STATUS_TRANSIT );
		comboStatus.setItemCaption( Shipment.STATUS_TRANSIT, getContext().getString( "shipment.status." + Shipment.STATUS_TRANSIT ) );

		comboStatus.addItem( Shipment.STATUS_DELIVERED );
		comboStatus.setItemCaption( Shipment.STATUS_DELIVERED, getContext().getString( "shipment.status." + Shipment.STATUS_DELIVERED ) );
	}

	@Override
	public void refreshVisibleContent( boolean repage )
	{
		configBoxes.refreshVisibleContent( repage );
		
		getModalParent().refreshVisibleContent( true );
	}
	
	@Override
	protected boolean editAfterNew()
	{
		return true;
	}

	@Override
	protected void doEditAfterNew()
	{
		new ModalNewShipment( getContext(), Operation.OP_MODIFY, (Shipment)newShipment, getModalParent() ).showModalWindow();
	}

	private void loadUserDefaults()
	{
		consignee = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.SHIPMENT_CONSIGNEE );
		notify = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.SHIPMENT_NOTIFY );
		incoterms = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.SHIPMENT_INCOTERMS );
		departure_port = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.SHIPMENT_DEPARTURE_PORT );
		arrival_port = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.SHIPMENT_ARRIVAL_PORT );
		carrier = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.SHIPMENT_CARRIER );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), consignee, newShipment.getRef_consignee().toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), notify, newShipment.getRef_notify().toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), incoterms, newShipment.getIncoterms() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), departure_port, newShipment.getDeparture_port() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), arrival_port, newShipment.getArrival_port() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), carrier, newShipment.getCarrier() );
	}
	
	@Override
	protected boolean hasDelete()
	{
		return getContext().hasRight( "configuration.shipments.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.shipments.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.shipments.modify" );
	}
	
	public void onShowPdf()
	{
		try
		{
			long ts = CalendarUtils.getTodayAsLong( "UTC" );
			
			String pagesize = "A4";
			String template = newShipment.getConsignee_taxable().booleanValue() ? "national-packing-template" : "international-packing-template";
			String timeout = "0";
			
			String extra = "ts=" + ts + 
							"&id=" + newShipment.getId() + 
							"&pagesize=" + pagesize + 
							"&template=" + template +
							"&url=" + getContext().getData( "Url" ) +
							"&timeout=" + timeout;
			
			String user = getContext().getUser().getLogin();
			String password = getContext().getUser().getPwd();
			
			String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
			String code = "code=" + Authorization.encrypt( extra, password ) ;

			String url = getContext().getData( "Url" ) + "/services/packing" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
			
			String caption = getContext().getString( "template.shipment.packing" ) + " " + newShipment.getFormattedNumber() ;

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
