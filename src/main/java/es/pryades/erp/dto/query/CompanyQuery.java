package es.pryades.erp.dto.query;

import es.pryades.erp.dto.Company;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class CompanyQuery extends Company
{
	private static final long serialVersionUID = 8860418354066550347L;
	
	private Long ref_user;
}
