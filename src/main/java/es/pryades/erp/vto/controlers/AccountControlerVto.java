package es.pryades.erp.vto.controlers;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.Utils;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.Account;
import es.pryades.erp.dto.Transaction;
import es.pryades.erp.vto.AccountVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class AccountControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -1310116157213041980L;

	public AccountControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		AccountVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Account.class) )
			{
				result = new AccountVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );
				
				Account account = (Account) dtoObj; 
				Transaction first = account.getTransactions().size() > 0 ? account.getTransactions().get( 0 ) : null; 

				result.setId( account.getId( ));
				
				result.setAccount_type( getContext().getString( "account.type." + account.getAccount_type() ) );
				result.setName( account.getName() );
				result.setNumber( account.getNumber() );
				result.setBalance( first != null ? Utils.getFormattedCurrency( first.getBalance() ) : "" ); 
				result.setCompany_name( account.getCompany() != null ? account.getCompany().getAlias() : "" ); 
			}
			else
			{
			}
		}

		return result;
	}
}
