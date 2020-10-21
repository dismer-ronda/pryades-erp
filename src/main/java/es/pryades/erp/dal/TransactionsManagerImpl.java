package es.pryades.erp.dal;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Constants;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.ibatis.TransactionMapper;
import es.pryades.erp.dto.Account;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.dto.Purchase;
import es.pryades.erp.dto.Transaction;
import es.pryades.erp.dto.query.InvoiceQuery;
import es.pryades.erp.dto.query.PurchaseQuery;
import es.pryades.erp.dto.query.TransactionQuery;
import es.pryades.erp.ioc.IOCManager;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class TransactionsManagerImpl extends BaseManagerImpl implements TransactionsManager
{
	private static final long serialVersionUID = -6552397629714997331L;
	
	private static final Logger LOG = Logger.getLogger( TransactionsManagerImpl.class );
	
	@Inject
	PurchasesManager purchasesManager;

	@Inject
	InvoicesManager invoicesManager;

	public static BaseManager build()
	{
		return new TransactionsManagerImpl();
	}

	public TransactionsManagerImpl()
	{
		super( TransactionMapper.class, Transaction.class, LOG );
	}

	private boolean creditExceeded( Account account, Transaction last, Transaction transaction )
	{
		double sign = ( transaction.getTransaction_type().equals( Transaction.TYPE_PAYMENT ) || transaction.getTransaction_type().equals( Transaction.TYPE_TRANSFER_SRC ) ) ? -1 : 1 ;
		
		double balance = Utils.roundDouble( last.getBalance() + sign * transaction.getAmount(), 2 );
		
		return balance < -1 * account.getCredit();
	}

	@Override
	public int addTransaction( AppContext ctx, Transaction transaction, Account account ) throws Throwable
	{
		TransactionQuery query = new TransactionQuery();
		query.setRef_account( transaction.getRef_account() );
		Transaction last = (Transaction)getLastRow( ctx, query );
		
		if ( last != null )
		{
			if ( creditExceeded( account, last, transaction ) )
				return Transaction.TRANSACTION_ERROR_CREDIT_LIMIT;
			
			if ( transaction.getTransaction_date() < last.getTransaction_date() )
				return Transaction.TRANSACTION_ERROR_DATE_BEFORE;
			
			if ( transaction.getTransaction_type().equals( Transaction.TYPE_PAYMENT ) )
				transaction.setAmount( -1 * transaction.getAmount() );
			
			transaction.setBalance( Utils.roundDouble( last.getBalance() + transaction.getAmount(), 2 ) );
			
			setRow( ctx, null, transaction );

			if ( transaction.getTransaction_type().equals( Transaction.TYPE_PAYMENT ) )
				transaction.setAmount( -1 * transaction.getAmount() );

			return Transaction.TRANSACTION_OK;
		}
		
		return Transaction.TRANSACTION_ERROR_NOT_INITIALIZED;
	}

	private void deleteInitTransaction( AppContext ctx, Transaction transaction ) throws Throwable
	{
		delRow( ctx, transaction );
	}
	
	private void deletePaymentTransaction( AppContext ctx, Transaction transaction ) throws Throwable
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		try 
		{
			PurchaseQuery queryPurchase = new PurchaseQuery();
			queryPurchase.setId( transaction.getRef_purchase() );
			Purchase purchase = (Purchase)purchasesManager.getRow( ctx, queryPurchase );

			Purchase clone = (Purchase)Utils.clone( purchase );
			
			purchase.setPayed( Utils.roundDouble( purchase.getPayed() + transaction.getAmount(), 2 ) );
			
			purchasesManager.setRow( ctx, clone, purchase );
			 
			delRow( ctx, transaction );
			
			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();
			
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
	}
	
	private void deleteIncomeTransaction( AppContext ctx, Transaction transaction ) throws Throwable
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		try 
		{
			InvoiceQuery queryInvoice = new InvoiceQuery();
			queryInvoice.setId( transaction.getRef_invoice() );
			Invoice invoice = (Invoice)invoicesManager.getRow( ctx, queryInvoice );

			Invoice clone = (Invoice)Utils.clone( invoice );
			
			invoice.setCollected( Utils.roundDouble( invoice.getCollected() - transaction.getAmount(), 2 ) );
			
			invoicesManager.setRow( ctx, clone, invoice );
			 
			delRow( ctx, transaction );
			
			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();
			
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
	}
	
	private void deleteTransferTransactions( AppContext ctx, Transaction transaction ) throws Throwable
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		try 
		{
			TransactionQuery query = new TransactionQuery();
			query.setRef_account( transaction.getRef_target() );
			Transaction peer = (Transaction)IOCManager._TransactionsManager.getLastRow( ctx, query );
			
			if ( transaction.getTransfer().equals( peer.getTransfer() ) )
			{
				IOCManager._TransactionsManager.delRow( ctx, transaction );
				IOCManager._TransactionsManager.delRow( ctx, peer );
			}
			else
				throw new Exception( "peer transaction not the at top" );

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();
			
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
	}
	
	public Transaction rollbackTransaction( AppContext ctx, Long account ) throws Throwable
	{
		TransactionQuery query = new TransactionQuery();
		query.setRef_account( account );
		Transaction lastTransaction = (Transaction)getLastRow( ctx, query );
		
		if ( lastTransaction != null )
		{
			switch ( lastTransaction.getTransaction_type() )
			{
				case Transaction.TYPE_INIT:
					deleteInitTransaction( ctx, lastTransaction );
				break;
				
				case Transaction.TYPE_PAYMENT:
					deletePaymentTransaction( ctx, lastTransaction );
				break;
				
				case Transaction.TYPE_INCOME:
					deleteIncomeTransaction( ctx, lastTransaction );
				break;

				case Transaction.TYPE_TRANSFER_DST:
				case Transaction.TYPE_TRANSFER_SRC:
					deleteTransferTransactions( ctx, lastTransaction );
				break;
			}
		}
		
		return lastTransaction;
	}
	
	public void addTransferTransaction( AppContext ctx, Transaction source, Transaction dest ) throws Throwable
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		try 
		{
			TransactionQuery querySource = new TransactionQuery();
			querySource.setRef_account( source.getRef_account() );
			Transaction lastSource = (Transaction)getLastRow( ctx, querySource );
			
			TransactionQuery queryDest = new TransactionQuery();
			queryDest.setRef_account( dest.getRef_account() );
			Transaction lastDest = (Transaction)getLastRow( ctx, queryDest );
			
			if ( lastSource != null && lastDest != null )
			{
				if ( creditExceeded( source.getAccount(), lastSource, source ) )
					throw new Exception( "Credit limit exceeded for account " + source.getAccount().getName() );
				
				long uid = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
				
				source.setRef_target( lastDest.getRef_account() );
				source.setTransfer( uid );
				source.setAmount( -1 * source.getAmount() );
				source.setBalance( Utils.roundDouble( lastSource.getBalance() + source.getAmount(), 2 ) );
				setRow( ctx, null, source );
				source.setAmount( -1 * source.getAmount() );

				dest.setRef_target( lastSource.getRef_account() );
				dest.setTransfer( uid );
				dest.setBalance( Utils.roundDouble( lastDest.getBalance() + dest.getAmount(), 2 ) );
				setRow( ctx, null, dest);
			}
			
			if ( finish )
				session.commit();

		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();
			
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			throw e;
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
	}

	public void exportAccountXls( AppContext ctx, Workbook workbook, Sheet sheet, Account account, Long from_date, Long to_date ) throws Throwable
	{
		TransactionQuery query = new TransactionQuery();
		query.setRef_account( account.getId() );
		query.setFrom_date( from_date );
		query.setTo_date( to_date );
		query.setOrder( "desc");
		query.setOrderby( "id");
		
		@SuppressWarnings("unchecked")
		List<Transaction> transactions = IOCManager._TransactionsManager.getRows( ctx, query );
		
		if ( transactions.size() > 0 )
		{
	        CellStyle styleHeader = workbook.createCellStyle();
	        Font fontHeader = workbook.createFont();
	        fontHeader.setBoldweight( Font.BOLDWEIGHT_BOLD );
	        styleHeader.setAlignment(CellStyle.ALIGN_CENTER );
	        styleHeader.setFont( fontHeader );
	
	        CellStyle styleFooter = workbook.createCellStyle();
	        Font fontFooter = workbook.createFont();
	        fontFooter.setBoldweight( Font.BOLDWEIGHT_BOLD );
	        styleFooter.setAlignment(CellStyle.ALIGN_RIGHT );
	        styleFooter.setFont( fontFooter);
	
	        CellStyle styleCurrency = workbook.createCellStyle();
	        Font fontCurrency = workbook.createFont();
	        styleCurrency.setAlignment(CellStyle.ALIGN_RIGHT );
	        styleCurrency.setFont( fontCurrency );
	        styleCurrency.setDataFormat( workbook.createDataFormat().getFormat( Constants.moneyFormat ) );
	
	        CellStyle styleFooterCurrency = workbook.createCellStyle();
	        Font fontFooterCurrency = workbook.createFont();
	        fontFooterCurrency.setBoldweight( Font.BOLDWEIGHT_BOLD );
	        styleFooterCurrency.setAlignment(CellStyle.ALIGN_RIGHT );
	        styleFooterCurrency.setFont( fontFooter);
	        styleFooterCurrency.setDataFormat( workbook.createDataFormat().getFormat( Constants.moneyFormat ) );
	
			String company = ctx.getOwner().getName() + " - " + ctx.getOwner().getTax_id();
			
			if ( account.getAccount_type().equals( Account.TYPE_PROVIDER ) )
				company = account.getCompany().getName() + " - " + account.getCompany().getTax_id();
	
			int i = sheet.getLastRowNum() + 1;
			
	        Row sheetRow = sheet.createRow( i++ );
	        Cell cell = sheetRow.createCell( 0 );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.transactions.company" ) );
			cell = sheetRow.createCell( 1 );
			cell.setCellValue( company );
			sheet.addMergedRegion( new CellRangeAddress( i-1, i-1, 1, 7 ) );
	
			sheetRow = sheet.createRow( i++ );
			cell = sheetRow.createCell( 0 );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.transactions.account" ) );
			cell = sheetRow.createCell( 1 );
			cell.setCellValue( account != null ? account.getName() + " - " + account.getNumber() : "" );
			sheet.addMergedRegion( new CellRangeAddress( i-1, i-1, 1, 7 ) );
	
			//sheet.createRow( i++ );
			
			sheetRow = sheet.createRow( i++ );
			int j = 0;
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.transactions.date" ) );
	
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.transactions.type" ) );
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.transactions.amount" ) );
	
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.transactions.balance" ) );
	
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.transactions.concept" ) );
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.transactions.customer.provider" ) );
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.transactions.purchase" ) );
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.transactions.invoice" ) );
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.transactions.target" ) );
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.transactions.transfer" ) );
			
	    	for ( Transaction transaction : transactions )
	    	{
				sheetRow = sheet.createRow( i++ );
				
				j = 0;
				cell = sheetRow.createCell( j++ );
				cell.setCellValue( transaction.getFormattedDate() );
	
				cell = sheetRow.createCell( j++ );
				cell.setCellValue( ctx.getString( "transaction.type." + transaction.getTransaction_type() ) );
				
				cell = sheetRow.createCell( j++ );
		        cell.setCellStyle(styleCurrency);
	
				cell.setCellValue( transaction.getAmount() );
	
				cell = sheetRow.createCell( j++ );
		        cell.setCellStyle(styleCurrency);
				cell.setCellValue( transaction.getBalance() );
				
				cell = sheetRow.createCell( j++ );
		        cell.setCellStyle(styleHeader);
				
				switch ( transaction.getTransaction_type() )
				{
					case Transaction.TYPE_PAYMENT:
						cell = sheetRow.createCell( j++ );
						cell.setCellValue( transaction.getPurchase() != null ? transaction.getPurchase().getTitle() : "" );
					break;
	
					case Transaction.TYPE_INCOME:
						cell = sheetRow.createCell( j++ );
						cell.setCellValue( transaction.getInvoice() != null ? transaction.getInvoice().getTitle() : "" );
					break;
	
					default:
						cell = sheetRow.createCell( j++ );
						cell.setCellValue( transaction.getDescription() );
				}
	
				switch ( transaction.getTransaction_type() )
				{
					case Transaction.TYPE_PAYMENT:
						cell = sheetRow.createCell( j++ );
						cell.setCellValue( transaction.getPurchase() != null ? transaction.getPurchase().getProvider().getName() : "" );
					break;
	
					case Transaction.TYPE_INCOME:
						cell = sheetRow.createCell( j++ );
						cell.setCellValue( transaction.getInvoice() != null ? transaction.getInvoice().getQuotation().getCustomer().getName() : "" );
					break;
	
					default:
						cell = sheetRow.createCell( j++ );
						cell.setCellValue( "" );
				}
	
				cell = sheetRow.createCell( j++ );
				cell.setCellValue( transaction.getPurchase() != null ? transaction.getPurchase().getFormattedNumber(): "" );
				
				cell = sheetRow.createCell( j++ );
				cell.setCellValue( transaction.getInvoice() != null ? transaction.getInvoice().getFormattedNumber() : "" );
				
				cell = sheetRow.createCell( j++ );
				cell.setCellValue( transaction.getTarget() != null ? transaction.getTarget().getName() : "" );
				
				cell = sheetRow.createCell( j++ );
				cell.setCellValue( transaction.getTarget() != null ? transaction.getTransfer().toString().toUpperCase() : "" );
			}
	
			for ( int col = 0; col < j; col++ )
				sheet.autoSizeColumn( col );
			
			sheet.createRow( i++ );
		}
	}

	@Override
	public byte[] exportListXls( AppContext ctx, TransactionQuery query ) throws Throwable
	{
		query.setOrderby( "id" );
		query.setOrder( "desc" );

		@SuppressWarnings("resource")
		Workbook workbook = new XSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
		
        CellStyle styleHeader = workbook.createCellStyle();
        Font fontHeader = workbook.createFont();
        fontHeader.setBoldweight( Font.BOLDWEIGHT_BOLD );
        styleHeader.setAlignment(CellStyle.ALIGN_CENTER );
        styleHeader.setFont( fontHeader );

        CellStyle styleFooter = workbook.createCellStyle();
        Font fontFooter = workbook.createFont();
        fontFooter.setBoldweight( Font.BOLDWEIGHT_BOLD );
        styleFooter.setAlignment(CellStyle.ALIGN_RIGHT );
        styleFooter.setFont( fontFooter);

        CellStyle styleCurrency = workbook.createCellStyle();
        Font fontCurrency = workbook.createFont();
        styleCurrency.setAlignment(CellStyle.ALIGN_RIGHT );
        styleCurrency.setFont( fontCurrency );
        styleCurrency.setDataFormat( workbook.createDataFormat().getFormat( Constants.moneyFormat ) );

        CellStyle styleFooterCurrency = workbook.createCellStyle();
        Font fontFooterCurrency = workbook.createFont();
        fontFooterCurrency.setBoldweight( Font.BOLDWEIGHT_BOLD );
        styleFooterCurrency.setAlignment(CellStyle.ALIGN_RIGHT );
        styleFooterCurrency.setFont( fontFooter);
        styleFooterCurrency.setDataFormat( workbook.createDataFormat().getFormat( Constants.moneyFormat ) );

		int i = 0;
		
		Row sheetRow = sheet.createRow( i++ );
		Cell cell = sheetRow.createCell( 0 );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.transactions" ) );
		sheet.addMergedRegion( new CellRangeAddress( i-1, i-1, 0, 7 ) );

		sheet.createRow( i++ );

		sheetRow = sheet.createRow( i++ );
		cell = sheetRow.createCell( 0 );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.transactions.query" ) );
		cell = sheetRow.createCell( 1 );
		cell.setCellValue( query.getPeriodToString() );
		sheet.addMergedRegion( new CellRangeAddress( i-1, i-1, 1, 7 ) );

		if ( query.getRef_account() != null )
		{
			Account queryAccount = new Account();
			queryAccount.setId( query.getRef_account() );
			Account account = (Account)IOCManager._AccountsManager.getRow( ctx, queryAccount );
			
			exportAccountXls( ctx, workbook, sheet, account, query.getFrom_date(), query.getTo_date() );
		}
		else
		{
			@SuppressWarnings("unchecked")
			List<Account> accounts = IOCManager._AccountsManager.getRows( ctx, new Account() );
			
			for ( Account account : accounts )
				exportAccountXls( ctx, workbook, sheet, account, query.getFrom_date(), query.getTo_date() );
		}
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		workbook.write( os );
		
		return os.toByteArray();
	}
}
