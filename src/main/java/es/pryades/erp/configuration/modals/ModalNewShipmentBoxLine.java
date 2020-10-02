package es.pryades.erp.configuration.modals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dashboard.Dashboard;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.dto.InvoiceLine;
import es.pryades.erp.dto.ShipmentBoxLine;
import es.pryades.erp.dto.query.InvoiceQuery;
import es.pryades.erp.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewShipmentBoxLine extends ModalWindowsCRUD implements ModalParent
{
	private static final long serialVersionUID = 6397893421188998089L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalNewShipmentBoxLine.class );

	protected ShipmentBoxLine newShipmentBoxLine;

	private ComboBox comboInvoiceLines;
	private TextField editLine_quantity;
	private TextField editGross_weight;
	private TextField editNet_weight;
	
	private List<Invoice> invoices;

	public ModalNewShipmentBoxLine( AppContext context, Operation modalOperation, ShipmentBoxLine orgParameter, ModalParent parentWindow )
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
			newShipmentBoxLine = (ShipmentBoxLine) Utils.clone( (ShipmentBoxLine) orgDto );
		}
		catch ( Throwable e1 )
		{
			newShipmentBoxLine = new ShipmentBoxLine();
			newShipmentBoxLine.setRef_box( ((ModalNewShipmentBox)getModalParent()).getNewShipmentBox().getId() );
			newShipmentBoxLine.setNet_weight( 0.0 );
			newShipmentBoxLine.setGross_weight( 0.0 );
		}

		bi = new BeanItem<BaseDto>( newShipmentBoxLine );

		loadInvoicesLines();

		editLine_quantity = new TextField( getContext().getString( "modalNewShipmentBoxLine.editQuantity" ), bi.getItemProperty( "quantity" ) );
		editLine_quantity.setWidth( "100%" );
		editLine_quantity.setNullRepresentation( "" );
		
		editGross_weight = new TextField( getContext().getString( "modalNewShipmentBoxLine.editGross_weight" ), bi.getItemProperty( "gross_weight" ) );
		editGross_weight.setWidth( "100%" );
		editGross_weight.setNullRepresentation( "" );
		
		editNet_weight = new TextField( getContext().getString( "modalNewShipmentBoxLine.editNet_weight" ), bi.getItemProperty( "net_weight" ) );
		editNet_weight.setWidth( "100%" );
		editNet_weight.setNullRepresentation( "" );
		
		comboInvoiceLines = new ComboBox(getContext().getString( "modalNewShipmentBoxLine.comboInvoiceLines" ));
		comboInvoiceLines.setWidth( "100%" );
		comboInvoiceLines.setNullSelectionAllowed( false );
		comboInvoiceLines.setTextInputAllowed( true );
		comboInvoiceLines.setImmediate( true );
		fillComboInvoicesLines();
		comboInvoiceLines.setPropertyDataSource( bi.getItemProperty( "ref_invoice_line" ) );
		comboInvoiceLines.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -1686034002879898914L;

			public void valueChange(ValueChangeEvent event) 
		    {
				setMaxQuantity();
		    }
		});
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( comboInvoiceLines );
		row1.addComponent( editLine_quantity );
		row1.addComponent( editNet_weight );
		row1.addComponent( editGross_weight );

		componentsContainer.addComponent( row1 );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewShipmentBoxLine";
	}

	@Override
	protected void defaultFocus()
	{
		editLine_quantity.focus();
	}

	private boolean checkMaxQuantity()
	{
		InvoiceLine invoiceLine = getInvoiceLine( newShipmentBoxLine.getRef_invoice_line() );

		int orgQuantity = orgDto == null ? 0 : ((ShipmentBoxLine)orgDto).getQuantity();
		int newQuantity = newShipmentBoxLine.getQuantity();
		int dif = newQuantity - orgQuantity;
		int totalPacked = invoiceLine.getTotal_packed() != null ? invoiceLine.getTotal_packed() : 0;

		if ( dif > invoiceLine.getQuantity() - totalPacked )
		{
			String error = getContext().getString( "modalNewShipmentBoxLine.quantityExceeded" ).
					replaceAll( "%title%", invoiceLine.getQuotation_line().getTitle() ).
					replaceAll( "%max_quantity%", Integer.toString( orgQuantity + invoiceLine.getQuantity() - totalPacked ) );
			Utils.showNotification( getContext(), error, Notification.Type.ERROR_MESSAGE );
			
			return false;
		}
		
		return true;
	}

	private void setMaxQuantity()
	{
		int orgQuantity = orgDto == null ? 0 : ((ShipmentBoxLine)orgDto).getQuantity();
		
		if ( orgQuantity == 0 )
		{
			InvoiceLine invoiceLine = getInvoiceLine( (Long)comboInvoiceLines.getValue() );
			
			int totalPacked = invoiceLine.getTotal_packed() != null ? invoiceLine.getTotal_packed() : 0;

			editLine_quantity.setValue( Integer.toString( invoiceLine.getQuantity() - totalPacked ) );
		}
	}

	private boolean checkWeights()
	{
		if ( newShipmentBoxLine.getGross_weight() <= newShipmentBoxLine.getNet_weight() )
		{
			String error = getContext().getString( "modalNewShipmentBoxLine.gross_net_constraint" );
			Utils.showNotification( getContext(), error, Notification.Type.ERROR_MESSAGE );
			
			return false;
		}
		
		return true;
	}

	@Override
	protected boolean onAdd()
	{
		if ( !checkMaxQuantity() )
			return false;

		if ( !checkWeights() )
			return false;

		try
		{
			newShipmentBoxLine.setId( null );
			
			IOCManager._ShipmentsBoxesLinesManager.setRow( getContext(), null, newShipmentBoxLine );
			
			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();

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
		if ( !checkMaxQuantity() )
			return false;

		if ( !checkWeights() )
			return false;

		try
		{
			IOCManager._ShipmentsBoxesLinesManager.setRow( getContext(), (ShipmentBoxLine) orgDto, newShipmentBoxLine );
			
			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			dashboard.refreshInvoicesTab();

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
			IOCManager._ShipmentsBoxesLinesManager.delRow( getContext(), newShipmentBoxLine );

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

	private void loadUserDefaults()
	{
	}

	private void saveUserDefaults()
	{
	}
	
	private InvoiceLine getInvoiceLine( Long id )
	{
		for ( Invoice invoice : invoices )
		{
			for ( InvoiceLine invoiceLine : invoice.getLines() )
				if ( invoiceLine.getId().equals( id ) )
					return invoiceLine;
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private void loadInvoicesLines()
	{
		try
		{
			InvoiceQuery query = new InvoiceQuery();
			query.setRef_shipment( ((ModalNewShipmentBox)getModalParent()).getShipment().getId() );
			
			invoices = IOCManager._InvoicesManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			
			invoices = new ArrayList<Invoice>();
		}
	}
	
	private void fillComboInvoicesLines()
	{
		for ( Invoice invoice : invoices )
		{
			for ( InvoiceLine invoiceLine : invoice.getLines() )
			{
				int totalPacked = invoiceLine.getTotal_packed() != null ? invoiceLine.getTotal_packed() : 0;
				
				if ( totalPacked < invoiceLine.getQuantity() || orgDto != null )
				{
					comboInvoiceLines.addItem( invoiceLine.getId() );
					comboInvoiceLines.setItemCaption( invoiceLine.getId(), invoice.getFormattedNumber() + " - " + invoiceLine.getQuotation_line().getTitle() );
				}
			}
		}
	}
	
	@Override
	public void refreshVisibleContent( boolean repage )
	{
		loadInvoicesLines();
		fillComboInvoicesLines();
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
