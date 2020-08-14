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
public class QuotationVto extends GenericVto
{
	private static final long serialVersionUID = 6846170349802984164L;
	
	private String number;						
	private String title;						
	private String quotation_date;						
	private String quotation_deliveries;						
	
	private String reference_request;
	private String reference_order;
	
	private String customer_name;

	private Double total_cost;
	private Double total_price;
	private Double total_profit;
	
	private Double total_quotation;

	private String status;
	
	public QuotationVto()
	{
	}
}
