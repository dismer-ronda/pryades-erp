package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.dal.ibatis.TransactionMapper;
import es.pryades.erp.dto.Transaction;
import es.pryades.erp.dto.query.TransactionQuery;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class TransactionsManagerImpl extends BaseManagerImpl implements TransactionsManager
{
	private static final long serialVersionUID = -6552397629714997331L;
	
	private static final Logger LOG = Logger.getLogger( TransactionsManagerImpl.class );

	public static BaseManager build()
	{
		return new TransactionsManagerImpl();
	}

	public TransactionsManagerImpl()
	{
		super( TransactionMapper.class, Transaction.class, LOG );
	}

	@Override
	public byte[] exportListXls( AppContext ctx, TransactionQuery query ) throws Throwable
	{
		return null;
	}

	@Override
	public boolean addTransaction( AppContext ctx, Transaction transaction ) throws Throwable
	{
		Transaction last = (Transaction)getLastRow( ctx, transaction );
		
		if ( last != null )
		{
			if ( transaction.getTransaction_type().equals( Transaction.TYPE_PAYMENT ) )
				transaction.setAmount( -1 * transaction.getAmount() );
			
			transaction.setBalance( last.getBalance() + transaction.getAmount() );
			
			setRow( ctx, null, transaction );

			if ( transaction.getTransaction_type().equals( Transaction.TYPE_PAYMENT ) )
				transaction.setAmount( -1 * transaction.getAmount() );

			return true;
		}
		
		return false;
	}
}
