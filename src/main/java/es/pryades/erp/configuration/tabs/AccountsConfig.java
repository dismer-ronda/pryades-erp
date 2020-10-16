package es.pryades.erp.configuration.tabs;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD.OperationCRUD;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.PagedTable;
import es.pryades.erp.configuration.modals.ModalNewAccount;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.Account;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.Query;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.AccountVto;
import es.pryades.erp.vto.controlers.AccountControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class AccountsConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = 2194864758855672809L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( AccountsConfig.class );

	private ComboBox comboTypes;

	private Button bttnApply;
	
	public AccountsConfig( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "alias" );
		setOrder( "desc" );
	}

	@Override
	public String getResourceKey()
	{
		return "accountsConfig";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "account_type", "company_name", "name", "number", "balance" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "account_type", "company_name", "name", "number", "balance" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( AccountVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	public List<Component> getCustomOperations()
	{
		return null;
	}

	@Override
	public Component getQueryComponent()
	{
		comboTypes = new ComboBox(getContext().getString( "accountsConfig.comboTypes" ));
		comboTypes.setWidth( "100%" );
		comboTypes.setNullSelectionAllowed( true );
		comboTypes.setTextInputAllowed( false );
		comboTypes.setImmediate( true );
		comboTypes.setRequiredError( getContext().getString( "words.required" ) );
		fillComboTypes();
		comboTypes.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -4273898552150870296L;

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
		rowQuery.addComponent( comboTypes );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		Account query = new Account();
		
		if ( comboTypes.getValue() != null )
			query.setAccount_type( (Integer)comboTypes.getValue() );
		
		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewAccount( getContext(), OperationCRUD.OP_ADD, null, AccountsConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewAccount( getContext(), OperationCRUD.OP_MODIFY, (Account)dto, AccountsConfig.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new AccountControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Account();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._AccountsManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
	}
	
	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.accounts.add" );
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
			private static final long serialVersionUID = 1023337437523177453L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				refreshVisibleContent( true );
			}
		} );
	}

	private void fillComboTypes()
	{
		for ( int i = Account.TYPE_BANK; i <= Account.TYPE_CREDIT; i++ )
		{
			comboTypes.addItem( i );
			comboTypes.setItemCaption( i, getContext().getString( "account.type." + i ) );
		}	
	}
}

