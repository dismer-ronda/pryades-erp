package es.pryades.erp.dal;

import java.util.List;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Constants;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Query;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
@SuppressWarnings("rawtypes")
public interface BaseManager 
{	
	public boolean hasUniqueId( AppContext ctx );

	public boolean hasBlob();

	public void setRow( AppContext ctx, BaseDto lastRow, BaseDto newRow ) throws BaseException;
	public void delRow( AppContext ctx, BaseDto row ) throws BaseException;

    public long getNumberOfRows( AppContext ctx, Query query ) throws BaseException;
	public List getRows( AppContext ctx, Query query ) throws BaseException;

	public BaseDto getLastRow( AppContext ctx, BaseDto query ) throws BaseException;
	public BaseDto getRow( AppContext ctx, BaseDto dto ) throws BaseException;
	public BaseDto getNextRow( AppContext ctx, BaseDto query ) throws BaseException;

	public void delAllRows( AppContext ctx, Query query ) throws BaseException;
	
	public boolean isLogEnabled( AppContext ctx, String action );
	public long getLogSetting();
	
	public boolean setEmptyToNull();
}
