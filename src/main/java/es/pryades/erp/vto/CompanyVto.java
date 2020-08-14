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
public class CompanyVto extends GenericVto
{
	private static final long serialVersionUID = 2128381954126520587L;
	
	String alias;
	String name;
    String tax_id;
    String type_company;
    String email;
    String phone;
    
	public CompanyVto()
	{
	}
}
