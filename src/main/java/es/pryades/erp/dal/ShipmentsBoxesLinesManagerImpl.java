package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.ShipmentBoxLineMapper;
import es.pryades.erp.dto.ShipmentBoxLine;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class ShipmentsBoxesLinesManagerImpl extends BaseManagerImpl implements ShipmentsBoxesLinesManager
{
	private static final long serialVersionUID = 5537725016279038274L;
	
	private static final Logger LOG = Logger.getLogger( ShipmentsBoxesLinesManagerImpl.class );

	public static BaseManager build()
	{
		return new ShipmentsBoxesLinesManagerImpl();
	}

	public ShipmentsBoxesLinesManagerImpl()
	{
		super( ShipmentBoxLineMapper.class, ShipmentBoxLine.class, LOG );
	}
}
