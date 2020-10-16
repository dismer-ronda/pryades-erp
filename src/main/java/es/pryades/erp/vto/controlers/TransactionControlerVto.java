package es.pryades.erp.vto.controlers;

import com.vaadin.ui.Label;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.GenericVto;
import es.pryades.erp.common.VtoControllerFactory;
import es.pryades.erp.dto.Transaction;
import es.pryades.erp.vto.TransactionVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class TransactionControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 8375423820978323272L;

	public TransactionControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		TransactionVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Transaction.class) )
			{
				result = new TransactionVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				Transaction transaction = (Transaction) dtoObj;
				
				result.setId( transaction.getId());
				
				String description = transaction.getDescription();
				
				if ( transaction.getTransaction_type().equals( Transaction.TYPE_PAYMENT ) )
					description = transaction.getPurchase().getTitle();
				else if ( transaction.getTransaction_type().equals( Transaction.TYPE_INCOME ) )
					description = transaction.getInvoice().getTitle();

				result.setTransaction_date( CalendarUtils.getDateFromLongAsString( transaction.getTransaction_date(), "dd-MM-yyyy" ) );
				result.setTransaction_type( getContext().getString( "transaction.type." + transaction.getTransaction_type() ) );
				result.setDescription( description );
				result.setAccount_name( transaction.getAccount().getName() );
				
				boolean neggativeAmount = transaction.getAmount() < 0;
				boolean neggativeBalance = transaction.getBalance() < 0;
				
				Label amount = new Label( transaction.getAmountAsString() );
				amount.setStyleName( neggativeAmount ? "red" : "green" );

				Label balance = new Label( transaction.getBalanceAsString() );
				balance.setStyleName( neggativeBalance ? "red" : "green" );

				result.setAmount( amount );
				result.setBalance( balance );
				
				result.setPurchase_number( transaction.getPurchase() != null ? transaction.getPurchase().getFormattedNumber() : "" );
				result.setInvoice_number( transaction.getInvoice() != null ? transaction.getInvoice().getFormattedNumber() : "" );
				
				result.setTarget( transaction.getTarget() != null ? transaction.getTarget().getName() : "" );
				result.setTransfer( transaction.getTransfer() != null ? Long.toString( transaction.getTransfer() ).toUpperCase() : "" );
			}
			else
			{
			}
		}

		return result;
	}
}
