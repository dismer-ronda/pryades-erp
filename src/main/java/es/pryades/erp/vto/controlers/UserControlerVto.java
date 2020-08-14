package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.User;
import es.pryades.erp.vto.UserVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class UserControlerVto extends GenericControlerVto
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7037511755429634762L;

	public UserControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		UserVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(User.class) )
			{
				result = new UserVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((User) dtoObj).getId());
				result.setName(((User) dtoObj).getName());
				result.setLogin( ((User) dtoObj).getLogin() );
				result.setEmail( ((User) dtoObj).getEmail() );
				result.setRef_profile( ((User) dtoObj).getRef_profile() );
				result.setPwd( ((User) dtoObj).getPwd() );
				result.setProfile_name( ((User) dtoObj).getProfile_name() );
				result.setRegion_name( ((User) dtoObj).getRegion_name() );
				result.setPlant_name( ((User) dtoObj).getPlant_name() );
			}
			else
			{
			}
		}

		return result;
	}
}
