package es.pryades.erp.dto.query;

import es.pryades.erp.dto.Operation;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class OperationQuery extends Operation
{
	private static final long serialVersionUID = -6988907690093283628L;
	
	private Long ref_user;
}
