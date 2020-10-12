package es.pryades.erp.dal;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.dto.query.InvoiceQuery;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface InvoicesManager extends BaseManager
{
	byte[] generatePdf( AppContext ctx, Invoice invoice ) throws Throwable;
	byte[] generateListZip( AppContext ctx, InvoiceQuery query ) throws Throwable;
	byte[] exportListXls( AppContext ctx, InvoiceQuery query ) throws Throwable;
}
