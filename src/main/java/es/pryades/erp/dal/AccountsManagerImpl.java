package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.AccountMapper;
import es.pryades.erp.dto.Account;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class AccountsManagerImpl extends BaseManagerImpl implements AccountsManager
{
	private static final long serialVersionUID = 8694018967266380294L;
	
	private static final Logger LOG = Logger.getLogger( AccountsManagerImpl.class );

	public static BaseManager build()
	{
		return new AccountsManagerImpl();
	}

	public AccountsManagerImpl()
	{
		super( AccountMapper.class, Account.class, LOG );
	}
}
