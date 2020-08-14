package es.pryades.erp.application;

import org.apache.log4j.Logger;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.ProfileRight;
import es.pryades.erp.dto.User;
import es.pryades.erp.ioc.IOCManager;

@SuppressWarnings({ "unchecked" })
public class LoginPanel extends VerticalLayout
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( LoginPanel.class );

	private static final long serialVersionUID = -644736804420195948L;

	private AppContext ctx;

	private PasswordField passwordField;
	private TextField userField;
	private TextField userFieldForgot;
	@SuppressWarnings("unused")
	private LoginWnd mainWnd;

	public LoginPanel( AppContext ctx, LoginWnd mainWnd, String user, String password )
	{
		this.ctx = ctx;
		this.mainWnd = mainWnd;

		setSpacing( true );
		setWidth( "100%" );

		Component component = null;

		/*component = buildLogo();
		addComponent( component );
		setComponentAlignment( component, Alignment.MIDDLE_CENTER );*/

		component = loginPanel( user, password );
		addComponent( component );
		setComponentAlignment( component, Alignment.MIDDLE_CENTER );

		component = loginSendPassword();
		addComponent( component );
		setComponentAlignment( component, Alignment.MIDDLE_CENTER );

		if ( user != null && password != null )
			onLogin();
		else
			userField.focus();
	}

	/*private Component buildLogo()
	{
		Embedded img = new Embedded( null, new ThemeResource( "images/logo.png" ) );
		img.setHeight( "165px" );
		img.setWidth( "362px" );

		return img;
	}*/

	private Component loginPanel( String user, String password )
	{
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth( "360px" );
		layout.setSpacing( true );
		layout.setMargin( true );

		VerticalLayout form = new VerticalLayout();
		form.setStyleName( "indigo_loginform" );
		form.setWidth( "100%" );

		HorizontalLayout rowHeader = new HorizontalLayout();
		rowHeader.setWidth( "100%" );
		rowHeader.setStyleName( "login_header" );

		HorizontalLayout rowButton = new HorizontalLayout();
		rowButton.setWidth( "100%" );

		Label labelCaption = new Label( ctx.getString( "LoginDlg.title" ) );
		labelCaption.setStyleName( "login_header" );
		labelCaption.setWidth( "100%" );

		rowHeader.addComponent( labelCaption );
		rowHeader.setComponentAlignment( labelCaption, Alignment.MIDDLE_LEFT );
		form.addComponent( rowHeader );

		VerticalLayout inside = new VerticalLayout();
		inside.setWidth( "100%" );
		inside.setMargin( true );
		inside.setSpacing( true );

		userField = new TextField();
		userField.setId( "LoginDlg.user" );
		userField.setInputPrompt( ctx.getString( "words.user" ) );
		userField.setWidth( "100%" );
		userField.setPrimaryStyleName( "indigo_login" );
		userField.setValue( user );
		userField.setNullRepresentation( "" );

		passwordField = new PasswordField();
		passwordField.setId( "LoginDlg.password" );
		passwordField.setInputPrompt( ctx.getString( "words.password" ) );
		passwordField.setWidth( "100%" );
		passwordField.setPrimaryStyleName( "indigo_password" );
		passwordField.setValue( password );
		passwordField.setNullRepresentation( "" );

		inside.addComponent( userField );
		inside.addComponent( passwordField );

		form.addComponent( inside );

		layout.addComponent( form );

		Button btn = new Button( ctx.getString( "words.login" ) );
		btn.addStyleName( "primary" );
		btn.setClickShortcut( KeyCode.ENTER );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 3827413316141851767L;

			public void buttonClick( ClickEvent event )
			{
				onLogin();
			}
		} );
		rowButton.addComponent( btn );
		rowButton.setComponentAlignment( btn, Alignment.MIDDLE_RIGHT );

		layout.addComponent( rowButton );

		return layout;
	}

	private Component loginSendPassword()
	{
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth( "360px" );
		layout.setSpacing( true );
		layout.setMargin( true );

		VerticalLayout form = new VerticalLayout();
		form.setStyleName( "indigo_loginform" );
		form.setWidth( "100%" );

		HorizontalLayout rowHeader = new HorizontalLayout();
		rowHeader.setWidth( "100%" );
		rowHeader.setStyleName( "login_header" );

		HorizontalLayout rowButton = new HorizontalLayout();
		rowButton.setWidth( "100%" );

		Label labelCaption = new Label( ctx.getString( "LoginDlg.password.forgot" ) );
		labelCaption.setStyleName( "login_header" );
		labelCaption.setWidth( "100%" );
		rowHeader.addComponent( labelCaption );
		rowHeader.setComponentAlignment( labelCaption, Alignment.MIDDLE_LEFT );
		form.addComponent( rowHeader );

		VerticalLayout inside = new VerticalLayout();
		inside.setWidth( "100%" );
		inside.setMargin( true );

		userFieldForgot = new TextField();
		userFieldForgot.setInputPrompt( ctx.getString( "LoginDlg.email" ) );
		userFieldForgot.setWidth( "100%" );
		userFieldForgot.setPrimaryStyleName( "indigo_login" );

		inside.addComponent( userFieldForgot );

		form.addComponent( inside );

		Button btn = new Button( ctx.getString( "LoginDlg.send" ) );
		btn.addClickListener( new Button.ClickListener()
		{

			private static final long serialVersionUID = 3827413316030851767L;

			public void buttonClick( ClickEvent event )
			{
				onRenew();
			}
		} );
		rowButton.addComponent( btn );
		rowButton.setComponentAlignment( btn, Alignment.MIDDLE_RIGHT );

		layout.addComponent( form );

		layout.addComponent( rowButton );

		return layout;
	}

	private void onLogin()
	{
		String login = (String)userField.getValue();
		String password = (String)passwordField.getValue();
		String subject = ctx.getString( "LoginDlg.message.subject" );
		String body = ctx.getString( "LoginDlg.message.body" );
		
		if ( login == null || password == null )
			Utils.showNotification( ctx, ctx.getString( "LoginDlg.loginfail" ), Notification.Type.ERROR_MESSAGE );
		else
		{
			try
			{
				IOCManager._UsersManager.validateUser( ctx, login, password, subject, body, true );
	
				if ( ctx.isPasswordExpired() )
					ctx.getUser().setStatus( User.PASS_EXPIRY );
	
				if ( ctx.getUser().getStatus() != User.PASS_OK )
					changePassword();
				else
					Login();
			}
			catch ( BaseException e )
			{
				switch ( e.getErrorCode() )
				{
					case BaseException.NULL_RETURN:
					case BaseException.LOGIN_FAIL:
						Utils.showNotification( ctx, ctx.getString( "LoginDlg.loginfail" ), Notification.Type.ERROR_MESSAGE );
						break;
	
					case BaseException.ZERO_SUBSCRIPTIONS:
						Utils.showNotification( ctx, ctx.getString( "LoginDlg.unsubscribed" ), Notification.Type.ERROR_MESSAGE );
						break;
	
					case BaseException.LOGIN_PASSWORD_CHANGED:
						Utils.showNotification( ctx, ctx.getString( "LoginDlg.newpass" ), Notification.Type.ERROR_MESSAGE );
						break;
	
					case BaseException.LOGIN_BLOCKED:
						Utils.showNotification( ctx, ctx.getString( "error.blocked" ), Notification.Type.ERROR_MESSAGE );
						break;
	
					default:
						Utils.showNotification( ctx, ctx.getString( "error.unknown" ) + e.getErrorCode(), Notification.Type.ERROR_MESSAGE );
						break;
				}
			}
		}
	}

	private void onRenew()
	{
		String email = (String)this.userFieldForgot.getValue();

		if ( email.isEmpty() )
			Utils.showNotification( ctx, ctx.getString( "LoginDlg.notblank" ), Notification.Type.ERROR_MESSAGE );
		else
		{
			try
			{
				IOCManager._UsersManager.sendNewPassword( ctx, email, ctx.getString( "LoginDlg.message.subject" ), ctx.getString( "LoginDlg.renew.message.body" ), true );

				Utils.showNotification( ctx, ctx.getString( "LoginDlg.sent" ), Notification.Type.TRAY_NOTIFICATION );
			}
			catch ( BaseException e )
			{
				switch ( e.getErrorCode() )
				{
					case BaseException.NULL_RETURN:
						Utils.showNotification( ctx, ctx.getString( "LoginDlg.notfound" ), Notification.Type.ERROR_MESSAGE );
						break;

					case BaseException.MAIL_ADDRESS_INVALID:
						Utils.showNotification( ctx, ctx.getString( "error.email.invalid" ), Notification.Type.ERROR_MESSAGE );
						break;

					case BaseException.MAIL_SEND_ERROR:
						Utils.showNotification( ctx, ctx.getString( "error.email.fail" ), Notification.Type.ERROR_MESSAGE );
						break;

					case BaseException.LOGIN_BLOCKED:
						Utils.showNotification( ctx, ctx.getString( "error.blocked" ), Notification.Type.ERROR_MESSAGE );
						break;

					default:
						Utils.showNotification( ctx, ctx.getString( "error.unknown" ) + e.getErrorCode(), Notification.Type.ERROR_MESSAGE );
						break;
				}
			}
		}
	}

	private void changePassword()
	{
		String comments = "";

		switch ( ctx.getUser().getStatus() )
		{
			case User.PASS_CHANGED:
				comments = ctx.getString( "LoginDlg.password.renew" );
				break;

			case User.PASS_EXPIRY:
				comments = ctx.getString( "LoginDlg.password.expired" );
				break;

			case User.PASS_FORGET:
				comments = ctx.getString( "LoginDlg.password.forget" );
				break;

			case User.PASS_NEW:
				ctx.getString( "LoginDlg.password.new" );
				break;
		}

		ChangePasswordDlg dlg = new ChangePasswordDlg( ctx.getString( "ChangePasswordDlg.title" ), ctx, comments + "\n" );

		dlg.addCloseListener( new Window.CloseListener()
		{
			private static final long serialVersionUID = -3049248279625994700L;

			@Override
			public void windowClose( CloseEvent e )
			{
				onPasswordChanged( e );
			}
		} );

		getUI().addWindow( dlg );
	}

	private void onPasswordChanged( CloseEvent event )
	{
		ChangePasswordDlg dlg = (ChangePasswordDlg)event.getWindow();

		if ( dlg.isChanged() )
			Login();
	}

	private void Login()
	{
		User usuario = ctx.getUser();

		try
		{
			ProfileRight query = new ProfileRight();
			query.setRef_profile( usuario.getRef_profile() );
			
			ctx.setRights( IOCManager._ProfilesRightsManager.getRows( ctx, query ) );

			if ( ctx.hasRight( "login" ) )
			{
				ErpApplication app = (ErpApplication) ctx.getData( "Application" );
				app.showMainWindow();
			}
			else
				Utils.showNotification( ctx, ctx.getString( "error.login.right" ), Notification.Type.ERROR_MESSAGE );
		}
		catch ( BaseException e )
		{
			Utils.showNotification( ctx, ctx.getString( "error.unknown" ) + " " + e.getErrorCode(), Notification.Type.ERROR_MESSAGE );
		}
	}
}
