package es.pryades.erp.dal;

import es.pryades.erp.common.AppContext;


/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface FilesManager extends BaseManager
{
	public Long saveFile( AppContext ctx, String fileName );
	public byte [] readFile( AppContext ctx, Long id );
	public void deleteFile( AppContext ctx, Long id );
}
