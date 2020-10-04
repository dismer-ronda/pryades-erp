package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.CompanyContactMapper;
import es.pryades.erp.dto.CompanyContact;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class CompaniesContactsManagerImpl extends BaseManagerImpl implements CompaniesContactsManager
{
	private static final long serialVersionUID = -3465516539569086047L;
	
	private static final Logger LOG = Logger.getLogger( CompaniesContactsManagerImpl.class );

	public static BaseManager build()
	{
		return new CompaniesContactsManagerImpl();
	}

	public CompaniesContactsManagerImpl()
	{
		super( CompanyContactMapper.class, CompanyContact.class, LOG );
	}

	@Override
	public boolean setEmptyToNull()
	{
		return false;
	}
}
