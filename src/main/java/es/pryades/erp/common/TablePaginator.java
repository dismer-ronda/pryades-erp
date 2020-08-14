package es.pryades.erp.common;

import java.util.List;

import es.pryades.erp.dto.BaseDto;

/**
 * 
 * @author Dismer Ronda
 *
 */
public interface TablePaginator 
{
	public List<BaseDto> getPageNum(Long pagNum) throws BaseException;
	public List<BaseDto> getFirstPage() throws BaseException;
	public List<BaseDto> getLastPage() throws BaseException;
	public List<BaseDto> getCurrPage() throws BaseException;
	public List<BaseDto> getNextPage() throws BaseException;
	public List<BaseDto> getPreviousPage() throws BaseException;

	public Boolean isFirstPageEnable();
	public Boolean isLastPageEnable();
	public Boolean isNextPageEnable();
	public Boolean isPreviousPageEnable();
	
	public long getCurrPag();
	public long getTotalPag();
}
