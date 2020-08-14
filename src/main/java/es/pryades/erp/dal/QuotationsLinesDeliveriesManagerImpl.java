package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.QuotationLineDeliveryMapper;
import es.pryades.erp.dto.QuotationLineDelivery;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class QuotationsLinesDeliveriesManagerImpl extends BaseManagerImpl implements QuotationsLinesDeliveriesManager
{
	private static final long serialVersionUID = -87468798146229820L;
	
	private static final Logger LOG = Logger.getLogger( QuotationsLinesDeliveriesManagerImpl.class );

	public static BaseManager build()
	{
		return new QuotationsLinesDeliveriesManagerImpl();
	}

	public QuotationsLinesDeliveriesManagerImpl()
	{
		super( QuotationLineDeliveryMapper.class, QuotationLineDelivery.class, LOG );
	}
}
