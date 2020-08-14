package es.pryades.erp.common;

import lombok.Getter;

import com.vaadin.event.MouseEvents;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class ErrorPopupWindow extends Window
{
	private static final long serialVersionUID = 45620281210923008L;

	private VerticalLayout layout;
	
	@Getter 
	AppContext context;
	
	public ErrorPopupWindow( AppContext context, String text )
	{
		super();
		
		this.context = context;

		addStyleName( "ErrorPopupWindow" );
		
		//setCloseShortcut( KeyCode.ESCAPE );
		center();

		setWidth( "-1px" );
		setHeight( "-1px" );

		setModal( true );
		setResizable( false );
		setClosable( false );
		setDraggable( false );

		setContent( layout = new VerticalLayout() );
		
		layout.setSizeUndefined();
		layout.setWidth( "100%" );
		layout.setMargin( true );
		layout.setSpacing( true );

		String parts[] = text.split( "\n" );
		
		for ( String part : parts )
		{
			Label label = new Label( part );
			label.setWidth( "100%" );
			
			layout.addComponent( label );
		}
		
		addClickListener(new MouseEvents.ClickListener() 
		{
			private static final long serialVersionUID = -5497594731565378787L;

			@Override
	        public void click(MouseEvents.ClickEvent event) 
			{
				if ( event.getButton().equals( MouseButton.LEFT ) )
					closeModalWindow();         
	        }
	    });
	}

	public void initComponents()
	{
	}

	public void showModalWindow()
	{
		((UI)getContext().getData( "Application" )).addWindow( this );
	}

	public void closeModalWindow()
	{
		((UI)getContext().getData( "Application" )).removeWindow( this );
	}
}
