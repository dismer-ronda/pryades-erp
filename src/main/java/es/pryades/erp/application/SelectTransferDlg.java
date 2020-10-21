package es.pryades.erp.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

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
import es.pryades.erp.dashboard.Dashboard;
import es.pryades.erp.dto.Account;
import es.pryades.erp.dto.Transaction;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.ioc.IOCManager;
import lombok.Getter;
import lombok.Setter;

public class SelectTransferDlg extends Window 
{
	private static final long serialVersionUID = -5043233065969550675L;

	private static final Logger LOG = Logger.getLogger( SelectTransferDlg.class );
	
	private VerticalLayout layout;

	@Getter	private PopupDateField srcDateField;
	@Getter	private PopupDateField dstDateField;
	
	private ComboBox srcComboAccounts;
	private ComboBox dstComboAccounts;
	
	private TextField editAmount;
	private TextField editDescription;
	
	private Label labelSrcBalance;
	private Label labelDstBalance;

	@Getter
	private AppContext context;
	
	@Getter
	private List<Account> accounts;
	
	@Getter @Setter
	private boolean success = false;
	
	@Getter @Setter private Double amount;
	@Getter @Setter private String description;
	@Getter @Setter private Long ref_source;
	@Getter @Setter private Long ref_dest;
		
	@SuppressWarnings("rawtypes")
	private BeanItem bi = new BeanItem<SelectTransferDlg>( this );

	private UserDefault default_from_date;
	private UserDefault default_to_date;

	public SelectTransferDlg( AppContext ctx )
	{
		super( ctx.getString( "selectTransferDlg.title" ) );
		
		this.context = ctx;
		
		setContent( layout = new VerticalLayout() );
		
		layout.setMargin( true );
		layout.setSpacing( true );
		layout.setSizeUndefined();
		
		loadUserDefaults();
		
		addComponents();
		
		center();
		
		setModal( true );
		setResizable( false );
		setClosable( true );
	}

