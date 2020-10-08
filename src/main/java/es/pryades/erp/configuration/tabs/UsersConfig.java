package es.pryades.erp.configuration.tabs;

import java.util.List;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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
import es.pryades.erp.configuration.modals.ModalNewUser;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.User;
import es.pryades.erp.dto.query.UserQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.UserVto;
import es.pryades.erp.vto.controlers.UserControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class UsersConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = 4942358294790571817L;

	private Button bttnApply;

	public UsersConfig( AppContext ctx )
	{
		super( ctx );

		setOrderby( "name" );
		setOrder( "asc" );
	}

	@Override
	public String getResourceKey()
	{
		return "usersConfig";
	}

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{"name", "login", "email", "profile_name" };
	}

	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( UserVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	@Override
	public Component getQueryComponent()
	{
		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		//bttnApply.setStyleName( "borderless" );
		//bttnApply.setIcon( new ThemeResource( "images/accept.png" ) );
		bttnApply.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 879941541499453494L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				refreshVisibleContent( true );
			}
		} );

		HorizontalLayout rowQuery = new HorizontalLayout();
		rowQuery.setSpacing( true );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		UserQuery query = new UserQuery();
		
		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewUser( getContext(), OperationCRUD.OP_ADD, null, UsersConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewUser( getContext(), OperationCRUD.OP_MODIFY, (User)dto, UsersConfig.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new UserControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new User();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._UsersManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
		int i = 0;
		int count = rows.size();
		
		while ( i < count )
		{
			User user = (User)rows.get( i );
			
			if ( getContext().getUser().getRef_profile() > user.getRef_profile() )
			{
				rows.remove( i );
				count--;
			}
			else 
				i++;
		}
	}

	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.users.add" );
	}

	@Override
	public void updateComponent()
	{
	}

	@Override
	public void onFieldEvent( Component component, String column )
	{
	}
}
