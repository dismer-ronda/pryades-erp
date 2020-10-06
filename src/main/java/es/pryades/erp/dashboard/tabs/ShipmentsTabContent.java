package es.pryades.erp.dashboard.tabs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD.Operation;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.configuration.modals.ModalNewShipment;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.Shipment;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.dto.query.ShipmentQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.ShipmentVto;
import es.pryades.erp.vto.controlers.ShipmentControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class ShipmentsTabContent extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -1428050637047294831L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ShipmentsTabContent.class );

	private PopupDateField fromDateField;
	private PopupDateField toDateField;

	private ComboBox comboCustomers;
	private ComboBox comboStatus;

	private Button bttnApply;
	
	private List<Company> customers;

	public ShipmentsTabContent( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "shipment_date" );
		setOrder( "desc" );
	}

	@Override
	public String getResourceKey()
	{
		return "shipmentsConfig";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "departure_date", "number", "title", "status", "consignee_name", "incoterms", "carrier", "tracking" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "departure_date", "number", "title", "status", "consignee_name", "incoterms", "carrier", "tracking" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new BaseTable( ShipmentVto.class, this, getContext() );
	}

	@Override
	public Component getQueryComponent()
	{
		loadCustomers();

		fromDateField = new PopupDateField( getContext().getString( "words.from" ) );
		fromDateField.setResolution( Resolution.DAY );
		fromDateField.setDateFormat( "dd-MM-yyyy" );
		fromDateField.setValue( null );
		fromDateField.setWidth( "160px" );
		
		toDateField = new PopupDateField( getContext().getString( "words.to" ) );
		toDateField.setResolution( Resolution.DAY );
		toDateField.setDateFormat( "dd-MM-yyyy" );
		toDateField.setValue( null );
		toDateField.setWidth( "160px" );
		
		comboCustomers = new ComboBox(getContext().getString( "shipmentsConfig.comboCustomer" ));
		comboCustomers.setWidth( "100%" );
		comboCustomers.setNullSelectionAllowed( true );
		comboCustomers.setTextInputAllowed( true );
		comboCustomers.setImmediate( true );
		fillComboCustomers();
		comboCustomers.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -2350554224827448768L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});

		comboStatus = new ComboBox(getContext().getString( "shipmentsConfig.comboStatus" ));
		comboStatus.setWidth( "100%" );
		comboStatus.setNullSelectionAllowed( true );
		comboStatus.setTextInputAllowed( false );
		comboStatus.setImmediate( true );
		fillComboStatus();
		comboStatus.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -8269365509732986488L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});
		
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
		rowQuery.addComponent( comboStatus );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		ShipmentQuery query = new ShipmentQuery();
		
		query.setFrom_date( fromDateField.getValue() != null ? CalendarUtils.getDateAsLong( fromDateField.getValue() ) : null );
		query.setTo_date( toDateField.getValue() != null ? CalendarUtils.getDateAsLong( toDateField.getValue() ) : null );
		
		if ( comboCustomers.getValue() != null )
			query.setRef_consignee( (Long)comboCustomers.getValue() );
		
		if ( comboStatus.getValue() != null )
			query.setStatus( (Integer)comboStatus.getValue() );
			
		query.setRef_user( getContext().getUser().getId() );

		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewShipment( getContext(), Operation.OP_ADD, null, ShipmentsTabContent.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewShipment( getContext(), Operation.OP_MODIFY, (Shipment)dto, ShipmentsTabContent.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new ShipmentControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Shipment();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._ShipmentsManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
	}
	
	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.shipments.add" );
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
			private static final long serialVersionUID = -338935594936252911L;

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

	public void refreshCustomers()
	{
		loadCustomers();
		fillComboCustomers();
	}
}

