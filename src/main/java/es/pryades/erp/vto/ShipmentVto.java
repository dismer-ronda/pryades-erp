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
public class ShipmentVto extends GenericVto
{
	private static final long serialVersionUID = 3950857991483418717L;

	private String title;		
	private String number;		
	private String departure_date;						
	private String incoterms;
	private String consignee_name;
	private String status;
	private String carrier;
	private String tracking;
    
	public ShipmentVto()
	{
	}
}
