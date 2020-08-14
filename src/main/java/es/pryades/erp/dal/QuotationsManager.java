package es.pryades.erp.dal;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.dto.Quotation;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface QuotationsManager extends BaseManager
{
	public boolean duplicateQuotation( AppContext ctx, Quotation src ) throws Throwable;
}
