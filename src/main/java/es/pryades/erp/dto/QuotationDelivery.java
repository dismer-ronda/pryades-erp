package es.pryades.erp.dto;

import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class QuotationDelivery extends BaseDto
{
	private static final long serialVersionUID = 9023553130295986408L;
	
	private Long ref_quotation;						
	private Long departure_date;
	
	private String departure_port;
	private String arrival_port;
	
	private String incoterms;
	private Double cost;

	private Boolean free_delivery; 	
	
	public String getDepartureDateFormatted()
	{
		return CalendarUtils.getDateFromLongAsString( getDeparture_date(), "dd-MM-yyyy" );
	}
	
	public String getCostAsCurrency()
	{
		return Utils.getFormattedCurrency( getCost() );
	}
}
