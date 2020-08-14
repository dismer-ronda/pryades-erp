package es.pryades.erp.application;

import org.apache.log4j.Logger;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

import es.pryades.erp.common.AppContext;

public class LoginWnd extends VerticalLayout 
{
	private static final long serialVersionUID = -4458035184302236190L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( LoginWnd.class );

	private LoginPanel loginPanel;
	
	public LoginWnd( AppContext ctx, String user, String password )
	{
		super();
	    
		setMargin( false );
		setSpacing( false );
		
		setSizeFull();
		
	    loginPanel = new LoginPanel( ctx, this, user, password );
		addComponent ( loginPanel );
		setComponentAlignment( loginPanel, Alignment.MIDDLE_CENTER );
	}
}
