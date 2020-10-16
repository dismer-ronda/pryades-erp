package es.pryades.erp.dashboard.tabs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.UI;

import es.pryades.erp.application.SelectTransferDlg;
import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dashboard.Dashboard;
import es.pryades.erp.dto.Account;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.Transaction;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.dto.query.TransactionQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.TransactionVto;
import es.pryades.erp.vto.controlers.TransactionControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class TransactionsTabContent extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = 9151147553455352570L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( TransactionsTabContent.class );

	private PopupDateField fromDateField;
	private PopupDateField toDateField;
	private ComboBox comboAccounts;
	private Button bttnApply;
	
	private UserDefault default_from;
	private UserDefault default_to;

	private List<Account> accounts;

	public TransactionsTabContent( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "id" );
		setOrder( "desc" );

		loadUserDefaults();
	}

	@Override
	public String getResourceKey()
	{
		return "transactionsTab";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "transaction_date", "transaction_type", "account_name", "description", "amount", "balance", "purchase_number", "invoice_number", "target", "transfer" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "transaction_date", "transaction_type", "account_name" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new BaseTable( TransactionVto.class, this, getContext() );//, getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	public List<Component> getCustomOperations()
	{
		List<Component> ops = new ArrayList<Component>();
		
		Button bttnPdf = new Button();
		bttnPdf.setCaption( getContext().getString( "transactionsTab.list.pdf" ) );
		bttnPdf.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -3218524176600265294L;

			public void buttonClick( ClickEvent event )
			{
				onShowListPdf();
			}
		} );
		ops.add( bttnPdf );

		Button bttnXls = new Button();
		bttnXls.setCaption( getContext().getString( "transactionsTab.list.xls" ) );
		ops.add( bttnXls );
		
		FileDownloader fileDownloaderXls = new FileDownloader( getXlsResource() );
        fileDownloaderXls.setOverrideContentType( true );
        fileDownloaderXls.extend( bttnXls );

		Button bttnTransfer = new Button();
		bttnTransfer.setCaption( getContext().getString( "transactionsTab.btnTransfer" ) );
		bttnTransfer.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -8959634811265682288L;

			public void buttonClick( ClickEvent event )
			{
				onTransfer();
			}
		} );
		ops.add( bttnTransfer );

		Button btnRollBack = new Button();
		btnRollBack.setCaption( getContext().getString( "transactionsTab.btnRollBack" ) );
		btnRollBack.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -9094113876674035044L;

			public void buttonClick( ClickEvent event )
			{
				onRollBack();
			}
		} );

		ops.add( btnRollBack );
		
		return ops;
	}

	@Override
	public Component getQueryComponent()
	{
		fromDateField = new PopupDateField( getContext().getString( "words.from" ) );
		fromDateField.setResolution( Resolution.DAY );
		fromDateField.setDateFormat( "dd-MM-yyyy" );
		fromDateField.setWidth( "160px" );
		fromDateField.setValue( getDefaultDate( default_from.getData_value() ) );

		toDateField = new PopupDateField( getContext().getString( "words.to" ) );
		toDateField.setResolution( Resolution.DAY );
		toDateField.setDateFormat( "dd-MM-yyyy" );
		toDateField.setWidth( "160px" );
		toDateField.setValue( getDefaultDate( default_to.getData_value() ) );
		
		loadAccounts();
		comboAccounts = new ComboBox(getContext().getString( "transactionsTab.comboAccounts" ));
		comboAccounts.setWidth( "100%" );
		comboAccounts.setNullSelectionAllowed( true );
		comboAccounts.setTextInputAllowed( true );
		comboAccounts.setImmediate( true );
		fillCombosAccounts();
		comboAccounts.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -5556137247051668082L;

			public void valueChange(ValueChangeEvent event) 
		    {
				refreshVisibleContent( true );
		    }
		});

		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		addButtonApplyFilterClickListener();

		HorizontalLayout rowQuery = new HorizontalLayout();
		rowQuery.setSpacing( true );
		rowQuery.addComponent( fromDateField );
		rowQuery.addComponent( toDateField );
		rowQuery.addComponent( comboAccounts );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		TransactionQuery query = new TransactionQuery();
		
		query.setFrom_date( fromDateField.getValue() != null ? CalendarUtils.getDayAsLong( fromDateField.getValue() ) : null );
		query.setTo_date( toDateField.getValue() != null ? CalendarUtils.getDayAsLong( toDateField.getValue() ) : null );
		
		query.setRef_account( comboAccounts.getValue() != null ? (Long)comboAccounts.getValue() : null );
		
		saveUserDefaults();

		return query;
	}

	@Override
	public void onOperationNew()
	{
		//new ModalNewTransaction( getContext(), OperationCRUD.OP_ADD, null, TransactionsTabContent.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		//new ModalNewInvoice( getContext(), OperationCRUD.OP_MODIFY, (Invoice)dto, TransactionsTabContent.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new TransactionControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Transaction();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._TransactionsManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
	}
	
	@Override
	public boolean hasAddRight()
	{
		return false; //getContext().hasRight( "configuration.transactions.add" );
	}

	@Override
	public void updateComponent()
	{
	}

	@Override
	public void onFieldEvent( Component component, String column )
	{
	}

	public void addButtonApplyFilterClickListener()
	{
		bttnApply.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -8959634811265682288L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				if ( CalendarUtils.checkValidPeriod( fromDateField.getValue(), toDateField.getValue() ) )
					refreshVisibleContent( true );
				else
					Utils.showNotification( getContext(), getContext().getString( "error.invalid.period" ), Notification.Type.ERROR_MESSAGE );
			}
		} );
	}

	private void loadUserDefaults()
	{
		default_from = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.TRANSACTIONS_FROM );
		default_to = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.TRANSACTIONS_TO );
	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_from, fromDateField.getValue() != null ? Long.toString( CalendarUtils.getDayAsLong( fromDateField.getValue() ) ) : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_to, toDateField.getValue() != null ? Long.toString( CalendarUtils.getDayAsLong( toDateField.getValue() ) ) : null );
	}
	
	private Date getDefaultDate( String date )
	{
		try
		{
			return CalendarUtils.getServerCalendarFromDateLong( Long.parseLong( date ) ).getTime();
		}
		catch ( Throwable e )
		{
		}
		
		return null;
	}
	
	private void onShowListPdf()
	{
		/*try
		{
			long ts = CalendarUtils.getTodayAsLong( "UTC" );
			
			String pagesize = "A4";
			String template = "list-invoices-template";
			String timeout = "0";
			
			InvoiceQuery query = (InvoiceQuery)getQueryObject();
			
			String extra = "ts=" + ts + 
					"&query=" + Utils.getUrlEncoded( Utils.toJson( query ) ) + 
					"&pagesize=" + pagesize + 
					"&language=" + getContext().getLanguage() +
					"&template=" + template +
					"&name=" + getContext().getString( "invoicesConfig.invoices" ) + "-" + query.getPeriodToString() +
					"&url=" + getContext().getData( "Url" ) +
					"&timeout=" + timeout;
			
			String user = getContext().getUser().getLogin();
			String password = getContext().getUser().getPwd();
			
			String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
			String code = "code=" + Authorization.encrypt( extra, password ) ;

			String url = getContext().getData( "Url" ) + "/services/invoices" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
			
			String caption = getContext().getString( "invoicesConfig.invoices" );

			ShowExternalUrlDlg dlg = new ShowExternalUrlDlg(); 
	
			dlg.setContext( getContext() );
			dlg.setUrl( url );
			dlg.setCaption( caption );
			dlg.createComponents();
			
			getUI().addWindow( dlg );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}*/
	}
	
	private StreamResource getXlsResource() 
	{
		StreamResource res =
			new StreamResource( 
				new StreamSource() 
				{
					private static final long serialVersionUID = -4001495054474088591L;

					@Override
		            public InputStream getStream() 
		            {
						try
						{
							return new ByteArrayInputStream( IOCManager._TransactionsManager.exportListXls( getContext(), (TransactionQuery)getQueryObject() ) );
						}
						catch ( Throwable e )
						{
							e.printStackTrace();
						}
						
						return null;
		            }
		        }, 
		        Utils.getUUID() + ".xls" );
		
		res.setCacheTime( 0 );
			
		return res;
	}

	private void onTransfer()
	{
		SelectTransferDlg dlg = new SelectTransferDlg( getContext() );
		getUI().addWindow( dlg );
	}

	private void onRollBack()
	{
		if ( comboAccounts.getValue() == null )
		{
			Utils.showNotification( getContext(), getContext().getString( "transactionsTab.rollbackAccount" ), Notification.Type.ERROR_MESSAGE );
			
			return;
		}
		
		ConfirmDialog.show( (UI)getContext().getData( "Application" ), getContext().getString( "transactionsTab.rollbackConfirmation" ),
		        new ConfirmDialog.Listener() 
				{
					private static final long serialVersionUID = 5728591968760994812L;

					public void onClose(ConfirmDialog dialog) 
		            {
		                if ( dialog.isConfirmed() ) 
		                {
		            		try 
		            		{
		            			Transaction transaction = (Transaction)IOCManager._TransactionsManager.rollbackTransaction( getContext(), (Long)comboAccounts.getValue() );
		            					
		            			if ( transaction != null )
		            			{
			            			Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
			            			dashboard.refreshTransactionsTab();
			            			
			            			if ( transaction.getTransaction_type().equals( Transaction.TYPE_PAYMENT ) )
			            				dashboard.refreshPurchasesTab();
			            			if ( transaction.getTransaction_type().equals( Transaction.TYPE_INCOME ) )
			            				dashboard.refreshInvoicesTab();
			            			
			            			String type = getContext().getString( "transaction.type." + transaction.getTransaction_type() );
			            			String message = getContext().getString( "transactionsTab.rollbackSuccess." + transaction.getTransaction_type() )
			            					.replaceAll( "%type%", type );
			            			
		            				Utils.showNotification( getContext(), transaction.getTransactionMessage( message ), Notification.Type.HUMANIZED_MESSAGE );
		            			}
		            			else
		            				Utils.showNotification( getContext(), getContext().getString( "transactionsTab.rollbackNothing" ), Notification.Type.HUMANIZED_MESSAGE );
		            		}
		            		catch ( Throwable e )
		            		{
	            				Utils.showNotification( getContext(), getContext().getString( "transactionsTab.rollbackError" ), Notification.Type.ERROR_MESSAGE );
		            			Utils.logException( e, LOG );
		            		}
		                } 
		            }
		        });
		
	}

	@SuppressWarnings("unchecked")
	private void loadAccounts()
	{
		try
		{
			Account query = new Account();
			
			accounts = IOCManager._AccountsManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			accounts = new ArrayList<Account>();
		}
	}
	
	private void fillCombosAccounts()
	{
		for ( Account account : accounts )
		{
			comboAccounts.addItem( account.getId() );
			comboAccounts.setItemCaption( account.getId(), account.getName() );
		}
	}
}

