package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.QuotationDeliveryMapper;
import es.pryades.erp.dto.QuotationDelivery;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class QuotationsDeliveriesManagerImpl extends BaseManagerImpl implements QuotationsDeliveriesManager
{
	private static final long serialVersionUID = 5304019534801282785L;
	
	private static final Logger LOG = Logger.getLogger( QuotationsDeliveriesManagerImpl.class );

	public static BaseManager build()
	{
		return new QuotationsDeliveriesManagerImpl();
	}

	public QuotationsDeliveriesManagerImpl()
	{
		super( QuotationDeliveryMapper.class, QuotationDelivery.class, LOG );
	}
}