	private void addComponents() 
	{
		srcDateField = new PopupDateField(getContext().getString( "selectTransferDlg.srcDateField" ));
		srcDateField.setWidth( "160px" );
		srcDateField.setResolution( Resolution.DAY );
		srcDateField.setDateFormat( "dd-MM-yyyy" );
		srcDateField.setRequired( true );
		srcDateField.setRequiredError( getContext().getString( "words.required" ) );
		srcDateField.setValue( getDefaultFromDate() );
		srcDateField.setInvalidCommitted( true );
		
		dstDateField = new PopupDateField(getContext().getString( "selectTransferDlg.dstDateField" ));
		dstDateField.setWidth( "160px" );
		dstDateField.setResolution( Resolution.DAY );
		dstDateField.setDateFormat( "dd-MM-yyyy" );
		dstDateField.setRequired( true );
		dstDateField.setRequiredError( getContext().getString( "words.required" ) );
		dstDateField.setValue( getDefaultToDate() );
		dstDateField.setInvalidCommitted( true );
		
		loadAccounts();

		srcComboAccounts = new ComboBox(getContext().getString( "selectTransferDlg.srcComboAccounts" ));
		srcComboAccounts.setWidth( "320px" );
		srcComboAccounts.setNullSelectionAllowed( false );
		srcComboAccounts.setTextInputAllowed( true );
		srcComboAccounts.setImmediate( true );
		srcComboAccounts.setRequired( true );
		srcComboAccounts.setRequiredError( getContext().getString( "words.required" ) );
		srcComboAccounts.setInvalidCommitted( true );
		srcComboAccounts.setPropertyDataSource( bi.getItemProperty( "ref_source" ) );
		srcComboAccounts.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = 1724286312489796801L;

			public void valueChange(ValueChangeEvent event) 
		    {
		        onSelectedSrcAccount();
		    }
		});

		dstComboAccounts = new ComboBox(getContext().getString( "selectTransferDlg.dstComboAccounts" ));
		dstComboAccounts.setWidth( "320px" );
		dstComboAccounts.setNullSelectionAllowed( false );
		dstComboAccounts.setTextInputAllowed( true );
		dstComboAccounts.setImmediate( true );
		dstComboAccounts.setRequired( true );
		dstComboAccounts.setRequiredError( getContext().getString( "words.required" ) );
		dstComboAccounts.setInvalidCommitted( true );
		dstComboAccounts.setPropertyDataSource( bi.getItemProperty( "ref_dest" ) );
		dstComboAccounts.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = 7794937955321613757L;

			public void valueChange(ValueChangeEvent event) 
		    {
		        onSelectedDstAccount();
		    }
		});

		fillCombosAccounts();

		editDescription = new TextField( getContext().getString( "selectTransferDlg.editDescription" ), bi.getItemProperty( "description" ) );
		editDescription.setWidth( "100%" );
		editDescription.setNullRepresentation( "" );
		
		editAmount = new TextField( getContext().getString( "selectTransferDlg.editAmount" ), bi.getItemProperty( "amount" ) );
		editAmount.setWidth( "160px" );
		editAmount.setRequired( true );
		editAmount.setRequiredError( getContext().getString( "words.required" ) );
		editAmount.setNullRepresentation( "" );
		editAmount.setInvalidCommitted( true );
		
		labelSrcBalance = new Label();
		labelSrcBalance.setWidth( "100%" );
		labelSrcBalance.setValue( getSelectedAccountBalance( srcComboAccounts ) );

		labelDstBalance = new Label();
		labelDstBalance.setWidth( "100%" );
		labelDstBalance.setValue( getSelectedAccountBalance( dstComboAccounts ) );
		
		Button button1 = new Button( getContext().getString( "words.ok" ) );
		button1.setClickShortcut( KeyCode.ENTER );
		button1.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = 2520680858918130646L;

			public void buttonClick(ClickEvent event) 
            {
				onOk(); 
            }
        });
		
		Button button2 = new Button( getContext().getString( "words.cancel" ) );
		button2.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = -4974071906385441706L;

			public void buttonClick(ClickEvent event) 
            {
				close();
            }
        });
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( srcDateField );
		row1.addComponent( srcComboAccounts );
		row1.addComponent( labelSrcBalance );
		row1.setComponentAlignment( labelSrcBalance, Alignment.BOTTOM_LEFT );
		row1.setExpandRatio( labelSrcBalance, 1.0f );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( dstDateField );
		row2.addComponent( dstComboAccounts );
		row2.addComponent( labelDstBalance );
		row2.setComponentAlignment( labelDstBalance, Alignment.BOTTOM_LEFT );
		row2.setExpandRatio( labelDstBalance, 1.0f );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( editDescription );
		row3.addComponent( editAmount );
		row3.setExpandRatio( editDescription, 1.0f );

		HorizontalLayout row4 = new HorizontalLayout();
		row4.setSpacing( true );
		row4.addComponent( button1 );
		row4.addComponent( button2 );

		layout.addComponent( row1 );
		layout.addComponent( row2 );
		layout.addComponent( row3 );
		layout.addComponent( row4 );
		layout.setComponentAlignment( row4, Alignment.BOTTOM_RIGHT );
	}

	private boolean checkValidValues()
	{
		if ( srcDateField.getValue() == null ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransferDlg.invalidSrcDate" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}
		
		if ( srcDateField.getValue().after( new Date() ) ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransferDlg.invalidSrcDate" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}
		
		if ( dstDateField.getValue() == null ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransferDlg.invalidDstDate" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}
		
		if ( dstDateField.getValue().after( new Date() ) ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransferDlg.invalidDstDate" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}
		
		if ( srcComboAccounts.getValue() == null ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransferDlg.invalidSrcAccount" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}

		if ( dstComboAccounts.getValue() == null ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransferDlg.invalidDstAccount" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}

		if ( srcComboAccounts.getValue().equals( dstComboAccounts.getValue() ) ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransferDlg.sameAccount" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}


		if ( editAmount.getValue() == null ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransferDlg.invalidAmount" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}

		/*if ( Utils.getDouble( editAmount.getValue(), 0 ) == 0 ) 
		{
			Utils.showNotification( getContext(), getContext().getString( "selectTransferDlg.invalidAmount" ), Notification.Type.ERROR_MESSAGE );
			
			return false;
		}*/

		return true;
	}
	
	private void onOk()
	{
		if ( checkValidValues() )
		{
			try
			{
				LOG.info( "amount " + amount );
				//double amount = Utils.roundDouble( Utils.getDouble( editAmount.getValue(), 0 ), 2 );
				
				Transaction source = new Transaction();
				source.setTransaction_type( Transaction.TYPE_TRANSFER_SRC );
				source.setRef_account( (Long)srcComboAccounts.getValue() );
				source.setAccount( getAccount( srcComboAccounts ) );
				source.setTransaction_date( CalendarUtils.getDayAsLong( srcDateField.getValue() ) );
				source.setAmount( amount );
				source.setDescription( editDescription.getValue() );
				
				Transaction dest = new Transaction();
				dest.setTransaction_type( Transaction.TYPE_TRANSFER_DST );
				dest.setRef_account( (Long)dstComboAccounts.getValue() );
				dest.setAccount( getAccount( dstComboAccounts ) );
				dest.setTransaction_date( CalendarUtils.getDayAsLong( dstDateField.getValue() ) );
				dest.setAmount( amount );
				dest.setDescription( editDescription.getValue() );
				
				IOCManager._TransactionsManager.addTransferTransaction( getContext(), source, dest );
				
				Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
				dashboard.refreshTransactionsTab();

				success = true;
				
				String type = getContext().getString( "transaction.type." + source.getTransaction_type() );
    			String message = getContext().getString( "selectTransferDlg.success." + source.getTransaction_type() )
    					.replaceAll( "%type%", type );
    			
				Utils.showNotification( getContext(), source.getTransactionMessage( message ), Notification.Type.HUMANIZED_MESSAGE );

				saveUserDefaults();

				close();
			}
			catch ( Throwable e )
			{
				Utils.showNotification( getContext(), getContext().getString( "selectTransferDlg.error" ), Notification.Type.ERROR_MESSAGE );

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
	
	private void fillCombosAccounts()
	{
		for ( Account account : accounts )
		{
			if ( account.getTransactions().size() > 0 )
			{
				srcComboAccounts.addItem( account.getId() );
				srcComboAccounts.setItemCaption( account.getId(), account.getName() );
	
				dstComboAccounts.addItem( account.getId() );
				dstComboAccounts.setItemCaption( account.getId(), account.getName() );
			}
		}
	}
	
	private Account getAccount( ComboBox combo )
	{
		if ( combo.getValue() != null )
		{
			for ( Account account : accounts )
				if ( account.getId().equals( (Long)combo.getValue() ) )
					return account;
		}
		
		return null;
	}

	private void loadUserDefaults()
	{
		default_from_date = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.TRANSFER_FROM_DATE );
		default_to_date = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.TRANSFER_TO_DATE );

	}

	private void saveUserDefaults()
	{
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_from_date, srcDateField.getValue() != null ? Long.toString( CalendarUtils.getDayAsLong( srcDateField.getValue() ) ) : null );
		IOCManager._UserDefaultsManager.setUserDefault( getContext(), default_to_date, dstDateField.getValue() != null ? Long.toString( CalendarUtils.getDayAsLong( dstDateField.getValue() ) ) : null );
	}
	
	private Date getDefaultFromDate() 
	{
		try
		{
			return CalendarUtils.getDateFromString( default_from_date.getData_value(), "yyyyMMddHHmmss" );
		}
		catch ( Throwable e )
		{
		}
		
		return new Date();
	}

	private Date getDefaultToDate() 
	{
		try
		{
			return CalendarUtils.getDateFromString( default_to_date.getData_value(), "yyyyMMddHHmmss" );
		}
		catch ( Throwable e )
		{
		}
		
		return new Date();
	}
	
	private String getSelectedAccountBalance( ComboBox combo )
	{
		Account account = getAccount( combo );
		
		if ( account != null )
		{
			List<Transaction> transactions = account.getTransactions();
			
			if ( transactions.size() > 0 )
				return getContext().getString( "selectTransferDlg.labelBalance" ) + " " + Utils.getFormattedCurrency( transactions.get( 0 ).getBalance() );
		}
		
		return "";
	}
	
	private void onSelectedSrcAccount()
	{
		labelSrcBalance.setValue( getSelectedAccountBalance( srcComboAccounts ) );
	}

	private void onSelectedDstAccount()
	{
		labelDstBalance.setValue( getSelectedAccountBalance( dstComboAccounts ) );
	}
}
