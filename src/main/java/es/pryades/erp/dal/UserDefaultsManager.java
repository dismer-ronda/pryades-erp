package es.pryades.erp.dal;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.dto.UserDefault;



/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface UserDefaultsManager extends BaseManager
{
	UserDefault getUserDefault( AppContext context, String key );
	void setUserDefault( AppContext context, UserDefault def, String value );
}
