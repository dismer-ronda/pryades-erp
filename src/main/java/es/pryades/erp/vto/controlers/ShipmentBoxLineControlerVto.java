package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.ShipmentBoxLine;
import es.pryades.erp.vto.ShipmentBoxLineVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class ShipmentBoxLineControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 2754413189283708908L;

	public ShipmentBoxLineControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		ShipmentBoxLineVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(ShipmentBoxLine.class) )
			{
				result = new ShipmentBoxLineVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((ShipmentBoxLine) dtoObj).getId());
				
				result.setTitle( ((ShipmentBoxLine) dtoObj).getInvoice_line().getQuotation_line().getTitle() );
				result.setQuantity( ((ShipmentBoxLine) dtoObj).getQuantity() );
				result.setGross_weight( ((ShipmentBoxLine) dtoObj).getGross_weight() );
				result.setNet_weight( ((ShipmentBoxLine) dtoObj).getNet_weight() );
			}
			else
			{
			}
		}

		return result;
	}
}
