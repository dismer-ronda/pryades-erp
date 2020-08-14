package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.QuotationAttachmentMapper;
import es.pryades.erp.dto.QuotationAttachment;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class QuotationsAttachmentsManagerImpl extends BaseManagerImpl implements QuotationsAttachmentsManager
{
	private static final long serialVersionUID = -2524010581724000346L;
	
	private static final Logger LOG = Logger.getLogger( QuotationsAttachmentsManagerImpl.class );

	public static BaseManager build()
	{
		return new QuotationsAttachmentsManagerImpl();
	}

	public QuotationsAttachmentsManagerImpl()
	{
		super( QuotationAttachmentMapper.class, QuotationAttachment.class, LOG );
	}
}
