package es.pryades.erp.common;

import es.pryades.erp.dto.Query;

/**
 * 
 * @author Dismer Ronda
 *
 */
public interface EnermetPaginatorFilter extends TablePaginator
{
	public Query getPaginatorQuery();
	public void setPaginatorQuery(Query query);
}
