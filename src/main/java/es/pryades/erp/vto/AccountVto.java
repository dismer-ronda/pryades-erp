package es.pryades.erp.vto;

import com.vaadin.ui.Label;

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
public class AccountVto extends GenericVto
{
	private static final long serialVersionUID = 2714644398253830487L;
	
	private String account_type;						
	private String name;						
	private String number;						
	private Label balance;
	private String company_name;						
	
	public AccountVto()
	{
	}
}
