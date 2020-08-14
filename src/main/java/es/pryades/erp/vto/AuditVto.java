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
public class AuditVto extends GenericVto
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3811584461197788012L;
	String audit_date;
    String audit_type;
    String audit_details;
    String user_name;
    
	public AuditVto()
	{
	}
}
