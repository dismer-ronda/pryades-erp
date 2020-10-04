package es.pryades.erp.application;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.AppUtils;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.Constants;
import es.pryades.erp.common.MessageDlg;
import es.pryades.erp.common.MinimizerContainer;
import es.pryades.erp.common.Utils;
import es.pryades.erp.configuration.ConfigurationDlg;
import es.pryades.erp.dashboard.Dashboard;
import es.pryades.erp.dto.Audit;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.ioc.IOCManager;
import lombok.Getter;

public class MainWnd extends VerticalLayout implements MinimizerContainer
{
	private static final long serialVersionUID = -8715200841149032537L;

	private static final Logger LOG = Logger.getLogger( MainWnd.class );

	private static final String AUTH_CONFIGURACION = "configuration";

	private HorizontalLayout buttonsBar;
	private HorizontalLayout topBar;
	private VerticalLayout workSpace;
	private HorizontalLayout minimizeSpace;
	
	@Getter
	private AppContext context;
	
	private HashMap<Component, Window> minimizeds;

	public MainWnd( AppContext context )
	{
		this.context = context;

		minimizeds = new HashMap<Component, Window>();
	}

	public void buildMainLayout()
	{
		setSizeFull();

		setMargin( false );
		setSpacing( false );

		addStyleName( "mainwnd" );

		addComponent( buildTopBar() );
		addComponent( buildWorkspace() );
		
		Dashboard dashboard = new Dashboard( getContext() );
		dashboard.setSizeFull();

		workSpace.addComponent( dashboard );
		workSpace.setExpandRatio( dashboard, 1.0f );
		
		try
		{
			dashboard.render();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}

		setExpandRatio( workSpace, 1.0f );
		
		/*
		if ( showBox != null )
		{
    		try
    		{
		    	ShipmentBox query = new ShipmentBox();
		    	query.setId( showBox );
		    	
		    	ShipmentBox box = (ShipmentBox)IOCManager._ShipmentsBoxesManager.getRow( getContext(), query );
		    	
		    	new ModalNewShipmentBox( getContext(), Operation.OP_MODIFY, (ShipmentBox)box, null ).showModalWindow();
    		}
			catch ( Throwable e )
    		{
				Utils.logException( e, LOG );
    		}
		}
		*/
	}

	private Component buildWorkspace()
	{
		workSpace = new VerticalLayout();
		workSpace.setMargin( true );
		workSpace.setSizeFull();

		return workSpace;
	}

	private Component buildTopBar()
	{
		topBar = new HorizontalLayout();
		topBar.setMargin( new MarginInfo( true, true, false, true ) );
		topBar.setWidth( "100%" );

		buttonsBar = new HorizontalLayout();
		buttonsBar.setWidth( "100%" );

		addButtons();

		topBar.addComponent( buttonsBar );

		topBar.setExpandRatio( buttonsBar, 1.0f );

		return topBar;
	}

	private void addButtons()
	{
		buttonsBar.removeAllComponents();

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing( true );
		
		@SuppressWarnings("unused")
		String nombre = getContext().getUser().getName();

		boolean firstButton = false;
		
		Button btn;

		if ( getContext().hasRight( AUTH_CONFIGURACION ) )
		{
			btn = new Button( (firstButton ? "| " : "") + getContext().getString( "words.configuration" ) );
			btn.addStyleName( "menu" );
			btn.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = -1378883418572457434L;

				public void buttonClick( ClickEvent event )
				{
					doShowConfiguration();
				}
			} );
			buttons.addComponent( btn );
			
