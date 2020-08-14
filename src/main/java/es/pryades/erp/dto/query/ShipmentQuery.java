package es.pryades.erp.dto.query;

import es.pryades.erp.dto.Shipment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class ShipmentQuery extends Shipment
{
	private static final long serialVersionUID = -6416514666594394749L;
	
	private Long from_date;						
	private Long to_date;

	private Long ref_user;
}
