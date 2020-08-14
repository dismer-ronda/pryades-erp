package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.UserCompanyMapper;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.UserCompany;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class UsersCompaniesManagerImpl extends BaseManagerImpl implements UsersCompaniesManager
{
	private static final long serialVersionUID = -757377952456307830L;
	
	private static final Logger LOG = Logger.getLogger( UsersCompaniesManagerImpl.class );

	public static BaseManager build()
	{
		return new UsersCompaniesManagerImpl();
	}

	public UsersCompaniesManagerImpl()
	{
		super( UserCompanyMapper.class, UserCompany.class, LOG );
	}

	@Override
	public long getLogSetting() 
	{
		return Parameter.PAR_LOG_DEFAULT;
	}
}
