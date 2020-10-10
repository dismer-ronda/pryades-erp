package es.pryades.erp.configuration.modals;

import org.apache.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.QuotationDelivery;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.ioc.IOCManager;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import lombok.Getter;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewQuotationDelivery extends ModalWindowsCRUD
{
	private static final long serialVersionUID = 5110206143152673052L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalNewQuotationDelivery.class );

	protected QuotationDelivery newQuotation;

	@Getter	private PopupDateField poputDepartureDate;
	
	private TextField editDeparture_port;
	private TextField editArrival_port;
	private TextField editIncoterms;
	private TextField editCost;
	private CheckBox checkFree_delivery;

	private UserDefault defaultDeparture;
	private UserDefault defaultIncoterms;
	private UserDefault defaultCost;

	/**
	 * editReference_order
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewQuotationDelivery( AppContext context, OperationCRUD modalOperation, QuotationDelivery orgParameter, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgParameter );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		loadUserDefaults();

		try
		{
			newQuotation = (QuotationDelivery) Utils.clone( (QuotationDelivery) orgDto );
		}
		catch ( Throwable e1 )
		{
			newQuotation = new QuotationDelivery();
			newQuotation.setDeparture_date( CalendarUtils.getTodayAsLong() );
			newQuotation.setRef_quotation( ((ModalNewQuotation)getModalParent()).getNewQuotation().getId() );
			newQuotation.setFree_delivery( Boolean.FALSE );

			newQuotation.setDeparture_port( defaultDeparture.getData_value() );
			newQuotation.setIncoterms( defaultIncoterms.getData_value() );
			newQuotation.setCost( Utils.getDouble( defaultCost.getData_value(), 0.0 ) );
		}

		bi = new BeanItem<BaseDto>( newQuotation );

		poputDepartureDate = new PopupDateField(getContext().getString( "modalNewQuotationDelivery.popupDepartureDate" ));
		poputDepartureDate.setResolution( Resolution.DAY );
		poputDepartureDate.setDateFormat( "dd-MM-yyyy" );
		poputDepartureDate.setWidth( "100%" );
		poputDepartureDate.setValue( CalendarUtils.getDateFromString( Long.toString( newQuotation.getDeparture_date() ), "yyyyMMddHHmmss" ) );
		
		editDeparture_port = new TextField( getContext().getString( "modalNewQuotationDelivery.editDeparture_port" ), bi.getItemProperty( "departure_port" ) );
		editDeparture_port.setWidth( "100%" );
		editDeparture_port.setNullRepresentation( "" );
		
		editArrival_port = new TextField( getContext().getString( "modalNewQuotationDelivery.editArrival_port" ), bi.getItemProperty( "arrival_port" ) );
		editArrival_port .setWidth( "100%" );
		editArrival_port .setNullRepresentation( "" );
		
		editIncoterms = new TextField( getContext().getString( "modalNewQuotationDelivery.editIncoterms" ), bi.getItemProperty( "incoterms" ) );
		editIncoterms.setWidth( "100%" );
		editIncoterms.setNullRepresentation( "" );
		
		editCost = new TextField( getContext().getString( "modalNewQuotationDelivery.editCost" ), bi.getItemProperty( "cost" ) );
		editCost.setWidth( "100%" );
		editCost.setRequired( true );
		editCost.setNullRepresentation( "" );

		checkFree_delivery = new CheckBox( getContext().getString( "modalNewQuotationDelivery.checkFree_delivery" ) );
		checkFree_delivery.setWidth( "100%" );
		checkFree_delivery.setValue( newQuotation.getFree_delivery() );

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( poputDepartureDate );
		row1.addComponent( editDeparture_port );
		row1.addComponent( editArrival_port );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( editIncoterms );
		row2.addComponent( editCost );
		row2.addComponent( checkFree_delivery );
		row2.setComponentAlignment( checkFree_delivery, Alignment.BOTTOM_LEFT );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewQuotationDelivery";
	}

	@Override
	protected void defaultFocus()
	{
		poputDepartureDate.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newQuotation.setId( null );
			newQuotation.setDeparture_date( CalendarUtils.getDayAsLong( poputDepartureDate.getValue() ) );
			newQuotation.setFree_delivery( checkFree_delivery.getValue().booleanValue() ? Boolean.TRUE : Boolean.FALSE );

			IOCManager._QuotationsDeliveriesManager.setRow( getContext(), null, newQuotation );

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
			newQuotation.setDeparture_date( CalendarUtils.getDayAsLong( poputDepartureDate.getValue() ) );
			newQuotation.setFree_delivery( checkFree_delivery.getValue().booleanValue() ? Boolean.TRUE : Boolean.FALSE );

			IOCManager._QuotationsDeliveriesManager.setRow( getContext(), (QuotationDelivery) orgDto, newQuotation );

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
			IOCManager._QuotationsDeliveriesManager.delRow( getContext(), newQuotation );

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	private void loadUserDefaults()
	{
		defaultDeparture = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_DELIVERY_DEPARTURE );
		defaultIncoterms = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_DELIVERY_INCOTERMS);
		defaultCost = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.QUOTATION_DELIVERY_COST);
	}

	private void saveUserDefaults()
	{
		if ( newQuotation.getDeparture_port() != null )
			IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultDeparture, newQuotation.getDeparture_port().toString() );
		if ( newQuotation.getIncoterms() != null )
			IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultIncoterms, newQuotation.getIncoterms().toString() );
		if ( newQuotation.getCost() != null )
			IOCManager._UserDefaultsManager.setUserDefault( getContext(), defaultCost, newQuotation.getCost().toString() );
	}

	@Override
	protected boolean hasDelete()
	{
		return getContext().hasRight( "configuration.quotations.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.quotations.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.quotations.modify" );
	}
}
