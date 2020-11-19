package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.ShipmentAttachmentMapper;
import es.pryades.erp.dto.ShipmentAttachment;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class ShipmentsAttachmentsManagerImpl extends BaseManagerImpl implements ShipmentsAttachmentsManager
{
	private static final long serialVersionUID = 8734425223633054098L;
	
	private static final Logger LOG = Logger.getLogger( ShipmentsAttachmentsManagerImpl.class );

	public static BaseManager build()
	{
		return new ShipmentsAttachmentsManagerImpl();
	}

	public ShipmentsAttachmentsManagerImpl()
	{
		super( ShipmentAttachmentMapper.class, ShipmentAttachment.class, LOG );
	}
}
