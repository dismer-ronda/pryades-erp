package es.pryades.erp.dal;

import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.ibatis.ShipmentBoxMapper;
import es.pryades.erp.dto.ShipmentBox;
import es.pryades.erp.dto.ShipmentBoxLine;
import es.pryades.erp.ioc.IOCManager;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class ShipmentsBoxesManagerImpl extends BaseManagerImpl implements ShipmentsBoxesManager
{
	private static final long serialVersionUID = 578137310625642944L;
	
	private static final Logger LOG = Logger.getLogger( ShipmentsBoxesManagerImpl.class );

	public static BaseManager build()
	{
		return new ShipmentsBoxesManagerImpl();
	}

	public ShipmentsBoxesManagerImpl()
	{
		super( ShipmentBoxMapper.class, ShipmentBox.class, LOG );
	}

	@Override
	public boolean setEmptyToNull()
	{
		return false;
	}
	
	@Override
	public boolean replicateBox( AppContext ctx, long id, int times ) throws BaseException
	{
		ShipmentBox query = new ShipmentBox();
		query.setId( id );
		ShipmentBox src = (ShipmentBox)getRow( ctx, query );
		
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();

		try 
		{
			for ( int i = 0; i < times; i++ )
			{
				ShipmentBox newBox = new ShipmentBox();
				
				newBox.setBox_type( src.getBox_type() );
				newBox.setLabel( src.getLabel() );
				newBox.setLabel_type( src.getLabel_type() );
				newBox.setRef_shipment( src.getRef_shipment() );
				newBox.setRef_container( src.getRef_container() );
				newBox.setWidth( src.getWidth() );
				newBox.setLength( src.getLength() );
				newBox.setHeight( src.getHeight() );
				
				newBox.setSub_boxes( new ArrayList<ShipmentBox>() );
				newBox.setLines( new ArrayList<ShipmentBoxLine>() );
				
				IOCManager._ShipmentsBoxesManager.setRow( ctx, null, newBox );
				
				for ( ShipmentBoxLine line : src.getLines()  )
				{
					ShipmentBoxLine newLine = new ShipmentBoxLine();
					
					if ( line.getInvoice_line().getTotal_packed() + line.getQuantity() <= line.getInvoice_line().getQuantity() )
					{
						newLine.setId( null );
						newLine.setRef_box( newBox.getId() );
						newLine.setRef_invoice_line( line.getRef_invoice_line() );
						
						newLine.setQuantity( line.getQuantity() );
						newLine.setNet_weight( line.getNet_weight() );
						newLine.setGross_weight( line.getGross_weight() );
	
						newLine.setInvoice_line( line.getInvoice_line() );
						
						IOCManager._ShipmentsBoxesLinesManager.setRow( ctx, null, newLine );
					}
					else
						throw new Exception( "Max item quantity exceeded" );
				}
			}
			
			if ( finish )
				session.commit();
			
			return false;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			
			if ( finish ) 
				session.rollback();
			
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}

		return false;
	}

	@Override
	public boolean canReplicateBox( AppContext ctx, long id, int times ) throws BaseException
	{
		ShipmentBox query = new ShipmentBox();
		query.setId( id );
		ShipmentBox src = (ShipmentBox)getRow( ctx, query );
		
		for ( ShipmentBoxLine line : src.getLines()  )
			if ( line.getInvoice_line().getTotal_packed() + times * line.getQuantity() > line.getInvoice_line().getQuantity() )
				return false;

		return true;
	}
}
