package es.pryades.erp.dal;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.dto.query.PurchaseQuery;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface PurchasesManager extends BaseManager
{
	byte[] generateListZip( AppContext ctx, PurchaseQuery query ) throws Throwable;
}
