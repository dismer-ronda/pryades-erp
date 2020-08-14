package es.pryades.erp.dal;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.dal.ibatis.InvoiceMapper;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.dto.InvoiceLine;
import es.pryades.erp.ioc.IOCManager;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class InvoicesManagerImpl extends BaseManagerImpl implements InvoicesManager
{
	private static final long serialVersionUID = 7316344102356022841L;
	
	private static final Logger LOG = Logger.getLogger( InvoicesManagerImpl.class );

	public static BaseManager build()
	{
		return new InvoicesManagerImpl();
	}

	public InvoicesManagerImpl()
	{
		super( InvoiceMapper.class, Invoice.class, LOG );
	}

	@Override
	public boolean duplicateInvoice( AppContext ctx, Invoice src ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();

		try 
		{
			Invoice newQuotation = new Invoice();
			
			newQuotation.setInvoice_date( CalendarUtils.getTodayAsLong() );
			newQuotation.setRef_quotation( src.getRef_quotation() );
			newQuotation.setTransport_cost( src.getTransport_cost() );
			newQuotation.setFree_delivery( src.getFree_delivery() );
			newQuotation.setRef_quotation( src.getRef_quotation() );
			
			IOCManager._InvoicesManager.setRow( ctx, null, newQuotation );
			
			for ( InvoiceLine line : src.getLines()  )
			{
				InvoiceLine newLine = new InvoiceLine();
				
				newLine.setRef_invoice( newQuotation.getId() );
				newLine.setRef_quotation_line( line.getRef_quotation_line() );
				newLine.setQuantity( 0 );
				
				IOCManager._InvoicesLinesManager.setRow( ctx, null, newLine );
			}
			
			if ( finish )
				session.commit();
			
			return false;
		}
		catch ( Throwable e )
		{
			if ( finish ) 
				session.rollback();
			
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}

		return false;
	}

	@Override
	public boolean setEmptyToNull()
	{
		return false;
	}
}
