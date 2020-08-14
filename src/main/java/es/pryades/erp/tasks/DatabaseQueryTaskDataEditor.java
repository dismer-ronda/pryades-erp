package es.pryades.erp.tasks;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.DialogLabel;
import es.pryades.erp.common.TaskActionDataEditor;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.User;
import es.pryades.erp.dto.query.UserQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.reports.CommonEditor;

public class DatabaseQueryTaskDataEditor extends CommonEditor implements TaskActionDataEditor 
{
	private static final long serialVersionUID = -9187149361325987049L;

	private static final Logger LOG = Logger.getLogger( DatabaseQueryTaskDataEditor.class );
	
	private TextArea editSql;
	private ComboBox comboUsers;
	
	private DatabaseQueryTaskData data;
	
	public DatabaseQueryTaskDataEditor( AppContext ctx ) 
	{
		super( ctx );
	}
	
	@Override
	public Object getComponent( String details, boolean readOnly )
	{
		data = null;
		
		try
		{
			data = (DatabaseQueryTaskData) Utils.toPojo( details, DatabaseQueryTaskData.class, false );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		if ( data == null )
		{
			try
			{
				data = (DatabaseQueryTaskData) Utils.toPojo( details, DatabaseQueryTaskData.class, false );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}
		}
		
		if ( data == null )
		{
			data = new DatabaseQueryTaskData();
			
			data.setRef_user( getContext().getUser().getId() );
		}
		
		bi = new BeanItem<DatabaseQueryTaskData>( data );
		
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing( true );
		
		editSql = new TextArea( bi.getItemProperty( "sql" ) );
		editSql.setWidth( "100%" );
		editSql.setNullRepresentation( "" );
                
		comboUsers = new ComboBox();
		comboUsers.setWidth( "100%" );
		comboUsers.setNullSelectionAllowed( false );
		comboUsers.setTextInputAllowed( true );
		comboUsers.setImmediate( true );
		fillComboUsers();
		comboUsers.setPropertyDataSource( bi.getItemProperty( "ref_user" ) );
		
		HorizontalLayout rowSql = new HorizontalLayout();
		rowSql.setWidth( "100%" );
		rowSql.addComponent( new DialogLabel( getContext().getString( "databaseQueryTaskDataEditor.sql" ), "120px" ) );
		rowSql.addComponent( editSql );
		rowSql.setExpandRatio( editSql, 1.0f );
			
		HorizontalLayout rowUser = new HorizontalLayout();
		rowUser.setWidth( "100%" );
		rowUser.addComponent( new DialogLabel( getContext().getString( "databaseQueryTaskDataEditor.user" ), "120px" ) );
		rowUser.addComponent( comboUsers );
		rowUser.setExpandRatio( comboUsers, 1.0f );
		
		HorizontalLayout row4 = new HorizontalLayout();
		row4.setWidth( "100%" );
		row4.setSpacing( true );
		row4.addComponent( rowSql );

		HorizontalLayout row5 = new HorizontalLayout();
		row5.setWidth( "100%" );
		row5.setSpacing( true );
		row5.addComponent( rowUser );

		layout.addComponent( row4 );
		layout.addComponent( row5 );
		
		return layout;
	}

	@Override
	public String getTaskData() throws BaseException
	{
		return Utils.toJson( data );
	}

	@Override
	public String isValidInput()
	{
		if ( editSql.isEmpty() )
			return getContext().getString( "databaseQueryTaskDataEditor.missing.sql" );
			
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void fillComboUsers()
	{
		comboUsers.removeAllItems();
		
		try
		{
			UserQuery query = new UserQuery();
			
			List<User> users = IOCManager._UsersManager.getRows( getContext(), query );

			for ( User user : users )
			{
				comboUsers.addItem( user.getId() );
				comboUsers.setItemCaption( user.getId(), user.getName() );
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
}
