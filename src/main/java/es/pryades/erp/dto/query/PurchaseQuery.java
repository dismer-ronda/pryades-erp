package es.pryades.erp.dto.query;

import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.dto.Purchase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class PurchaseQuery extends Purchase
{
	private static final long serialVersionUID = -4092043605164045315L;
	
	private Long from_date;						
	private Long to_date;
	private Boolean for_payment;

	public String getPeriodToString()
	{
		String ret = "";
		
		if ( getFrom_date() != null )
			ret += CalendarUtils.getFormatedDate( getFrom_date(), "dd-MM-yyyy" );
		
		ret += "  ...  ";
		
		if ( getTo_date() != null )
			ret += CalendarUtils.getFormatedDate( getTo_date(), "dd-MM-yyyy" );
		
		return ret;
	}
}
