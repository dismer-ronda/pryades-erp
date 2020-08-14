package es.pryades.erp.application;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.AppUtils;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.User;
import es.pryades.erp.dto.UserDefault;
import es.pryades.erp.ioc.IOCManager;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ChangePasswordDlg extends Window 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5722841682959892036L;

	private VerticalLayout layout;
	private GridLayout grid;

	private PasswordField password1;
	private PasswordField password2;
	
	private AppContext ctx;
	
	private boolean changed = false;

	private UserDefault lastPassword1;
	private UserDefault lastPassword2;
	
	public ChangePasswordDlg( String title, AppContext ctx, String comments )
	{
		super( title );
		
		this.ctx = ctx;
		
		lastPassword1 = IOCManager._UserDefaultsManager.getUserDefault( ctx, UserDefault.LAST_PASSWORD1 ); 
		lastPassword2 = IOCManager._UserDefaultsManager.getUserDefault( ctx, UserDefault.LAST_PASSWORD2 ); 
						
		setContent( layout = new VerticalLayout() );
		
		layout.setMargin( true );
		layout.setSpacing( true );
		layout.setSizeUndefined();
		
		grid = new GridLayout();
		
	    grid.setColumns( 2 );
		grid.setMargin( false );
		grid.setSpacing( true );
		grid.setSizeUndefined();

		if ( comments != null )
			AppUtils.createLabel( comments, "-1px", layout );

	    layout.addComponent( grid );
	    
		addComponents();
		
		center();
		
		setModal( true );
		setResizable( false );
		setClosable( true );
	}

	private void addComponents() 
	{
		AppUtils.createLabel( ctx.getString( "ChangePasswordDlg.new" ), "120px", grid );		
		password1 = AppUtils.createPassword( grid, "ChangePasswordDlg.new" );
		
		AppUtils.createLabel( ctx.getString( "ChangePasswordDlg.repeat" ), "120px", grid );		
		password2 = AppUtils.createPassword( grid, "ChangePasswordDlg.repeat" );
		
		Button button1 = AppUtils.createButton( ctx.getString( "words.change" ), ctx.getString( "words.change" ), "ChangePasswordDlg.change", layout );
		button1.setClickShortcut( KeyCode.ENTER );
		button1.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = -2405899090243896584L;

			public void buttonClick(ClickEvent event) 
            {
				onChangePassword();
            }
        });
        layout.setComponentAlignment( button1, Alignment.BOTTOM_RIGHT );
        
        password1.focus();
	}

	public static boolean assertCurrentPassword( AppContext ctx, String newPassword )
	{
		boolean valid = !Utils.MD5( newPassword ).equals( ctx.getUser().getPwd() );
		
		if ( !valid )
			Utils.showNotification( ctx, ctx.getString( "ChangePasswordDlg.different" ), Notification.Type.ERROR_MESSAGE );
		
		return valid;
	}
	
	public static boolean assertLastPasswords( AppContext ctx, UserDefault lastPassword1, UserDefault lastPassword2, String newPassword )
	{
		if ( ctx.getIntegerParameter( Parameter.PAR_STRENGTH_REUSE ).equals( 0 ) )
			return true;
		
		boolean valid = !lastPassword1.getData_value().equalsIgnoreCase( Utils.MD5( newPassword ) ) && 
				!lastPassword2.getData_value().equalsIgnoreCase( Utils.MD5( newPassword ) );
		
		if ( !valid )
			Utils.showNotification( ctx, ctx.getString( "ChangePasswordDlg.different" ), Notification.Type.ERROR_MESSAGE );
		
		return valid;
	}
	
	public static boolean assertNotLogin( AppContext ctx, String newPassword )
	{
		if ( ctx.getIntegerParameter( Parameter.PAR_STRENGTH_LOGIN ).equals( 0 ) )
			return true;
		
		boolean valid = !newPassword.toUpperCase().contains( ctx.getUser().getLogin().toUpperCase() );
		
		if ( !valid )
			Utils.showNotification( ctx, ctx.getString( "ChangePasswordDlg.not.login" ), Notification.Type.ERROR_MESSAGE );
		
		return valid;
	}

	public static boolean assertMinSize( AppContext ctx, String newPassword )
	{
		if ( ctx.getIntegerParameter( Parameter.PAR_STRENGTH_SIZE ).equals( 0 ) )
			return true;
	
		int minSize = ctx.getIntegerParameter( Parameter.PAR_PASSWORD_MIN_SIZE );
		
		boolean valid = newPassword.length() >= minSize;
		
		if ( !valid )
			Utils.showNotification( ctx, ctx.getString( "ChangePasswordDlg.size" ).replaceAll( "%size%", Integer.toString( minSize ) ), Notification.Type.ERROR_MESSAGE );
		
		return valid;
	}
	
	public static boolean assertUpperCase( AppContext ctx, String newPassword )
	{
		if ( ctx.getIntegerParameter( Parameter.PAR_STRENGTH_CAPITAL ).equals( 0 ) )
			return true;
	
		for ( int i = 0; i < newPassword.length(); i++ )
			if ( Character.isUpperCase( newPassword.charAt( i ) ) )
				return true;
					
		Utils.showNotification( ctx, ctx.getString( "ChangePasswordDlg.uppercase" ), Notification.Type.ERROR_MESSAGE );
					
		return false;
	}

	public static boolean assertDigit( AppContext ctx, String newPassword )
	{
		if ( ctx.getIntegerParameter( Parameter.PAR_STRENGTH_DIGIT ).equals( 0 ) )
			return true;

		for ( int i = 0; i < newPassword.length(); i++ )
			if ( Character.isDigit( newPassword.charAt( i ) ) )
				return true;
					
		Utils.showNotification( ctx, ctx.getString( "ChangePasswordDlg.digit" ), Notification.Type.ERROR_MESSAGE );
					
		return false;
	}
	
	public static boolean assertSymbol( AppContext ctx, String newPassword )
	{
		if ( ctx.getIntegerParameter( Parameter.PAR_STRENGTH_SYMBOL ).equals( 0 ) )
			return true;

		String specialChars = "/*!@#$%^&*()\"{}_[]|\\?/<>,.";
		
		for ( int i = 0; i < newPassword.length(); i++ )
			if ( specialChars.indexOf( newPassword.charAt( i ) ) != -1 )
				return true;
					
		Utils.showNotification( ctx, ctx.getString( "ChangePasswordDlg.symbol" ), Notification.Type.ERROR_MESSAGE );
					
		return false;
	}
	
	private void saveLastPasswords( String lastPassword )
	{
		String last1 = lastPassword1.getData_value().isEmpty() ? lastPassword : lastPassword1.getData_value(); 

		IOCManager._UserDefaultsManager.setUserDefault( ctx, lastPassword2, last1 ); 
		IOCManager._UserDefaultsManager.setUserDefault( ctx, lastPassword1, lastPassword ); 
	}
	
	private void onChangePassword()
	{
    	String pwd1 = (String) password1.getValue();
    	String pwd2 = (String) password2.getValue();

    	if ( pwd1.equals( pwd2 ) )
    	{
	       	try 
	       	{
	       		User usuario = ctx.getUser();
	       		
	       		if ( assertNotLogin( ctx, pwd1 ) &&
	       				assertCurrentPassword( ctx, pwd1 ) && 
	       				assertLastPasswords( ctx, lastPassword1, lastPassword2, pwd1 ) && 
	       				assertMinSize( ctx, pwd1 ) && 
	       				assertUpperCase( ctx, pwd1 ) && 
	       				assertDigit( ctx, pwd1 ) && 
	       				assertSymbol( ctx, pwd1 ) )
				{
	       			saveLastPasswords( ctx.getUser().getPwd() );
	       			
					usuario.setPwd( pwd1 );
					usuario.setStatus( User.PASS_OK );	
					usuario.setRetries( 0 );
	    	    	
					IOCManager._UsersManager.setPassword( ctx, usuario, "", "", false );
	
		           	setChanged( true );
	
		           	getUI().removeWindow( this );
				}
	       	}
	       	catch ( BaseException e ) 
	        {
	        	switch ( e.getErrorCode() )
	        	{
	            	default:
	            		Utils.showNotification( ctx, ctx.getString( "error.unknown" ) + e.getErrorCode(), Notification.Type.ERROR_MESSAGE );
	            	break;
				}
	        }
    	}
    	else
    		Utils.showNotification( ctx, ctx.getString( "ChangePasswordDlg.mismatch" ), Notification.Type.ERROR_MESSAGE );
    }
	
	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
}
