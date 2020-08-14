package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.ShipmentMapper;
import es.pryades.erp.dto.Shipment;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class ShipmentsManagerImpl extends BaseManagerImpl implements ShipmentsManager
{
	private static final long serialVersionUID = 6036350353726846646L;

	private static final Logger LOG = Logger.getLogger( ShipmentsManagerImpl.class );

	public static BaseManager build()
	{
		return new ShipmentsManagerImpl();
	}

	public ShipmentsManagerImpl()
	{
		super( ShipmentMapper.class, Shipment.class, LOG );
	}

	@Override
	public boolean setEmptyToNull()
	{
		return false;
	}
}
