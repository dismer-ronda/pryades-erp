package es.pryades.erp.dto.query;

import es.pryades.erp.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class AuditQuery extends BaseDto
{
	private static final long serialVersionUID = -5333897399766315271L;

	private Integer audit_type;
	private Long ref_user;

	private Long from_date;
	private Long to_date;
	
	private String textQuery;
}
