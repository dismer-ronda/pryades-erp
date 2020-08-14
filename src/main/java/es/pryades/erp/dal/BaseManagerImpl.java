package es.pryades.erp.dal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.apache.ibatis.session.SqlSession;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.Constants;
import es.pryades.erp.common.Settings;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.ibatis.BaseMapper;
import es.pryades.erp.dto.Audit;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.Query;
import es.pryades.erp.ioc.IOCManager;

import org.apache.log4j.Logger;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
@SuppressWarnings( {"unchecked","rawtypes", "unused"} )
@Data
public abstract class BaseManagerImpl implements BaseManager, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4196236496172454400L;
	Class mapperClass;
	Class dtoClass;
	Logger logger;
	
	public BaseManagerImpl( Class mapperClass, Class dtoClass, Logger logger )
	{
		setMapperClass( mapperClass );
		setLogger( logger );
		setDtoClass( dtoClass );
	}
	
	@Override
	public boolean hasUniqueId( AppContext ctx ) 
	{
		return true;
	}
	
	@Override
	public boolean hasBlob() 
	{
		return false;
	}
	
	public boolean setEmptyToNull()
	{
		return true;
	}
	
	protected int getAuditTypeNew() 							{ return Constants.AUDIT_TYPE_UNDEFINED; }
	protected int getAuditTypeModify()							{ return Constants.AUDIT_TYPE_UNDEFINED; }
	protected int getAuditTypeDelete()							{ return Constants.AUDIT_TYPE_UNDEFINED; }

	protected int getAuditNewDuration( AppContext ctx ) 						{ return Constants.AUDIT_DURATION_DEFAULT; }
	protected int getAuditModifyDuration( AppContext ctx )						{ return Constants.AUDIT_DURATION_DEFAULT; }
	protected int getAuditDeleteDuration( AppContext ctx )						{ return Constants.AUDIT_DURATION_DEFAULT; }
	
	protected String getAuditDetailsNew( AppContext ctx, BaseDto newRow ) 						{ return ""; }
	protected String getAuditDetailsModify( AppContext ctx, BaseDto lastRow, BaseDto newRow )	{ return ""; }
	protected String getAuditDetailsDelete( AppContext ctx, BaseDto row )						{ return ""; }

	private void audit( AppContext ctx, int type, int duration, String details )
	{
		if ( type != Constants.AUDIT_TYPE_UNDEFINED )
		{
			try
			{
				Audit audit = new Audit();
				
				audit.setAudit_date( CalendarUtils.getTodayAsLong( "UTC" ) );
				audit.setAudit_type( type );
				
				if ( ctx.getUser() != null )
					audit.setRef_user( ctx.getUser().getId() );
				
				audit.setDuration( duration );
				audit.setAudit_details( details );
				
				IOCManager._AuditsManager.setRow( ctx, null, audit );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, getLogger() );
			}
		}
	}
	
	public void setRow( AppContext ctx, BaseDto lastRow, BaseDto newRow ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();

		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );

			if ( setEmptyToNull() )
				Utils.emptyToNull( newRow, newRow.getClass() );
			
			if ( !hasUniqueId( ctx ) || newRow.getId() == null )
			{
				if ( !hasBlob() && isLogEnabled( ctx, "I" ) ) 
					getLogger().info( "inserting row " + newRow );
				
				mapper.addRow( newRow );
				
				try
				{
					audit( ctx, getAuditTypeNew(), getAuditNewDuration(ctx), getAuditDetailsNew( ctx, newRow ) );
				}
				catch ( Throwable e1 )
				{
					Utils.logException( e1, getLogger() );
				}
				
				if ( hasUniqueId( ctx ) && newRow.getId() == null )
					throw new BaseException( new Exception( "Row id was not set. Check SQL script" ), getLogger(), BaseException.INVALID_ROW_ID );
			}
			else
			{
				if ( !hasBlob() && isLogEnabled( ctx, "U" ) )
					getLogger().info( "updating row " + newRow );

				if ( lastRow == null )
					throw new BaseException( new Exception( "Update row without the last row value" ), getLogger(), BaseException.UPDATE_WITHOUT_LAST );
				
				if ( !lastRow.getClass().equals( newRow.getClass() ) )
					throw new BaseException( new Exception( "Update mismatch classes" ), getLogger(), BaseException.UPDATE_CLASSES_MISMATCH );
				
				mapper.setRow( newRow );

				try
				{
					audit( ctx, getAuditTypeModify(), getAuditModifyDuration(ctx), getAuditDetailsModify( ctx, lastRow, newRow ) );
				}
				catch ( Throwable e1 )
				{
					Utils.logException( e1, getLogger() );
				}
			}

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish ) 
				session.rollback();
			
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );
			
			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, getLogger(), BaseException.UNKNOWN );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
	}

	public void delRow( AppContext ctx, BaseDto row ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			if ( !hasBlob() && isLogEnabled( ctx, "D" ) )
				getLogger().info( "deleting row " + row );

			mapper.delRow( row );

			try
			{
				audit( ctx, getAuditTypeDelete(), getAuditDeleteDuration(ctx), getAuditDetailsDelete( ctx, row ) );
			}
			catch ( Throwable e1 )
			{
				Utils.logException( e1, getLogger() );
			}

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();
			
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			throw new BaseException( e, getLogger(), BaseException.UNKNOWN );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
	}

	public long getNumberOfRows( AppContext ctx, Query query ) throws BaseException
    {
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		long count = 0;
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			count = mapper.getNumberOfRows( query ); 
		}
		catch ( Throwable e )
		{
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			throw new BaseException( e, getLogger(), BaseException.UNKNOWN );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
		
		return count;
    }
    
	public List getRows( AppContext ctx, Query query ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		ArrayList<BaseDto> rows = new ArrayList<BaseDto>();
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			boolean paged = (query != null) && (query.getPageSize() != null) && (query.getPageSize() != -1);  
			
			if ( isLogEnabled( ctx, "S" ) )
				getLogger().info( "retrieving rows " + query );

			ArrayList<BaseDto> temp = paged ? mapper.getPage( query ) : mapper.getRows( query );
			
			for ( BaseDto dto : temp )
			{
				if ( dto != null )
				{
					Utils.nullToEmpty( dto, dto.getClass() );
					
					rows.add( dto );
				}
			}
		}
		catch ( Throwable e )
		{
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			throw new BaseException( e, getLogger(), BaseException.UNKNOWN );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}

		if ( rows == null )
			throw new BaseException( new Exception( "Null return" ), getLogger(), BaseException.NULL_RETURN );
		
		return rows;
	}
	
	public BaseDto newDto() throws BaseException
	{
		try 
		{
			return (BaseDto) dtoClass.newInstance();
		} 
		catch ( Throwable e ) 
		{
			throw new BaseException( e, getLogger(), BaseException.UNKNOWN );
		} 
	}

	public BaseDto getLastRow( AppContext ctx, BaseDto query ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		BaseDto row = null;
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			row = mapper.getLastRow( query ); 
			
			if ( row != null )
				Utils.nullToEmpty( row, row.getClass() );
		}
		catch ( Throwable e )
		{
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			if ( e instanceof BaseException )
				throw (BaseException)e;
			
			throw new BaseException( e, getLogger(), 0 );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
		
		return row;
	}
	
	public BaseDto getRow( AppContext ctx, BaseDto dto ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		BaseDto row = null;
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			row = mapper.getRow( dto ); 
			
			if ( row != null )
				Utils.nullToEmpty( row, row.getClass() );
		}
		catch ( Throwable e )
		{
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			if ( e instanceof BaseException )
				throw (BaseException)e;
			
			throw new BaseException( e, getLogger(), 0 );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
		
		return row;
	}
	
	public BaseDto getNextRow( AppContext ctx, BaseDto query ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		BaseDto row = null;
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			row = mapper.getNextRow( query ); 
			
			if ( row != null )
				Utils.nullToEmpty( row, row.getClass() );
		}
		catch ( Throwable e )
		{
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			if ( e instanceof BaseException )
				throw (BaseException)e;
			
			throw new BaseException( e, getLogger(), 0 );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
		
		return row;
	}

	public void delAllRows( AppContext ctx, Query query ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			if ( !hasBlob() && isLogEnabled( ctx, "D" ) )
				getLogger().info( "deleting rows " + query );

			mapper.delAllRows( query );

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();
			
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			throw new BaseException( e, getLogger(), BaseException.UNKNOWN );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
	}
	
	public boolean isLogEnabled( AppContext ctx, String action )
	{
		return ctx.isLogEnabled( getLogSetting(), action );
	}

	@Override
	public long getLogSetting() 
	{
		return Parameter.PAR_LOG_DEFAULT;
	}
}
