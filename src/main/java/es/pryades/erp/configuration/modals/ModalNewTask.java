package es.pryades.erp.configuration.modals;

import org.apache.log4j.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.AppUtils;
import es.pryades.erp.common.Constants;
import es.pryades.erp.common.DialogLabel;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Settings;
import es.pryades.erp.common.TaskAction;
import es.pryades.erp.common.TaskActionDataEditor;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Task;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.processors.SignalsProcessor;
import lombok.Getter;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewTask extends ModalWindowsCRUD implements ModalParent
{
	private static final long serialVersionUID = 2426923130471051142L;

	private static final Logger LOG = Logger.getLogger( ModalNewTask.class );

	@Getter
	protected Task newTask;

	private ComboBox comboClazz;
	private ComboBox comboTimezone;
	private ComboBox comboLanguage;
	private TextField editDescription;
	private TextField editMonth;
	private TextField editDay;
	private TextField editHour;
	private TextField editTimes;
	
	private VerticalLayout layoutAction;
	private TaskActionDataEditor actionEditor;
	private Component actionComp;

	private boolean dirty;

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewTask( AppContext context, OperationCRUD modalOperation, Task orgDto, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgDto );
		
		setWidth( "1024px" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newTask = (Task) Utils.clone( (Task) orgDto );
		}
		catch ( Throwable e1 )
		{
			newTask = new Task();
			
			newTask.setTimezone( "UTC" );
			newTask.setLanguage( getContext().getLanguage() );
			newTask.setSystem( 0 );
			newTask.setMonth( "1" );
			newTask.setDay( "1" );
			newTask.setHour( "1" );
			newTask.setTimes( 1 );
		}

		dirty = false;

		bi = new BeanItem<BaseDto>( newTask );

		layoutAction = new VerticalLayout();
		layoutAction.setWidth( "100%" );
		
		comboClazz = new ComboBox();
		comboClazz.setWidth( "100%" );
		comboClazz.setNullSelectionAllowed( false );
		comboClazz.setTextInputAllowed( false );
		comboClazz.setImmediate( true );
		comboClazz.setRequired( true );
		comboClazz.setRequiredError( getContext().getString( "words.required" ) );
		comboClazz.addValueChangeListener( new ValueChangeListener()
		{
			private static final long serialVersionUID = 3947274110196900646L;

			@Override
			public void valueChange( ValueChangeEvent event )
			{
				onChangeAccion();
			}
		});
		fillComboClazzes();
		comboClazz.setPropertyDataSource( bi.getItemProperty( "clazz" ) );
		
		comboTimezone = new ComboBox();
		comboTimezone.setWidth( "100%" );
		comboTimezone.setNullSelectionAllowed( false );
		comboTimezone.setTextInputAllowed( true );
		comboTimezone.setImmediate( true );
		fillComboTimezones();
		comboTimezone.setPropertyDataSource( bi.getItemProperty( "timezone" ) );
		
		comboLanguage = new ComboBox();
		comboLanguage.setWidth( "100%" );
		comboLanguage.setNullSelectionAllowed( false );
		comboLanguage.setTextInputAllowed( false );
		comboLanguage.setImmediate( true );
		fillComboLanguages();
		comboLanguage.setPropertyDataSource( bi.getItemProperty( "language" ) );
		
		editDescription = new TextField( bi.getItemProperty( "description" ) );
		editDescription.setWidth( "100%" );
		editDescription.setNullRepresentation( "" );
		editDescription.setRequired( true );
		editDescription.setRequiredError( getContext().getString( "words.required" ) );

		editMonth = new TextField( bi.getItemProperty( "month" ) );
		editMonth.setWidth( "100%" );
		editMonth.setNullRepresentation( "" );
		editMonth.setRequired( true );
		editMonth.setRequiredError( getContext().getString( "words.required" ) );

		editDay = new TextField( bi.getItemProperty( "day" ) );
		editDay.setWidth( "100%" );
		editDay.setNullRepresentation( "" );
		editDay.setRequired( true );
		editDay.setRequiredError( getContext().getString( "words.required" ) );

		editHour = new TextField( bi.getItemProperty( "hour" ) );
		editHour.setWidth( "100%" );
		editHour.setNullRepresentation( "" );
		editHour.setRequired( true );
		editHour.setRequiredError( getContext().getString( "words.required" ) );

		editTimes = new TextField( bi.getItemProperty( "times" ) );
		editTimes.setWidth( "100%" );
		editTimes.setNullRepresentation( "" );
		editTimes.setRequired( true );
		editTimes.setRequiredError( getContext().getString( "words.required" ) );

		HorizontalLayout rowClazz = new HorizontalLayout();
		rowClazz.setWidth( "100%" );
		rowClazz.addComponent( new DialogLabel( getContext().getString( "modalNewTask.clazz" ), "120px" ) );
		rowClazz.addComponent( comboClazz );
		rowClazz.setExpandRatio( comboClazz, 1.0f );
		
		HorizontalLayout rowDescription = new HorizontalLayout();
		rowDescription.setWidth( "100%" );
		rowDescription.addComponent( new DialogLabel( getContext().getString( "modalNewTask.description" ), "120px" ) );
		rowDescription.addComponent( editDescription );
		rowDescription.setExpandRatio( editDescription, 1.0f );

		HorizontalLayout rowMonth = new HorizontalLayout();
		rowMonth.setWidth( "100%" );
		rowMonth.addComponent( new DialogLabel( getContext().getString( "modalNewTask.month" ), "120px" ) );
		rowMonth.addComponent( editMonth );
		rowMonth.setExpandRatio( editMonth, 1.0f );
		AppUtils.addRowHint( getContext(), rowMonth, "modalNewTask.month.hint" );

		HorizontalLayout rowDay = new HorizontalLayout();
		rowDay.setWidth( "100%" );
		rowDay.addComponent( new DialogLabel( getContext().getString( "modalNewTask.day" ), "120px" ) );
		rowDay.addComponent( editDay );
		rowDay.setExpandRatio( editDay, 1.0f );
		AppUtils.addRowHint( getContext(), rowDay, "modalNewTask.day.hint" );

		HorizontalLayout rowHour = new HorizontalLayout();
		rowHour.setWidth( "100%" );
		rowHour.addComponent( new DialogLabel( getContext().getString( "modalNewTask.hour" ), "120px" ) );
		rowHour.addComponent( editHour );
		rowHour.setExpandRatio( editHour, 1.0f );
		AppUtils.addRowHint( getContext(), rowHour, "modalNewTask.hour.hint" );

		HorizontalLayout rowTimes = new HorizontalLayout();
		rowTimes.setWidth( "100%" );
		rowTimes.addComponent( new DialogLabel( getContext().getString( "modalNewTask.times" ), "120px" ) );
		rowTimes.addComponent( editTimes );
		rowTimes.setExpandRatio( editTimes, 1.0f );
		AppUtils.addRowHint( getContext(), rowTimes, "modalNewTask.times.hint" );
		
		HorizontalLayout rowAction = new HorizontalLayout();
		rowAction.setWidth( "100%" );
		rowAction.addComponent( layoutAction );
		rowAction.setExpandRatio( layoutAction, 1.0f );

		HorizontalLayout rowTimezone = new HorizontalLayout();
		rowTimezone.setWidth( "100%" );
		rowTimezone.addComponent( new DialogLabel( getContext().getString( "modalNewTask.timezone" ), "120px" ) );
		rowTimezone.addComponent( comboTimezone );
		rowTimezone.setExpandRatio( comboTimezone, 1.0f );

		HorizontalLayout rowLanguage = new HorizontalLayout();
		rowLanguage.setWidth( "100%" );
		rowLanguage.addComponent( new DialogLabel( getContext().getString( "modalNewTask.language" ), "120px" ) );
		rowLanguage.addComponent( comboLanguage );
		rowLanguage.setExpandRatio( comboLanguage, 1.0f );

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( rowClazz );
		row1.addComponent( rowDescription );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( rowTimezone );
		row2.addComponent( rowLanguage );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( rowMonth );
		row3.addComponent( rowDay );

		HorizontalLayout row4 = new HorizontalLayout();
		row4.setWidth( "100%" );
		row4.setSpacing( true );
		row4.addComponent( rowHour );
		row4.addComponent( rowTimes );

		HorizontalLayout row7 = new HorizontalLayout();
		row7.setWidth( "100%" );
		row7.setSpacing( true );
		row7.addComponent( rowAction );
		
		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		componentsContainer.addComponent( row3 );
		componentsContainer.addComponent( row4 );
		componentsContainer.addComponent( row7 );
		
		if ( !getOperation().equals( OperationCRUD.OP_ADD ) )
		{
			if ( getContext().hasRight( "configuration.tasks.dispatch" ) )
			{
				Button btnDispatch = new Button();
				btnDispatch.setCaption( getContext().getString( "tasksConfig.dispatch" ) );
				btnDispatch.addClickListener( new Button.ClickListener()
				{
					private static final long serialVersionUID = 982407465902279459L;
	
					public void buttonClick( ClickEvent event )
					{
						onDispatchTask();
					}
				} );
	
				getDefaultOperationsRow().addComponentAsFirst( btnDispatch );
				getDefaultOperationsRow().setComponentAlignment( btnDispatch, Alignment.MIDDLE_LEFT );
			}
		}
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewTask";
	}

	@Override
	protected void defaultFocus()
	{
		editDescription.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newTask.setId( null );
			
			if ( actionEditor != null )
			{	
				String message = actionEditor.isValidInput();
				
				if ( message != null )
				{
					Utils.showNotification( getContext(), message, Notification.Type.ERROR_MESSAGE );
					
					return false;
				}

				newTask.setDetails( actionEditor.getTaskData() );
			}
			
			IOCManager._TasksManager.setRow( getContext(), null, newTask );

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
			if ( actionEditor != null )
			{
				String message = actionEditor.isValidInput();
				
				if ( message != null )
				{
					Utils.showNotification( getContext(), message, Notification.Type.ERROR_MESSAGE );
					
					return false;
				}

				newTask.setDetails( actionEditor.getTaskData() );
			}

			IOCManager._TasksManager.setRow( getContext(), (Task) orgDto, newTask );
	
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
			IOCManager._TasksManager.delRow( getContext(), newTask );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}
	
	@Override
	public void refreshVisibleContent( boolean repage )
	{
		dirty = true;
	}
	
	private void fillComboClazzes()
	{
		for ( int i = Constants.TASK_FIRST; i < Constants.TASK_FIRST + Constants.TASK_CLAZZES; i++ )
		{
			TaskAction action = IOCManager._TasksManager.getTaskAction( i );

			if ( action.isUserEnabledForTask( context ) )
			{
				comboClazz.addItem( i );
				comboClazz.setItemCaption( i, getContext().getString( "task.clazz." + i ) );
			}
		}
	}
	
	public void onChangeAccion()
	{
		layoutAction.removeAllComponents();
		actionEditor = null;
		actionComp = null;

		if ( newTask.getClazz() == null )
			return;

		try
		{
			TaskAction action = IOCManager._TasksManager.getTaskAction( newTask );
			
			if ( action != null )
			{
				actionEditor = (TaskActionDataEditor)action.getTaskDataEditor( getContext() );
	
				if ( actionEditor != null )
				{
					actionComp = (Component)actionEditor.getComponent( newTask.getDetails(), false );
		
					if ( actionComp != null )
					{
						layoutAction.addComponent( actionComp );
						layoutAction.setComponentAlignment( actionComp, Alignment.MIDDLE_CENTER );
					}
				}
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}

	private void fillComboTimezones()
	{
		String timezones[] = Utils.getTimezones();
		
		for ( String timezone : timezones )
			comboTimezone.addItem( timezone );
	}

	private void fillComboLanguages()
	{
		String languages[] = Settings.LANGUAGES.split( "," );
		
		for ( String language : languages )
		{
			comboLanguage.addItem( language );
			comboLanguage.setItemCaption( language, getContext().getString( "language." + language ) );
		}
	}

	@Override
	protected void onClose()
	{
		closeModalWindow( dirty, false );		
	}

	@Override
	protected boolean hasDelete()
	{
		return getContext().hasRight( "configuration.tasks.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.tasks.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.tasks.modify" );
	}
	
	public void onDispatchTask()
	{
		try
		{
			SignalsProcessor.dispatchSignal( newTask );
			
			Utils.showNotification( getContext(), getContext().getString( "tasksConfig.dispatched" ), Notification.Type.TRAY_NOTIFICATION );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
}
