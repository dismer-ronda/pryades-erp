package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.Company;
import es.pryades.erp.vto.CompanyVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class CompanyControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -9177601789049482241L;

	public CompanyControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		CompanyVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Company.class) )
			{
				result = new CompanyVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((Company) dtoObj).getId());
				result.setAlias( ((Company) dtoObj).getAlias() ); 
				result.setName( ((Company) dtoObj).getName() ); 
				result.setTax_id( ((Company) dtoObj).getTax_id() ); 
				result.setEmail( ((Company) dtoObj).getEmail() ); 
				result.setPhone( ((Company) dtoObj).getPhone() ); 
				result.setType_company( getContext().getString( "company.type." + ((Company) dtoObj).getType_company() ) ); 
			}
			else
			{
			}
		}

		return result;
	}
}
