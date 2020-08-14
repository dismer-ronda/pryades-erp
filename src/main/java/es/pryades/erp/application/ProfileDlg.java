package es.pryades.erp.application;

import lombok.Getter;
import lombok.Setter;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.AppUtils;
import es.pryades.erp.common.Utils;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ProfileDlg extends Window 
{
	private static final long serialVersionUID = 7640800684408118855L;

	private static final String COMPONENT_WIDTH = "250px";

	private VerticalLayout layout;
	private GridLayout grid;

	@Getter @Setter AppContext context;

	public ProfileDlg( String title )
	{
		super( title );
		
    	setModal(true);
		setResizable(false);
		setClosable(true);
		
		layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeUndefined();

		setContent(layout);
	}

	public void addComponents() 
	{
		grid = new GridLayout();
		
	    grid.setColumns( 2 );
	    grid.setColumnExpandRatio(1, (float) 1.0);
		grid.setMargin( false );
		grid.setSpacing( true );

		AppUtils.createLabel( getContext().getString( "words.login.noun" ), "120px", grid );
		AppUtils.createInput(getContext().getUser().getLogin(), COMPONENT_WIDTH, true, grid, null );

		AppUtils.createLabel( getContext().getString( "words.email" ), "120px", grid );
		AppUtils.createInput( getContext().getUser().getEmail(), COMPONENT_WIDTH, true, grid, null );

		AppUtils.createLabel( getContext().getString( "words.name" ), "120px", grid );
		AppUtils.createInput( getContext().getUser().getName(), COMPONENT_WIDTH, true, grid, null );

		/*AppUtils.createLabel( getContext().getString( "words.institution" ), "120px", grid );
		AppUtils.createInput( getContext().getUsuario().getRazon(), COMPONENT_WIDTH, true, grid, null );*/

        layout.addComponent( grid );
		layout.setComponentAlignment( grid, Alignment.MIDDLE_CENTER );
        
        HorizontalLayout rowButtons = AppUtils.createRow( false, true );
        
        Button button1 = new Button( getContext().getString( "ProfileDlg.change.password" ) );
		rowButtons.addComponent( button1 );
        button1.addClickListener(new Button.ClickListener() 
        {
			private static final long serialVersionUID = -3115613691360392441L;

			public void buttonClick(ClickEvent event) 
            {
				doChangePassword();
            }
        });
        
        layout.addComponent( rowButtons );
        layout.setComponentAlignment( rowButtons, Alignment.MIDDLE_RIGHT );
	}
	
	private void onPasswordChanged( CloseEvent event )
	{
		ChangePasswordDlg dlg = (ChangePasswordDlg)event.getWindow();

    	if ( dlg.isChanged() )
    		Utils.showNotification( getContext(), getContext().getString( "ProfileDlg.password.changed" ), Notification.Type.TRAY_NOTIFICATION );
	}
	
	private void doChangePassword()
	{
		ChangePasswordDlg dlg = new ChangePasswordDlg( getContext().getString( "ChangePasswordDlg.title" ), getContext(), null );
		
		dlg.addCloseListener
		( 
			new Window.CloseListener() 
			{
				private static final long serialVersionUID = -3014889015338025115L;

				@Override
			    public void windowClose( CloseEvent e ) 
			    {
					onPasswordChanged( e );
			    }
			}
		);
		
		getUI().addWindow( dlg );
	}
}
