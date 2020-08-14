package es.pryades.erp.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class QuotationLineDelivery extends BaseDto
{
	private static final long serialVersionUID = -2964360668961337404L;
	
	private Long ref_quotation_delivery;
	private Long ref_quotation_line;
	
	private Integer quantity;
}
