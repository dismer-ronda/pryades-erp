package es.pryades.erp.common;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class MessageDlg extends BaseWindow 
{
	private static final long serialVersionUID = 6980809303878600640L;
	
	private VerticalLayout layout;
	private GridLayout grid;
	
	private Button button1;
	private Label label1;
	
	public MessageDlg( AppContext ctx, String title, String message )
	{
		super( ctx, title );
		
		layout = new VerticalLayout();
		layout.setMargin( true );
		layout.setSpacing( true );
		layout.setSizeUndefined();
		
		setContent( layout );
		
		grid = new GridLayout();
		
	    grid.setColumns( 2 );
		grid.setMargin( true );
		grid.setSpacing( true );
		grid.setSizeUndefined();

	    layout.addComponent( grid );
	    
		addComponents();
		
		label1.setValue( message );

		setModal( true );
		center();
		setResizable( false );
	}

	private void addComponents() 
	{
		label1 = AppUtils.createLabel( "", "500px", grid );

		button1 = AppUtils.createButton( getContext().getString( "words.close" ), getContext().getString( "words.close" ), "MessageDlg.close", layout );
		//button1.setIcon( new ThemeResource( "images/cancel.png" ) );
        button1.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -8473221545290066157L;

			public void buttonClick(ClickEvent event) 
            {
				onClose();
            }
        });
        layout.setComponentAlignment( button1, Alignment.BOTTOM_RIGHT );
	}
		
	private void onClose()
	{
		getUI().removeWindow( this );
	}
	
	public static void showMessage( String title, String msg, BaseWindow parent )
	{
		parent.getUI().addWindow( new MessageDlg( parent.getContext(), title, msg ) );
	}
}
