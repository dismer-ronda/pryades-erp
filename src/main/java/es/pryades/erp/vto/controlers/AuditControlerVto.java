package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.Audit;
import es.pryades.erp.vto.AuditVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class AuditControlerVto extends GenericControlerVto
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7892841844756229885L;

	public AuditControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		AuditVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Audit.class) )
			{
				result = new AuditVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((Audit) dtoObj).getId());
				result.setAudit_date( CalendarUtils.getDateFromLongAsString( ((Audit) dtoObj).getAudit_date(), "yyyy/MM/dd HH:mm:ss", "UTC" ) ); 
				result.setAudit_type( ((Audit) dtoObj).getAuditTypeAsString( getContext() ) );
				result.setUser_name( ((Audit) dtoObj).getUser_name() );
				result.setAudit_details( ((Audit) dtoObj).getAudit_details() );
			}
			else
			{
			}
		}

		return result;
	}
}
