package es.pryades.erp.configuration.modals;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.configuration.tabs.ShipmentsBoxesConfig;
import es.pryades.erp.configuration.tabs.ShipmentsBoxesLinesConfig;
import es.pryades.erp.dashboard.Dashboard;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Shipment;
import es.pryades.erp.dto.ShipmentBox;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.ioc.IOCManager;
import lombok.Getter;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewShipmentBox extends ModalWindowsCRUD implements ModalParent
{
	private static final long serialVersionUID = 137804086963054862L;

	private static final Logger LOG = Logger.getLogger( ModalNewShipmentBox.class );

	private Shipment parentShipment;
	private ShipmentBox parentBox;
	
	@Getter
	protected ShipmentBox newShipmentBox;

	private ComboBox comboTypes;
	private TextField editLength;
	private TextField editWidth;
	private TextField editHeight;
	private TextField editLabel;
	
	private Panel panelBoxes;
	private ShipmentsBoxesConfig configBoxes;

	private Panel panelLines;
	private ShipmentsBoxesLinesConfig configLines;

	private boolean dirty;
	
	private UserDefault type;
	private UserDefault length;
	private UserDefault width;
	private UserDefault height;
	
	public ModalNewShipmentBox( AppContext context, Operation modalOperation, ShipmentBox orgParameter, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgParameter );

		setWidth( "50%" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		loadUserDefaults();
		
		if ( getModalParent().getClass().equals( ModalNewShipment.class ) )
		{
			parentShipment = ((ModalNewShipment)getModalParent()).getNewShipment();
		}
		else if ( getModalParent().getClass().equals( ModalNewShipmentBox.class ) )
		{
			parentBox = ((ModalNewShipmentBox)getModalParent()).getNewShipmentBox();
		}

		try
		{
			newShipmentBox = (ShipmentBox) Utils.clone( (ShipmentBox) orgDto );
		}
		catch ( Throwable e1 )
		{
			newShipmentBox = new ShipmentBox();
			
			if ( parentShipment != null )
				newShipmentBox.setRef_shipment( parentShipment.getId() );
			else if ( parentBox != null )
				newShipmentBox.setRef_container( parentBox.getId() );
			newShipmentBox.setLength( Utils.getDouble( length.getData_value(), 0 ) );
			newShipmentBox.setWidth( Utils.getDouble( width.getData_value(), 0 ) );
			newShipmentBox.setHeight( Utils.getDouble( height.getData_value(), 0 ) );
		}

		layout.setHeight( "-1px" );
		
		dirty = false;

		bi = new BeanItem<BaseDto>( newShipmentBox );

		if ( getOperation().equals( Operation.OP_ADD ) )
		{
			comboTypes = new ComboBox(getContext().getString( "modalNewShipmentBox.comboType" ));
			comboTypes.setWidth( "100%" );
			comboTypes.setNullSelectionAllowed( false );
			comboTypes.setTextInputAllowed( false );
			comboTypes.setImmediate( true );
			fillComboTypes();
			comboTypes.setPropertyDataSource( bi.getItemProperty( "box_type" ) );
		}
		
		editLength = new TextField( getContext().getString( "modalNewShipmentBox.editLength" ), bi.getItemProperty( "length" ) );
		editLength.setWidth( "100%" );
		editLength.setNullRepresentation( "" );

		editWidth = new TextField( getContext().getString( "modalNewShipmentBox.editWidth" ), bi.getItemProperty( "width" ) );
		editWidth.setWidth( "100%" );
		editWidth.setNullRepresentation( "" );

		editHeight = new TextField( getContext().getString( "modalNewShipmentBox.editHeight" ), bi.getItemProperty( "height" ) );
		editHeight.setWidth( "100%" );
		editHeight.setNullRepresentation( "" );

		editLabel = new TextField( getContext().getString( "modalNewShipmentBox.editLabel" ), bi.getItemProperty( "label" ) );
		editLabel.setWidth( "100%" );
		editLabel.setNullRepresentation( "" );

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( editLabel );
		if ( getOperation().equals( Operation.OP_ADD ) )
			row1.addComponent( comboTypes );
		row1.addComponent( editLength );
		row1.addComponent( editWidth );
		row1.addComponent( editHeight );
		
		componentsContainer.addComponent( row1 );
		
		if ( !getOperation().equals( Operation.OP_ADD ) )
		{
			if ( newShipmentBox.getBox_type() < ShipmentBox.TYPE_CARDBOARD_BOX )
			{
				showBoxSubBoxes();
				componentsContainer.addComponent( panelBoxes );
			}

			if ( newShipmentBox.getBox_type() >= ShipmentBox.TYPE_CARDBOARD_BOX )
			{
				showBoxLines();
				componentsContainer.addComponent( panelLines );
			}
		}
	}

	private void showBoxSubBoxes()
	{
		panelBoxes = new Panel( getContext().getString( "modalNewShipmentBox.subBoxes" ) );
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
	
	private void showBoxLines()
	{
		panelLines = new Panel( getContext().getString( "modalNewShipmentBox.lines" ) );
		panelLines.setStyleName( "borderless light" );
		panelLines.setHeight( "400px" );

		configLines = new ShipmentsBoxesLinesConfig( getContext(), this );
		configLines.initializeComponent();
		configLines.setSizeFull();
		configLines.setSpacing( true );
		configLines.getTable().setMargin( false );
		configLines.getOppLayout().setMargin( false );

		panelLines.setContent( configLines );
	}
	
	/*private void showQuotationLines()
	{
		panelLines = new Panel();
		panelLines.setStyleName( "borderless light" );
		panelLines.setHeight( "400px" );

		configBoxes = new ShipmentsBoxesConfig( getContext(), this );
		configBoxes.initializeComponent();
		configBoxes.setSizeFull();
		configBoxes.setSpacing( true );
		configBoxes.getTable().setMargin( false );
		configBoxes.getOppLayout().setMargin( false );

		panelLines.setContent( configBoxes );
	}
	
	private void showQuotationDeliveries()
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
		return "modalNewShipmentBox";
	}

	@Override
	protected void defaultFocus()
	{
		editLength.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newShipmentBox.setId( null );
			
			IOCManager._ShipmentsBoxesManager.setRow( getContext(), null, newShipmentBox );
			
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
			IOCManager._ShipmentsBoxesManager.setRow( getContext(), (ShipmentBox) orgDto, newShipmentBox );

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
			IOCManager._ShipmentsBoxesManager.delRow( getContext(), newShipmentBox );

			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	private void fillComboTypes()
	{
		if ( parentShipment != null )
		{
			comboTypes.addItem( ShipmentBox.TYPE_CONTAINER );
			comboTypes.setItemCaption( ShipmentBox.TYPE_CONTAINER, getContext().getString( "shipment.box.type." + ShipmentBox.TYPE_CONTAINER ) );
	
			comboTypes.addItem( ShipmentBox.TYPE_PALLET );
			comboTypes.setItemCaption( ShipmentBox.TYPE_PALLET, getContext().getString( "shipment.box.type." + ShipmentBox.TYPE_PALLET ) );
	
			comboTypes.addItem( ShipmentBox.TYPE_WOOD_BOX );
			comboTypes.setItemCaption( ShipmentBox.TYPE_WOOD_BOX, getContext().getString( "shipment.box.type." + ShipmentBox.TYPE_WOOD_BOX) );
	
			comboTypes.addItem( ShipmentBox.TYPE_CARDBOARD_BOX );
			comboTypes.setItemCaption( ShipmentBox.TYPE_CARDBOARD_BOX, getContext().getString( "shipment.box.type." + ShipmentBox.TYPE_CARDBOARD_BOX ) );

			comboTypes.addItem( ShipmentBox.TYPE_BULK );
			comboTypes.setItemCaption( ShipmentBox.TYPE_BULK, getContext().getString( "shipment.box.type." + ShipmentBox.TYPE_BULK ) );
		}
		else
		{
			for ( int i = parentBox.getBox_type() + 1; i <= ShipmentBox.TYPE_BULK; i++ )
			{
				comboTypes.addItem( i );
				comboTypes.setItemCaption( i, getContext().getString( "shipment.box.type." + i ) );
			}
		}
	}

	private void reloadShipmentBox()
	{
		try
		{
			ShipmentBox query = new ShipmentBox();
			query.setId( newShipmentBox.getId() );
			
			newShipmentBox = (ShipmentBox)IOCManager._ShipmentsBoxesManager.getRow( getContext(), query );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
	
	@Override
	public void refreshVisibleContent( boolean repage )
	{
		//reloadShipmentBox();
		
		if ( configBoxes != null )
			configBoxes.refreshVisibleContent( repage );
		
		if ( configLines != null )
			configLines.refreshVisibleContent( repage );
		
		dirty = true;
		
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
		new ModalNewShipmentBox( getContext(), Operation.OP_MODIFY, (ShipmentBox)newShipmentBox, getModalParent() ).showModalWindow();
	}

	private void loadUserDefaults()
	{
		type = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.SHIPMENT_BOX_TYPE );
		length = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.SHIPMENT_BOX_LENGTH );
		width = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.SHIPMENT_BOX_WIDTH );
		height = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.SHIPMENT_BOX_HEIGHT );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), type, newShipmentBox.getBox_type().toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), length, newShipmentBox.getLength().toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), width, newShipmentBox.getWidth().toString() );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), height, newShipmentBox.getHeight().toString() );
	}
	
	/*private void onDeliveryClick( Button button )
	{
		new ModalNewQuotationDelivery( getContext(), Operation.OP_MODIFY, (QuotationDelivery)button.getData(), this ).showModalWindow();
	}
	
	private void onDeliveryAddClick()
	{
		new ModalNewQuotationDelivery( getContext(), Operation.OP_ADD, null, this ).showModalWindow();
	}

	private void onAttachmentClick( Button button )
	{
		new ModalNewQuotationAttachment( getContext(), Operation.OP_MODIFY, (QuotationAttachment)button.getData(), this ).showModalWindow();
	}
	
	private void onAttachmentAddClick()
	{
		new ModalNewQuotationAttachment( getContext(), Operation.OP_ADD, null, this ).showModalWindow();
	}*/
	
	public Shipment getShipment()
	{
		if ( getModalParent().getClass().equals( ModalNewShipment.class ) )
			return parentShipment;
		
		return ((ModalNewShipmentBox)getModalParent()).getShipment();
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
}
