package es.pryades.erp.dal;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.dto.Invoice;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface InvoicesManager extends BaseManager
{
	public boolean duplicateInvoice( AppContext ctx, Invoice src ) throws Throwable;
}
