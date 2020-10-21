package es.pryades.erp.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.Account;
import es.pryades.erp.dto.Purchase;
import es.pryades.erp.dto.Transaction;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.ioc.IOCManager;
import lombok.Getter;
import lombok.Setter;

public class SelectPaymentDlg extends Window 
{
	private static final long serialVersionUID = 5927622002524143461L;

	private VerticalLayout layout;

	@Getter	private PopupDateField paymentDateField;
	private ComboBox comboAccounts;
	private TextField editAmount;
	private Label labelBalance;
	
	@Getter
	private AppContext context;
	
	@Getter
	private Purchase purchase;
	private List<Account> accounts;
	private Transaction transaction;
	private BeanItem<Transaction> bi;
	
	@Getter @Setter
	private boolean success = false;

	private UserDefault default_account;
	private UserDefault default_date;
	
	public SelectPaymentDlg( AppContext ctx, String title, Transaction transaction )
	{
		super( title );
		
		this.context = ctx;
		this.transaction = transaction;
		
		setContent( layout = new VerticalLayout() );
		
		layout.setMargin( true );
		layout.setSpacing( true );
		layout.setSizeUndefined();
		
		bi = new BeanItem<Transaction>( transaction);
		
		loadUserDefaults();
		
		transaction.setRef_account( getDefaultAccount() );
		transaction.setTransaction_date( getDefaultDate() );
 
		addComponents();
		
		center();
		
		setModal( true );
		setResizable( false );
		setClosable( true );
	}

