package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.Task;
import es.pryades.erp.vto.TaskVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class TaskControlerVto extends GenericControlerVto
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1862504231060338917L;

	public TaskControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		TaskVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Task.class) )
			{
				result = new TaskVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((Task) dtoObj).getId());
				result.setMonth( ((Task) dtoObj).getMonth() ); 
				result.setDay( ((Task) dtoObj).getDay() ); 
				result.setHour( ((Task) dtoObj).getHour() ); 
				result.setDescription( ((Task) dtoObj).getDescription() ); 
				result.setClazz( ((Task) dtoObj).getTaskClazzAsString( getContext() ) );
				//result.setUser_name( ((Report) dtoObj).getUser_name() );
				//result.setDetails( ((Report) dtoObj).getReport_details() );
			}
			else
			{
			}
		}

		return result;
	}
}