			firstButton = true;
		}

		btn = new Button( (firstButton ? "| " : "") + getContext().getString( "words.my.account" ) );
		btn.addStyleName( "menu" );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -201295445460106132L;

			public void buttonClick( ClickEvent event )
			{
				doShowUserProfile();
			}
		} );
		buttons.addComponent( btn );
		
		btn = new Button( "| " + getContext().getString( "words.logout" ) );
		btn.addStyleName( "menu" );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 6954508559809158467L;

			public void buttonClick( ClickEvent event )
			{
				Logout();
			}
		} );
		buttons.addComponent( btn );
		
		buttonsBar.addComponent( buttons );
		buttonsBar.setComponentAlignment( buttons, Alignment.MIDDLE_RIGHT );
	}

	public void Logout()
	{
		registerLogout();
		
		ErpApplication app = (ErpApplication) getContext().getData( "Application" );
		app.showLoginWindow( null, null );
	}

	private void showMainApp()
	{
		try
		{
			//showCentersDlg();
		}
		catch ( Throwable e )
		{
			if ( !(e instanceof BaseException) )
				new BaseException( e, LOG, 0 );
		}
	}

	private void doShowUserProfile()
	{
		ProfileDlg dlg = new ProfileDlg( getContext().getString( "ProfileDlg.title" ) );

		dlg.setContext( getContext() );

		dlg.addComponents();

		getUI().addWindow( dlg );
	}

	private void doShowConfiguration()
	{
		ConfigurationDlg dlg = new ConfigurationDlg();

		dlg.setContext( getContext() );
		dlg.createComponents();

		getUI().addWindow( dlg );
	}

	public void messageAndExit( String title, String message )
	{
		MessageDlg dlg = new MessageDlg( getContext(), title, message );

		dlg.addCloseListener( new Window.CloseListener()
		{
			private static final long serialVersionUID = -5303587015039065226L;

			@Override
			public void windowClose( CloseEvent e )
			{
				Logout();
			}
		} );

		getUI().addWindow( dlg );
	}

	public void startMainWindow()
	{
		registerLogin();
		
		try
		{
			showMainApp();
		}
		catch ( Throwable e )
		{
			if ( e instanceof BaseException )
			{
				String error = AppUtils.getExceptionMessage( (BaseException)e, getContext() );

				if ( error.isEmpty() )
					error = getContext().getString( "error.unknown" );

				messageAndExit( getContext().getString( "words.error" ), error );
			}
			else
				messageAndExit( getContext().getString( "words.error" ), getContext().getString( "error.unknown" ) );
		}
	}
	
	@Override
	public void minimizeWindow( Window window, Component icon )
	{
		if ( minimizeSpace == null )
		{
			minimizeSpace = new HorizontalLayout();
			
			workSpace.addComponent( minimizeSpace );
		}
		
		minimizeSpace.addComponent( icon );
		
		minimizeds.put( icon, window );
		
		window.setVisible( false );
	}

	@Override
	public void restoreWindow( Component icon )
	{
		Window wnd = minimizeds.remove( icon );
		
		if ( wnd != null )
		{
			minimizeSpace.removeComponent( icon );
			
			wnd.setVisible( true );
			
			if ( minimizeds.isEmpty() )
			{
				workSpace.removeComponent( minimizeSpace );
				minimizeSpace = null;
			}
		}
	}
	
	private void registerLogin()
	{
		try
		{
			Audit audit = new Audit();
			
			audit.setAudit_date( CalendarUtils.getTodayAsLong( "UTC" ) );
			audit.setAudit_type( Constants.AUDIT_TYPE_LOGIN );
			audit.setRef_user( getContext().getUser().getId() );
			audit.setDuration( getContext().getIntegerParameter( Parameter.PAR_DURATION_LOGIN_LOGOUT ) );
			
			IOCManager._AuditsManager.setRow( getContext(), null, audit );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}

	private void registerLogout()
	{
		try
		{
			Audit audit = new Audit();
			
			audit.setAudit_date( CalendarUtils.getTodayAsLong( "UTC" ) );
			audit.setAudit_type( Constants.AUDIT_TYPE_LOGOUT );
			audit.setRef_user( getContext().getUser().getId() );
			audit.setDuration( getContext().getIntegerParameter( Parameter.PAR_DURATION_LOGIN_LOGOUT ) );
			
			IOCManager._AuditsManager.setRow( getContext(), null, audit );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
}
