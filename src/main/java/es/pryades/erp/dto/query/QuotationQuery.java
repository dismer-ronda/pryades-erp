package es.pryades.erp.dto.query;

import es.pryades.erp.dto.Quotation;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class QuotationQuery extends Quotation
{
	private static final long serialVersionUID = -2739825898208379003L;
	
	private Long from_date;						
	private Long to_date;
	private Long ref_user;
}
