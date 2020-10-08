package es.pryades.erp.configuration.tabs;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD.OperationCRUD;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.configuration.modals.ModalNewShipmentBox;
import es.pryades.erp.configuration.modals.ModalNewShipmentBoxLine;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.ShipmentBoxLine;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.ShipmentBoxLineVto;
import es.pryades.erp.vto.controlers.ShipmentBoxLineControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class ShipmentsBoxesLinesConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -7576747937964184689L;

	private static final Logger LOG = Logger.getLogger( ShipmentsBoxesLinesConfig.class );

	private ModalNewShipmentBox parent;

	public ShipmentsBoxesLinesConfig( AppContext ctx, ModalParent parent )
	{
		super( ctx );
	
		this.parent = (ModalNewShipmentBox)parent;
		
		setOrderby( "id" );
		setOrder( "asc" );
	}

	@Override
	public String getResourceKey()
	{
		return "shipmentsBoxesLinesConfig";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "title", "quantity", "net_weight", "gross_weight"  };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "quantity", "net_weight", "gross_weight" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new BaseTable( ShipmentBoxLineVto.class, this, getContext() );
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
		ShipmentBoxLine query = new ShipmentBoxLine();
		query.setRef_box( parent.getNewShipmentBox().getId() );
		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewShipmentBoxLine( getContext(), OperationCRUD.OP_ADD, null, parent ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewShipmentBoxLine( getContext(), OperationCRUD.OP_MODIFY, (ShipmentBoxLine)dto, parent ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new ShipmentBoxLineControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new ShipmentBoxLine();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._ShipmentsBoxesLinesManager;
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
}

