package es.pryades.erp.vto;

import es.pryades.erp.common.GenericVto;
import es.pryades.erp.dto.InvoiceLine;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Dismer Ronda
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ShipmentBoxLineVto extends GenericVto
{
	private static final long serialVersionUID = -3175711623225819030L;
	
	private String title;		
	private Integer quantity;			
	private Double net_weight; 
	private Double gross_weight;
	
	public ShipmentBoxLineVto()
	{
	}
}
