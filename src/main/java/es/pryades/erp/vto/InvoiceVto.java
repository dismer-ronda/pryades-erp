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
public class InvoiceVto extends GenericVto
{
	private static final long serialVersionUID = 9200599285229803259L;
	
	private String number;						
	private String title;						
	private String invoice_date;						
	
	private String reference_request;
	private String reference_order;
	
	private String customer_name;

	private Double total_price;
	private Double total_invoice;
	
	public InvoiceVto()
	{
	}
}
