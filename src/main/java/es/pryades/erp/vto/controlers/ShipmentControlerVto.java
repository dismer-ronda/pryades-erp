package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.Shipment;
import es.pryades.erp.vto.ShipmentVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class ShipmentControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 7900335037737512275L;

	public ShipmentControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		ShipmentVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Shipment.class) )
			{
				result = new ShipmentVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((Shipment) dtoObj).getId());
				
				result.setTitle(((Shipment) dtoObj).getTitle());
				result.setNumber(((Shipment) dtoObj).getFormattedNumber());
				result.setDeparture_date( CalendarUtils.getDateFromLongAsString( ((Shipment) dtoObj).getDeparture_date(), "dd-MM-yyyy" ) );
				result.setIncoterms( ((Shipment) dtoObj).getIncoterms() ); 
				result.setConsignee_name( ((Shipment) dtoObj).getConsignee_name() ); 
				result.setStatus( getContext().getString( "shipment.status." + ((Shipment) dtoObj).getStatus()) );
				result.setCarrier( ((Shipment) dtoObj).getCarrier() );
				result.setTracking( ((Shipment) dtoObj).getTracking() );
			}
			else
			{
			}
		}

		return result;
	}
}
