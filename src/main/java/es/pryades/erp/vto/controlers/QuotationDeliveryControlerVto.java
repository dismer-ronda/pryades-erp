package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.QuotationDelivery;
import es.pryades.erp.vto.QuotationDeliveryVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class QuotationDeliveryControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -5805357941834866776L;

	public QuotationDeliveryControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		QuotationDeliveryVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(QuotationDelivery.class) )
			{
				result = new QuotationDeliveryVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				QuotationDelivery delivery = ((QuotationDelivery) dtoObj);

				result.setId( delivery.getId() );
				
				result.setDeparture_date( CalendarUtils.getDateFromLongAsString( delivery.getDeparture_date(), "dd-MM-yyyy" ) );
				result.setDeparture_port( delivery.getDeparture_port() );
				result.setArrival_port( delivery.getArrival_port() );
				result.setIncoterms( delivery.getIncoterms() );
				result.setCost( delivery.getCost() );
				result.setFree_delivery( getContext().getString( delivery.getFree_delivery().booleanValue() ? "words.provider" : "words.customer" ) );
			} 
			else
			{
			}
		}

		return result;
	}
}
