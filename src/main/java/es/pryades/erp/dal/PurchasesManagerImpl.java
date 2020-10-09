package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.PurchaseMapper;
import es.pryades.erp.dto.Purchase;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class PurchasesManagerImpl extends BaseManagerImpl implements PurchasesManager
{
	private static final long serialVersionUID = -3882554937723336630L;
	
	private static final Logger LOG = Logger.getLogger( PurchasesManagerImpl.class );

	public static BaseManager build()
	{
		return new PurchasesManagerImpl();
	}

	public PurchasesManagerImpl()
	{
		super( PurchaseMapper.class, Purchase.class, LOG );
	}
}
