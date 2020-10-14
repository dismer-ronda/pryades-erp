package es.pryades.erp.configuration.modals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import es.pryades.erp.application.SelectNumberDlg;
import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dashboard.Dashboard;
import es.pryades.erp.dto.Account;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Company;
import es.pryades.erp.dto.Transaction;
import es.pryades.erp.dto.query.CompanyQuery;
import es.pryades.erp.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewAccount extends ModalWindowsCRUD 
{
	private static final long serialVersionUID = -8976581299831039339L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalNewAccount.class );

	private List<Company> companies;

	protected Account newCompany;

	private ComboBox comboCompanies;
	private ComboBox comboTypes;
	private TextField editNumber;
	private TextField editName;

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewAccount( AppContext context, OperationCRUD modalOperation, Account orgParameter, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgParameter );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		setWidth( "1024px" );

		try
		{
			newCompany = (Account) Utils.clone( (Account) orgDto );
		}
		catch ( Throwable e1 )
		{
			newCompany = new Account();
			newCompany.setAccount_type( Account.TYPE_BANK);
		}

		bi = new BeanItem<BaseDto>( newCompany );

		comboTypes = new ComboBox(getContext().getString( "modalNewAccount.comboTypes" ));
		comboTypes.setWidth( "100%" );
		comboTypes.setNullSelectionAllowed( false );
		comboTypes.setTextInputAllowed( false );
		comboTypes.setImmediate( true );
		comboTypes.setRequired( true );
		comboTypes.setRequiredError( getContext().getString( "words.required" ) );
		fillComboTypes();
		comboTypes.setPropertyDataSource( bi.getItemProperty( "account_type" ) );

		loadCompanies();
		comboCompanies = new ComboBox(getContext().getString( "modalNewAccount.comboCompanies" ));
		comboCompanies.setWidth( "100%" );
		comboCompanies.setNullSelectionAllowed( true );
		comboCompanies.setTextInputAllowed( true );
		comboCompanies.setImmediate( true );
		fillComboCompanies();
		comboCompanies.setPropertyDataSource( bi.getItemProperty( "ref_company" ) );
		
		editName = new TextField( getContext().getString( "modalNewAccount.editName" ), bi.getItemProperty( "name" ) );
		editName.setWidth( "100%" );
		editName.setNullRepresentation( "" );
		editName.setRequired( true );
		editName.setRequiredError( getContext().getString( "words.required" ) );
		editName.setInvalidCommitted( true );
		
		editNumber = new TextField( getContext().getString( "modalNewAccount.editNumber" ), bi.getItemProperty( "number" ) );
		editNumber.setWidth( "100%" );
		editNumber.setNullRepresentation( "" );
		editNumber.setRequired( true );
		editNumber.setRequiredError( getContext().getString( "words.required" ) );
		editNumber.setInvalidCommitted( true );
		
		Button btnInit = new Button();
		btnInit.setCaption( getContext().getString( "modalNewAccount.btnInit" ) );
		btnInit.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 6009616552424331249L;

			public void buttonClick( ClickEvent event )
			{
				onInitAccount();
			}
		} );

		getDefaultOperationsRow().addComponentAsFirst( btnInit );
		getDefaultOperationsRow().setComponentAlignment( btnInit, Alignment.MIDDLE_LEFT );

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( comboTypes );
		row1.addComponent( comboCompanies );
		row1.addComponent( editName );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( editNumber );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewAccount";
	}

	@Override
	protected void defaultFocus()
	{
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newCompany.setId( null );

			IOCManager._AccountsManager.setRow( getContext(), null, newCompany );
			
			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onModify()
	{
		try
		{
			IOCManager._AccountsManager.setRow( getContext(), (Account) orgDto, newCompany );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onDelete()
	{
		try
		{
			IOCManager._AccountsManager.delRow( getContext(), newCompany );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	private void fillComboTypes()
	{
		for ( int i = Account.TYPE_BANK; i <= Account.TYPE_PROVIDER; i++ )
		{
			comboTypes.addItem( i );
			comboTypes.setItemCaption( i, getContext().getString( "account.type." + i ) );
		}	
	}
	
	@Override
	protected boolean hasDelete()
	{
		return getContext().hasRight( "configuration.accounts.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.accounts.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.accounts.modify" );
	}
	
	@SuppressWarnings("unchecked")
	private void loadCompanies()
	{
		try
		{
			CompanyQuery query = new CompanyQuery();
			
			companies = IOCManager._CompaniesManager.getRows( getContext(), query );
		}
		catch ( BaseException e )
		{
			e.printStackTrace();
			companies = new ArrayList<Company>();
		}
	}
	
	private void fillComboCompanies()
	{
		for ( Company company : companies )
		{
			comboCompanies.addItem( company.getId() );
			comboCompanies.setItemCaption( company.getId(), company.getAlias() );
		}
	}
	
	private void onInitAccount()
	{
		final SelectNumberDlg dlg = new SelectNumberDlg( getContext(), getContext().getString( "modalNewAccount.initialization" ) );
		dlg.addCloseListener
		( 
			new Window.CloseListener() 
			{
				private static final long serialVersionUID = 6401304507483980881L;

				@Override
			    public void windowClose( CloseEvent e ) 
			    {
					String value = dlg.getValue();
					
					if ( value != null && !value.isEmpty() )
					{
						try
						{
							Double balance = Utils.getDouble( value, 0 );
							
							Transaction transaction = new Transaction();
							transaction.setTransaction_type( Transaction.TYPE_INIT );
							transaction.setTransaction_date( CalendarUtils.getTodayAsLong() );
							transaction.setRef_account( newCompany.getId() );
							transaction.setAmount( balance );
							transaction.setBalance( balance );
							transaction.setDescription( getContext().getString( "modalNewAccount.initialization" ) );
							
							IOCManager._TransactionsManager.setRow( getContext(), null, transaction );
									
							Dashboard dashboard = (Dashboard)getContext().getData( "dashboard" );
							dashboard.refreshTransactionsTab();
						}
						catch ( Throwable e1 )
						{
							Utils.logException( e1, LOG );
						}
					}
			    }
			}
		);
		getUI().addWindow( dlg );
	}
}
