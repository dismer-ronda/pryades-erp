package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.CompanyMapper;
import es.pryades.erp.dto.Company;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class CompaniesManagerImpl extends BaseManagerImpl implements CompaniesManager
{
	private static final long serialVersionUID = 7655523660839236791L;
	
	private static final Logger LOG = Logger.getLogger( CompaniesManagerImpl.class );

	public static BaseManager build()
	{
		return new CompaniesManagerImpl();
	}

	public CompaniesManagerImpl()
	{
		super( CompanyMapper.class, Company.class, LOG );
	}

	@Override
	public boolean setEmptyToNull()
	{
		return false;
	}
}