	private void addComponents() 
	{
		paymentDateField = new PopupDateField(getContext().getString( "selectTransactionDlg.popupDate" ));
		paymentDateField.setResolution( Resolution.DAY );
		paymentDateField.setDateFormat( "dd-MM-yyyy" );
		paymentDateField.setWidth( "100%" );
		paymentDateField.setRequired( true );
		paymentDateField.setValue( CalendarUtils.getDateFromString( Long.toString( transaction.getTransaction_date() ), "yyyyMMddHHmmss" ) );
		paymentDateField.setRequiredError( getContext().getString( "words.required" ) );
		paymentDateField.setInvalidCommitted( true );
		
		loadAccounts();
		comboAccounts = new ComboBox(getContext().getString( "selectTransactionDlg.comboAccounts" ));
		comboAccounts.setWidth( "100%" );
		comboAccounts.setNullSelectionAllowed( false );
		comboAccounts.setTextInputAllowed( true );
		comboAccounts.setImmediate( true );
		fillComboAccounts();
		comboAccounts.setPropertyDataSource( bi.getItemProperty( "ref_account" ) );
		comboAccounts.setRequired( true );
		comboAccounts.setRequiredError( getContext().getString( "words.required" ) );
		comboAccounts.setInvalidCommitted( true );
		comboAccounts.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -2157047517750609170L;

			public void valueChange(ValueChangeEvent event) 
		    {
		        onSelectedAccount();
		    }
		});
		
		editAmount = new TextField( getContext().getString( "selectTransactionDlg.editAmount" ), bi.getItemProperty( "amount" ) );
		editAmount.setWidth( "100%" );
		editAmount.setRequired( true );
		editAmount.setRequiredError( getContext().getString( "words.required" ) );
		editAmount.setNullRepresentation( "" );
		editAmount.setInvalidCommitted( true );

		labelBalance = new Label();
		labelBalance.setWidth( "100%" );
		labelBalance.setValue( getSelectedAccountBalance() );
		labelBalance.addStyleName( "centered" );

		Button button1 = new Button( getContext().getString( "words.ok" ) );
		button1.setClickShortcut( KeyCode.ENTER );
		button1.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = -5815887453692195238L;

			public void buttonClick(ClickEvent event) 
            {
				onOk(); 
            }
        });
		
		
		Button button2 = new Button( getContext().getString( "words.cancel" ) );
		button2.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = 772726976260836023L;

			public void buttonClick(ClickEvent event) 
            {
				close();
            }
        });
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( paymentDateField );
		row1.addComponent( comboAccounts);
		row1.addComponent( editAmount);
		row1.addComponent( labelBalance );
		row1.setComponentAlignment( labelBalance, Alignment.BOTTOM_LEFT );
		
		HorizontalLayout row4 = new HorizontalLayout();
		row4.setSpacing( true );
		row4.addComponent( button1 );
		row4.addComponent( button2 );

		layout.addComponent( row1 );
		layout.addComponent( row4 );
		layout.setComponentAlignment( row4, Alignment.BOTTOM_RIGHT );
	}

	private boolean checkValidValues()
	{
		if ( paymentDateField.getValue() == null ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransactionDlg.invalidDate" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}
		
		if ( paymentDateField.getValue().after( new Date() ) ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransactionDlg.invalidDate" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}
		
		if ( comboAccounts.getValue() == null ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransactionDlg.invalidAccount" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}

		if ( transaction.getAmount() == null ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransactionDlg.invalidAmount" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}

		if ( Utils.roundDouble( transaction.getAmount(), 2 ) == 0 ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransactionDlg.invalidAmount" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}

		return true;
	}
	
	private void onOk()
	{
		if ( checkValidValues() )
		{
			transaction.setTransaction_date( CalendarUtils.getDayAsLong( paymentDateField.getValue() ) ); 
			
			try
			{
				int result = IOCManager._TransactionsManager.addTransaction( getContext(), transaction, getAccount() );
				
				if ( result == Transaction.TRANSACTION_OK  )
				{
					success = true;

					String type = getContext().getString( "transaction.type." + transaction.getTransaction_type() );
        			String message = getContext().getString( "selectTransactionDlg.success." + transaction.getTransaction_type() )
        					.replaceAll( "%type%", type );
        			
    				Utils.showNotification( getContext(), transaction.getTransactionMessage( message ), Notification.Type.HUMANIZED_MESSAGE );

					saveUserDefaults();

					close();
				}
				else
				{
					Utils.showNotification( getContext(), getContext().getString( "selectTransactionDlg.error." + result ), Notification.Type.ERROR_MESSAGE );
				}
			}
			catch ( Throwable e )
			{
				Utils.showNotification( getContext(), getContext().getString( "selectTransactionDlg.error." + Transaction.TRANSACTION_ERROR_EXCEPTION ), Notification.Type.ERROR_MESSAGE );

				e.printStackTrace();
			}
		}
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
	
	private void fillComboAccounts()
	{
		for ( Account account : accounts )
		{
			if ( account.getTransactions().size() > 0 )
			{
				comboAccounts.addItem( account.getId() );
				comboAccounts.setItemCaption( account.getId(), account.getName() );
			}
		}
	}

	private Long getDefaultAccount() 
	{
		try
		{
			return Long.parseLong( default_account.getData_value() );
		}
		catch ( Throwable e )
		{
		}
		
		return null;
	}

	private Long getDefaultDate() 
	{
		try
		{
			return Long.parseLong( default_date.getData_value() );
		}
		catch ( Throwable e )
		{
		}
		
		return CalendarUtils.getDayAsLong( new Date() );
	}

	private Account getAccount()
	{
		if ( comboAccounts.getValue() != null )
		{
			for ( Account account : accounts )
				if ( account.getId().equals( (Long)comboAccounts.getValue() ) )
					return account;
		}
		
		return null;
	}

	private void loadUserDefaults()
	{
		default_account = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.TRANSACTION_ACCOUNT );
		default_date = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.TRANSACTION_DATE );

	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_account, comboAccounts.getValue() != null ? comboAccounts.getValue().toString() : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_date, paymentDateField.getValue() != null ? Long.toString( CalendarUtils.getDayAsLong( paymentDateField.getValue() ) ) : null );
	}

	private String getSelectedAccountBalance()
	{
		Account account = getAccount();
		
		if ( account != null )
		{
			List<Transaction> transactions = account.getTransactions();
			
			if ( transactions.size() > 0 )
				return getContext().getString( "selectTransactionDlg.labelBalance" ) + " " + Utils.getFormattedCurrency( transactions.get( 0 ).getBalance() );
		}
		
		return "";
	}
	
	private void onSelectedAccount()
	{
		labelBalance.setValue( getSelectedAccountBalance() );
	}
}
