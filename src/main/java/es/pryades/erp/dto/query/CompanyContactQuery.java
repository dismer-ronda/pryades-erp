package es.pryades.erp.dto.query;

import es.pryades.erp.dto.CompanyContact;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class CompanyContactQuery extends CompanyContact
{
	private static final long serialVersionUID = 5857289157217732637L;
	
	private Long ref_user;
}
