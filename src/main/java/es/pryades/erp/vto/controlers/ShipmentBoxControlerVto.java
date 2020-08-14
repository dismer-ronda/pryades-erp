package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.ShipmentBox;
import es.pryades.erp.vto.ShipmentBoxVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class ShipmentBoxControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -528615411374410655L;

	public ShipmentBoxControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		ShipmentBoxVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(ShipmentBox.class) )
			{
				result = new ShipmentBoxVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((ShipmentBox) dtoObj).getId());
				
				result.setBox_type( getContext().getString( "shipment.box.type." + ((ShipmentBox) dtoObj).getBox_type()) );
				result.setLabel( ((ShipmentBox) dtoObj).getLabel() );
				
				result.setLength( ((ShipmentBox) dtoObj).getLength());
				result.setWidth( ((ShipmentBox) dtoObj).getWidth());
				result.setHeight( ((ShipmentBox) dtoObj).getHeight());
			}
			else
			{
			}
		}

		return result;
	}
}
