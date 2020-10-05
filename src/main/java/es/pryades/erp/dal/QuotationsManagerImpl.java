package es.pryades.erp.dal;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.ibatis.QuotationMapper;
import es.pryades.erp.dto.Quotation;
import es.pryades.erp.dto.QuotationDelivery;
import es.pryades.erp.dto.QuotationLine;
import es.pryades.erp.dto.QuotationLineDelivery;
import es.pryades.erp.ioc.IOCManager;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class QuotationsManagerImpl extends BaseManagerImpl implements QuotationsManager
{
	private static final long serialVersionUID = -1129543325414484216L;
	
	private static final Logger LOG = Logger.getLogger( QuotationsManagerImpl.class );

	public static BaseManager build()
	{
		return new QuotationsManagerImpl();
	}

	public QuotationsManagerImpl()
	{
		super( QuotationMapper.class, Quotation.class, LOG );
	}

	@Override
	public boolean duplicateQuotation( AppContext ctx, Quotation src ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();

		try 
		{
			Quotation newQuotation = new Quotation();
			
			newQuotation.setTitle( src.getTitle() );
			newQuotation.setQuotation_date( CalendarUtils.getTodayAsLong() );
			newQuotation.setValidity( src.getValidity() );
			newQuotation.setRef_customer( src.getRef_customer() );
			newQuotation.setReference_request( src.getReference_request() );
			newQuotation.setReference_order( src.getReference_order() );
			newQuotation.setOrigin( src.getOrigin() );
			newQuotation.setPackaging( src.getPackaging() );
			newQuotation.setDelivery( src.getDelivery() );
			newQuotation.setWarranty( src.getWarranty() );
			newQuotation.setPayment_terms( src.getPayment_terms() );
			newQuotation.setTax_rate( src.getTax_rate() );
			newQuotation.setStatus( Quotation.STATUS_CREATED);
			newQuotation.setRef_contact( src.getRef_contact() );
			newQuotation.setRef_user( src.getRef_user() );
			newQuotation.setWeight( src.getWeight() );
			newQuotation.setVolume( src.getVolume() );
			
			newQuotation.setDeliveries( new ArrayList<QuotationDelivery>() );
			
			IOCManager._QuotationsManager.setRow( ctx, null, newQuotation );
			
			HashMap<Long, Long> map = new HashMap<Long, Long>();
			
			for ( QuotationDelivery delivery : src.getDeliveries()  )
			{
				QuotationDelivery newDelivery = (QuotationDelivery)Utils.clone( delivery );
				
				newDelivery.setId( null );
				newDelivery.setRef_quotation( newQuotation.getId() );

				IOCManager._QuotationsDeliveriesManager.setRow( ctx, null, newDelivery );
				
				newQuotation.getDeliveries().add( newDelivery );
				
				map.put( delivery.getId(), newDelivery.getId() );
			}

			for ( QuotationLine line : src.getLines()  )
			{
				QuotationLine newLine = new QuotationLine();
				
				newLine.setRef_quotation( newQuotation.getId() );
				newLine.setRef_provider( line.getRef_provider() );
				newLine.setLine_order( line.getLine_order() );
				newLine.setOrigin( line.getOrigin() );
				newLine.setReference( line.getReference() );
				newLine.setTitle( line.getTitle() );
				newLine.setDescription( line.getDescription() );
				newLine.setCost( line.getCost() );
				newLine.setMargin( line.getMargin() );
				newLine.setReal_cost( line.getReal_cost() );
				newLine.setTax_rate( line.getTax_rate() );
				
				IOCManager._QuotationsLinesManager.setRow( ctx, null, newLine );
				
				for ( QuotationLineDelivery lineDelivery : line.getLine_deliveries()  )
				{
					QuotationLineDelivery newLineDelivery = new QuotationLineDelivery();
					
					newLineDelivery.setId( null );
					newLineDelivery.setRef_quotation_line( newLine.getId() );
					newLineDelivery.setRef_quotation_delivery( map.get( lineDelivery.getRef_quotation_delivery() ) );
					newLineDelivery.setQuantity( lineDelivery.getQuantity() );

					IOCManager._QuotationsLinesDeliveriesManager.setRow( ctx, null, newLineDelivery );
				}
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
