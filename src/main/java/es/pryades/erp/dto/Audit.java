package es.pryades.erp.dto;

import es.pryades.erp.common.AppContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class Audit extends BaseDto
{
	private static final long serialVersionUID = 2964811672133344905L;
	
	private Long audit_date;
	private Integer audit_type;
	private Long ref_user;
	private String audit_details;
	private String user_name;
	private Integer duration;
	
   	public String getAuditTypeAsString( AppContext ctx )
   	{
   		return ctx.getString( "audit.type." + getAudit_type() ); 
   	}
}
