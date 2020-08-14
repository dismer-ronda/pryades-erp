package es.pryades.erp.dal.ibatis;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.dto.Right;
import es.pryades.erp.dto.User;
import es.pryades.erp.dto.query.RightQuery;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public interface UserMapper extends BaseMapper
{
    public void setPassword( User user );
    public void setRetries( User user );
    public void setStatus( User user );
    
    public Right getRight( RightQuery query ) throws BaseException;
}
