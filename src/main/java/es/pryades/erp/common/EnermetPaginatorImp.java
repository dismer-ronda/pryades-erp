package es.pryades.erp.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Query;
import es.pryades.erp.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 *
 */
@SuppressWarnings("unused")
public class EnermetPaginatorImp implements EnermetPaginatorFilter, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8835359137380243584L;

	private static final Logger LOG = Logger.getLogger( EnermetPaginatorImp.class ); 
	
	protected Integer pagSize;
	protected Long currPag;
	protected Long totalPag;
	
	private VtoControllerFactory factory;
	
	protected Query query;

	protected AppContext ctx;
	
	boolean firstPageEnable;
	boolean lastPageEnable;
	boolean nextPageEnable;
	boolean previousPageEnable;
	
	/**
	 * 
	 * @param vtoDataRef
	 * @param ctx
	 * @param resource
	 */
	public EnermetPaginatorImp( VtoControllerFactory factory, AppContext ctx)
	{
		this.factory = factory; 
		this.ctx = ctx;
		
		currPag = 1L;
		pagSize = -1;
		totalPag = -1L;

		firstPageEnable = false;
		lastPageEnable = false;
		nextPageEnable = false;
		previousPageEnable = false;
	}
	
	/**
	 * 
	 * @param pagSize
	 * @param vtoDataRef
	 * @param queryfilterRef
	 * @param ctx
	 * @param resource
	 */
	public EnermetPaginatorImp (Integer pagSize, VtoControllerFactory factory, AppContext ctx)
	{
		this.pagSize = pagSize;
		this.factory = factory; 
		this.ctx = ctx;

		currPag = 1L;
		totalPag = -1L;
		
		firstPageEnable = false;
		lastPageEnable = false;
		nextPageEnable = false;
		previousPageEnable = false;
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public Query getPaginatorQuery()
	{
		return query;
	}
	
	/**
	 * 
	 * 
	 */
	@Override
	public void setPaginatorQuery(Query query )
	{
		this.query = query;
		totalPag = getTotalPages();
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	@Override
	public List<BaseDto>  getPageNum(Long pagNum) throws BaseException 
	{
		if(pagNum == 1){
			return getFirstPage();
		}
		else if(pagNum == totalPag)
		{
			return getLastPage(); 
		}
		else
		{
			currPag = pagNum;
			return getCurrPage();
		}
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<BaseDto>  getFirstPage() throws BaseException
	{
		List<BaseDto>  result = null; 
		
		currPag = 1L;
		query.setPageNumber(currPag);
		query.setPageSize(pagSize);

		result = retriveValuesFromInstanceDB(1);	
		
		return result;
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<BaseDto>  getLastPage() throws BaseException
	{
		currPag = totalPag;
		query.setPageNumber(currPag);
		query.setPageSize(pagSize);
		
		if(currPag == 1)
			return retriveValuesFromInstanceDB(1); //code currPage number
		else
			return retriveValuesFromInstanceDB(3); //code lastPage
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<BaseDto>  getCurrPage() throws BaseException 
	{
		query.setPageNumber(currPag);
		query.setPageSize(pagSize);
		
		if(currPag == 1)
			return retriveValuesFromInstanceDB(1); //code firstPage
		else
			return retriveValuesFromInstanceDB(2); //code currPage number
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<BaseDto>  getNextPage() throws BaseException 
	{
		currPag++;
		query.setPageNumber(currPag);
		query.setPageSize(pagSize);
		
		if(currPag == 1)
			return retriveValuesFromInstanceDB(1); //code firstPage
		else if(currPag == totalPag)
			return retriveValuesFromInstanceDB(3); //code last number
		else
			return retriveValuesFromInstanceDB(2); //code currPage number
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public List<BaseDto>  getPreviousPage() throws BaseException 
	{
		currPag--;
		query.setPageNumber(currPag);
		query.setPageSize(pagSize);
		
		if(currPag == 1)
			return retriveValuesFromInstanceDB(1); //code firstPage
		else if(currPag == totalPag)
			return retriveValuesFromInstanceDB(3); //code last number
		else
			return retriveValuesFromInstanceDB(2); //code currPage number
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isFirstPageEnable() 
	{
		return firstPageEnable;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isLastPageEnable() 
	{
		return lastPageEnable;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isNextPageEnable()
	{
		return nextPageEnable;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	@Override
	public Boolean isPreviousPageEnable() 
	{
		return previousPageEnable;
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public long getCurrPag() 
	{
		return (totalPag > 0 ? currPag.longValue() : 0);
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public long getTotalPag() {
		return totalPag.longValue();
	}

	/**
	 * 
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("unchecked")
	private List<BaseDto> retriveValuesFromInstanceDB(Integer pageCode) throws BaseException
	{
		List<BaseDto>  listaElementos = null;
		
		query.setPageSize(pagSize);
		
		if(totalPag > 0) // conozco el total de paginas
		{
			switch(pageCode)
			{
			
				case 1: // firstPage
						query.setPageNumber(1L);
						listaElementos = factory.getFieldManagerImp().getRows(ctx, query);
						factory.preProcessRows(listaElementos);
						
						updatePageAvaibility();	
						
						if(listaElementos == null || listaElementos.size()==0)
						{
							firstPageEnable = false;
							lastPageEnable = false;
							nextPageEnable = false;
							previousPageEnable = false;
						}
					break;
				case 2: // page Number
						query.setPageNumber(currPag);
						listaElementos = factory.getFieldManagerImp().getRows(ctx, query);
						factory.preProcessRows(listaElementos);
	
						updatePageAvaibility();	
						
						if(listaElementos == null || listaElementos.size()==0)
						{
							firstPageEnable = false;
							lastPageEnable = false;
							nextPageEnable = false;
							previousPageEnable = false;
						}
					break;
				case 3: // last page
						query.setPageNumber(totalPag);
						listaElementos = factory.getFieldManagerImp().getRows(ctx, query);
						factory.preProcessRows(listaElementos);
	
						updatePageAvaibility();	
						
						if(listaElementos == null || listaElementos.size()==0)
						{
							firstPageEnable = false;
							lastPageEnable = false;
							nextPageEnable = false;
							previousPageEnable = false;
						}
					break;
			}
		}
		else // no conozco el total de paginas
		{
			switch(pageCode)
			{
				case 1: // firstPage
					query.setPageNumber(1L);
					listaElementos = factory.getFieldManagerImp().getRows(ctx, query);
					factory.preProcessRows(listaElementos);

					updatePageAvaibility();	
					
					if(listaElementos == null || listaElementos.size()==0)
					{
						firstPageEnable = false;
						lastPageEnable = false;
						nextPageEnable = false;
						previousPageEnable = false;
					}
				break;
					case 2: // page Number
						query.setPageNumber(currPag);
						listaElementos = factory.getFieldManagerImp().getRows(ctx, query);
						factory.preProcessRows(listaElementos);

						updatePageAvaibility();	
						
						if(listaElementos == null || listaElementos.size()==0)
						{
							firstPageEnable = false;
							lastPageEnable = false;
							nextPageEnable = false;
							previousPageEnable = false;
						}
					break;
			}
		}
		
		return (listaElementos == null ? new ArrayList<BaseDto>() : listaElementos);
	}

//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * 
	 */
	private void updatePageAvaibility()
	{
		if(pagSize < 1 )
		{
			firstPageEnable = false;
			lastPageEnable = false;
			nextPageEnable = false;
			previousPageEnable = false;
		}
		else
		{
			if(currPag > 1)
			{
				firstPageEnable = true;
				previousPageEnable = true;
			}
			else
			{
				firstPageEnable = false;
				previousPageEnable = false;
			}
			
			
			if(currPag == totalPag)
			{
				lastPageEnable = false;
				nextPageEnable = false;
			}
			else
			{
				lastPageEnable = true;
				nextPageEnable = true;
			}
		}
	}
	
//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	private Long getTotalPages()
	{
		Long result = 0L;
		Long totalRows = 0L;
		
		try 
		{
			query.setPageSize(pagSize);
			totalRows = factory.getFieldManagerImp().getNumberOfRows(ctx, query);
			
			if(totalRows > 0)
			{
				if(pagSize > 0)
				{
					Long div = totalRows/(long)pagSize;
					Long divMod = totalRows % (long)pagSize;
					
					if(divMod != 0)
						div++;
					
					result = div;
				}
				else
				{
					result = 0L;
				}
			}
			else
			{
				result = 0L;
			}
		} 
		catch ( Throwable e )
		{
			totalPag = -1L;
		}
		
		return result;
	}
	
//------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------
	
}
