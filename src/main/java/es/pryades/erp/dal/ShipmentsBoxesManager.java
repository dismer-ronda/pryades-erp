package es.pryades.erp.dal;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface ShipmentsBoxesManager extends BaseManager
{
	public boolean replicateBox( AppContext ctx, long ref_box, int times ) throws BaseException;
	public boolean canReplicateBox( AppContext ctx, long ref_box, int times ) throws BaseException;
}
