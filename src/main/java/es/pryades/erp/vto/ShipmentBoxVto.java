package es.pryades.erp.vto;

import es.pryades.erp.common.GenericVto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Dismer Ronda
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ShipmentBoxVto extends GenericVto
{
	private static final long serialVersionUID = 6063829195399394579L;
	
	private String label;		
	private String box_type;		
	private Double width;						
	private Double length;
	private Double height;
	private Double gross_weight;
    
	public ShipmentBoxVto()
	{
	}
}
