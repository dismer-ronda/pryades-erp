package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.Profile;
import es.pryades.erp.vto.ProfileVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class ProfileControlerVto extends GenericControlerVto
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5742118802757140261L;

	public ProfileControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		ProfileVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Profile.class) )
			{
				result = new ProfileVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((Profile) dtoObj).getId());
				result.setDescription(((Profile) dtoObj).getDescription());
			}
			else
			{
			}
		}

		return result;
	}
}
