package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.ShipmentBoxMapper;
import es.pryades.erp.dto.ShipmentBox;

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
}
