package es.pryades.erp.dal;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.dto.Transaction;
import es.pryades.erp.dto.query.TransactionQuery;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface TransactionsManager extends BaseManager
{
	byte[] exportListXls( AppContext ctx, TransactionQuery query ) throws Throwable;
	
	boolean addTransaction( AppContext ctx, Transaction payment ) throws Throwable;
}
