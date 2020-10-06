package es.pryades.erp.vto;

import com.vaadin.ui.Label;

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
public class QuotationLineVto extends GenericVto
{
	private static final long serialVersionUID = -792798013568585771L;
	
	private Integer line_order;
	private String origin;
	private String reference;
	private String title;
	private String description;

	private Double cost;
	private Double margin;
	private Double price;
	private Double real_cost;
	private Label total_invoiced;

	private Integer quantity;

	private Double total_cost;
	private Double total_price;
	private Double profit;

	private String provider_name;

	public QuotationLineVto()
	{
	}
}
