package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.Utils;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.QuotationLine;
import es.pryades.erp.dto.QuotationLineDelivery;
import es.pryades.erp.vto.QuotationLineVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class QuotationLineControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 8815504025434822867L;

	public QuotationLineControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		QuotationLineVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(QuotationLine.class) )
			{
				result = new QuotationLineVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				QuotationLine line = ((QuotationLine) dtoObj);

				result.setId( line.getId() );
				
				result.setLine_order( line.getLine_order() ); 
				result.setOrigin( line.getOrigin() ); 
				result.setReference( line.getReference() ); 
				result.setTitle( line.getTitle() ); 
				result.setDescription( line.getDescription() ); 
				result.setCost( line.getCost() ); 
				result.setReal_cost( line.getReal_cost() ); 
				result.setMargin( line.getMargin() );
				result.setTotal_invoiced( line.getTotal_invoiced() );
				result.setProvider_name( line.getProvider() != null ? line.getProvider().getName() : "" );
				
				result.setPrice( line.getPrice() ); 
				
				double total_cost = 0;
				double total_price = 0;
				int quantity = 0;
				for ( QuotationLineDelivery delivery : line.getLine_deliveries() )
				{
					total_cost += delivery.getQuantity() * line.getReal_cost();
					total_price += delivery.getQuantity() * line.getPrice();
					quantity += delivery.getQuantity();
				}
				 
				result.setTotal_cost( Utils.roundDouble( total_cost, 2 ) );
				result.setTotal_price( Utils.roundDouble( total_price, 2 ) );
				result.setProfit( Utils.roundDouble( total_price - total_cost, 2 ) );
				result.setQuantity( quantity );
			}
			else
			{
			}
		}

		return result;
	}
}
