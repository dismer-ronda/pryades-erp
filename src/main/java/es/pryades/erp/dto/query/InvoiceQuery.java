package es.pryades.erp.dto.query;

import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.dto.Invoice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class InvoiceQuery extends Invoice
{
	private static final long serialVersionUID = 1183748481115773254L;
	
	private Long from_date;						
	private Long to_date;

	private String reference_request;
	private String reference_order;

	private Long ref_operation;
	private Long ref_customer;
	private Long ref_user;
	
	private Boolean for_collect;
	
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
