package es.pryades.erp.dal;

import java.util.HashMap;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.dto.Parameter;


/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface ParametersManager extends BaseManager
{
	public HashMap<Long, Parameter> getParameters( AppContext ctx );
	public void loadParameters( AppContext ctx );
}
