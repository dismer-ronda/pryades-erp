package es.pryades.erp.configuration.tabs;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD.Operation;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.configuration.modals.ModalNewShipment;
import es.pryades.erp.configuration.modals.ModalNewShipmentBox;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.Shipment;
import es.pryades.erp.dto.ShipmentBox;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.ShipmentBoxVto;
import es.pryades.erp.vto.controlers.ShipmentBoxControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class ShipmentsBoxesConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = 4419823937209578639L;
	
	private static final Logger LOG = Logger.getLogger( ShipmentsBoxesConfig.class );
	
	private ModalNewShipment parentShipment;
	private ModalNewShipmentBox parentBox;
	
	//private ModalNewShipmentBox parent;
	
	public ShipmentsBoxesConfig( AppContext ctx, ModalParent parent )
	{
		super( ctx );
	
		setOrderby( "id" );
		setOrder( "asc" );
		
		if ( parent.getClass().equals( ModalNewShipment.class ) )
			parentShipment = (ModalNewShipment)parent;
		if ( parent.getClass().equals( ModalNewShipmentBox.class ) )
			parentBox = (ModalNewShipmentBox)parent;
	}

	@Override
	public String getResourceKey()
	{
		return "shipmentsBoxesConfig";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "label", "box_type", "length", "width", "height" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "label", "box_type", "length", "width", "height" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new BaseTable( ShipmentBoxVto.class, this, getContext() );
	}

	public List<Component> getCustomOperations()
	{
		return null;
	}

	@Override
	public Component getQueryComponent()
	{
		return null;
	}
	
	@Override
	public Query getQueryObject()
	{
		ShipmentBox query = new ShipmentBox();

		if ( parentShipment != null )
			query.setRef_shipment( parentShipment.getNewShipment().getId() );
		if ( parentBox != null )
			query.setRef_container( parentBox.getNewShipmentBox().getId() );
		
		return query;
	}

	@Override
	public void onOperationNew()
	{
		ModalParent parent = parentShipment != null ? parentShipment : parentBox;
		
		new ModalNewShipmentBox( getContext(), Operation.OP_ADD, null, parent ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		ModalParent parent = parentShipment != null ? parentShipment : parentBox;
		
		new ModalNewShipmentBox( getContext(), Operation.OP_MODIFY, (ShipmentBox)dto, parent ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new ShipmentBoxControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new ShipmentBox();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._ShipmentsBoxesManager;
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
	
	public Shipment getShipment()
	{
		if ( parentShipment != null )
			return parentShipment.getNewShipment();
		
		return parentBox.getShipment();
	}
}

