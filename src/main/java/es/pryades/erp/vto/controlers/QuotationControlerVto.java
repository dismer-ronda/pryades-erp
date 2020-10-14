package es.pryades.erp.vto.controlers;

import java.util.List;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.Utils;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.Quotation;
import es.pryades.erp.dto.QuotationDelivery;
import es.pryades.erp.dto.QuotationLine;
import es.pryades.erp.dto.QuotationLineDelivery;
import es.pryades.erp.vto.QuotationVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class QuotationControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 6662301649856756872L;

	public QuotationControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		QuotationVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Quotation.class) )
			{
				result = new QuotationVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((Quotation) dtoObj).getId());
				
				result.setNumber(((Quotation) dtoObj).getFormattedNumber());
				result.setTitle(((Quotation) dtoObj).getTitle());
				result.setQuotation_date( CalendarUtils.getDateFromLongAsString( ((Quotation) dtoObj).getQuotation_date(), "dd-MM-yyyy" ) );
				
				result.setReference_request( ((Quotation) dtoObj).getReference_request() ); 
				result.setReference_order( ((Quotation) dtoObj).getReference_order() ); 
				result.setCustomer_name( ((Quotation) dtoObj).getCustomer().getAlias() ); 
				
				List<QuotationDelivery> deliveries = ((Quotation) dtoObj).getDeliveries();
				String text = "";
				for ( QuotationDelivery delivery : deliveries )
				{
					if ( !text.isEmpty() )
						text += ", ";
					
					text += CalendarUtils.getDateFromLongAsString( delivery.getDeparture_date(), "dd-MM-yyyy" );
				}
				result.setQuotation_deliveries( text );
				
				List<QuotationLine> lines = ((Quotation) dtoObj).getLines();
				double total_cost = 0;
				double total_price = 0;
				for ( QuotationLine line : lines )
				{
					for ( QuotationLineDelivery delivery : line.getLine_deliveries() )
					{
						total_cost += delivery.getQuantity() * line.getCost();
						total_price += delivery.getQuantity() * line.getPrice();
					}
				}
				result.setTotal_cost( Utils.roundDouble( total_cost, 2 ) );
				result.setTotal_price( Utils.roundDouble( total_price, 2 ) );
				result.setTotal_profit( Utils.roundDouble( total_price - total_cost, 2 ) );
				result.setTotal_quotation( Utils.roundDouble( total_price + ((Quotation) dtoObj).getTotalTransportCost(), 2 ) );
				result.setStatus( getContext().getString( "quotation.status." + ((Quotation) dtoObj).getStatus()) );
			}
			else
			{
			}
		}

		return result;
	}
}
