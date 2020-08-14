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
public class ProfileVto extends GenericVto
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1138689932719730209L;
	String description;

	public ProfileVto()
	{
	}
}
