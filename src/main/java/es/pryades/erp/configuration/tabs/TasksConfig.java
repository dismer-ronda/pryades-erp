package es.pryades.erp.configuration.tabs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.Constants;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD.Operation;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.PagedTable;
import es.pryades.erp.common.TaskAction;
import es.pryades.erp.configuration.modals.ModalNewTask;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.Query;
import es.pryades.erp.dto.Task;
import es.pryades.erp.dto.query.TaskQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.TaskVto;
import es.pryades.erp.vto.controlers.TaskControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class TasksConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = 6798669013238869216L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( TasksConfig.class );

	private ComboBox comboClazz;
	
	private Button bttnApply;
	
	public TasksConfig( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "clazz" );
		setOrder( "desc" );
	}

	@Override
	public String getResourceKey()
	{
		return "tasksConfig";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "month", "day", "hour", "clazz", "description" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "clazz", "description" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( TaskVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	@Override
	public Component getQueryComponent()
	{
		comboClazz = new ComboBox( getContext().getString( "tasksConfig.clazz" ) );
		comboClazz.setNullSelectionAllowed( true );
		comboClazz.setTextInputAllowed( false );
		comboClazz.setImmediate( true );
		fillComboClazzes();
		
		bttnApply = new Button( getContext().getString( "words.search" ) );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		//bttnApply.setStyleName( "borderless" );
		//bttnApply.setIcon( new ThemeResource( "images/accept.png" ) );
		addButtonApplyFilterClickListener();

		HorizontalLayout rowQuery = new HorizontalLayout();
		rowQuery.setSpacing( true );
		rowQuery.addComponent( comboClazz );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		TaskQuery query = new TaskQuery();
		
		query.setClazz( (Integer)comboClazz.getValue() );
		query.setSystem( 0 );
		
		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewTask( getContext(), Operation.OP_ADD, null, TasksConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewTask( getContext(), Operation.OP_MODIFY, (Task)dto, TasksConfig.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new TaskControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Task();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._TasksManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
		List<BaseDto> invalid = new ArrayList<BaseDto>();
		
		for ( BaseDto row : rows )
		{
			TaskAction action = IOCManager._TasksManager.getTaskAction( (Task)row );
			
			if ( !action.isUserEnabledForTask( context ) )
				invalid.add( row );
		}
		
		rows.removeAll( invalid );
	}
	
	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.tasks.add" );
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
			private static final long serialVersionUID = -2798057296163756220L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				refreshVisibleContent( true );
			}
		} );
	}

	private void fillComboClazzes()
	{
		for ( int i = Constants.TASK_FIRST; i < Constants.TASK_FIRST + Constants.TASK_CLAZZES; i++ )
		{
			comboClazz.addItem( i );
			comboClazz.setItemCaption( i, getContext().getString( "task.clazz." + i ) );
		}
	}
}

