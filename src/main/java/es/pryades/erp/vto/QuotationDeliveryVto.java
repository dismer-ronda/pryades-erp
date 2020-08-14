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
public class QuotationDeliveryVto extends GenericVto
{
	private static final long serialVersionUID = 284495070717935764L;

	private String departure_date;
	
	private String departure_port;
	private String arrival_port;
	
	private String incoterms;
	private Double cost;
	
	private String free_delivery; 	

	public QuotationDeliveryVto()
	{
	}
}
