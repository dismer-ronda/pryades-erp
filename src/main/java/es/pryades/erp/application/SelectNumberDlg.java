package es.pryades.erp.application;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.AppUtils;
import lombok.Getter;

public class SelectNumberDlg extends Window 
{
	private static final long serialVersionUID = 5547567173227206084L;

	private VerticalLayout layout;

	private TextField editNumber;
	
	@Getter
	private AppContext context;
	
	@Getter
	private String value;

	public SelectNumberDlg( AppContext ctx, String title )
	{
		super( title );
		
		this.context = ctx;
		
		setContent( layout = new VerticalLayout() );
		
		layout.setMargin( true );
		layout.setSpacing( true );
		layout.setSizeUndefined();
		
		addComponents();
		
		center();
		
		setModal( true );
		setResizable( false );
		setClosable( true );
	}

	private void addComponents() 
	{
		editNumber = new TextField( getContext().getString( "SelectNumberDlg.editNumber" ) );
		editNumber.setWidth( "100%" );
		editNumber.setNullRepresentation( "" );
		
		Button button1 = AppUtils.createButton( context.getString( "words.ok" ), context.getString( "words.ok" ), "SelectNumberDlg.ok", layout );
		button1.setClickShortcut( KeyCode.ENTER );
		button1.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = 31563218448960612L;

			public void buttonClick(ClickEvent event) 
            {
				onOk();
            }
        });
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( editNumber );
		row1.addComponent( button1 );
		row1.setComponentAlignment( button1, Alignment.BOTTOM_LEFT );

		layout.addComponent( row1 );
        
        editNumber.focus();
	}

	private void onOk()
	{
		value = editNumber.getValue();
		
		close();
    }
}
