package es.pryades.erp.dto.query;

import es.pryades.erp.dto.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class UserQuery extends User
{
	private static final long serialVersionUID = -7869911886690481929L;
}
