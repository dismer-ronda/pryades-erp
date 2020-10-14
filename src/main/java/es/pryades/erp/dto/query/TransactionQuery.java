package es.pryades.erp.dto.query;

import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.dto.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class TransactionQuery extends Transaction
{
	private static final long serialVersionUID = -2699568976514593735L;
	
	private Long from_date;						
	private Long to_date;

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
