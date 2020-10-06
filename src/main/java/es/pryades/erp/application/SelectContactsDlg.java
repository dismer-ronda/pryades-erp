package es.pryades.erp.application;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.dto.CompanyContact;
import lombok.Getter;
import lombok.Setter;

public class SelectContactsDlg extends Window 
{
	private static final long serialVersionUID = -8060700375740929738L;

	private VerticalLayout layout;

	@Getter private AppContext context;
	
	@Getter	private BeanItem<SelectContactsDlg> bi;

	@Getter @Setter
	private List<CompanyContact> contacts;
	private Panel panelLines;
	private List<CheckBox> checksLines;
	private String copy;
	
	@Getter @Setter private List<CompanyContact> selected_contacts = new ArrayList<CompanyContact>();

	@Getter private boolean accepted = false;
	
	public SelectContactsDlg( AppContext ctx, String title, List<CompanyContact> contacts, String copy )
	{
		super( title );
		
		this.context = ctx;
		this.contacts = contacts;
		this.copy = copy;
		
		setContent( layout = new VerticalLayout() );
		
		layout.setMargin( true );
		layout.setSpacing( true );
		
		addComponents();
		
		center();
		
		setModal( true );
		setResizable( false );
		setClosable( true );
	}
	
	private boolean isAlreadyIncluded( String email )
	{
		if ( copy != null )
		{
			String parts[] = copy.split( "," );
			
			for ( String part : parts )
			{
				if ( part.trim().equalsIgnoreCase( email ) )
					return true;
			}
		}
		
		return false;
	}

	private void addComponents() 
	{
		bi = new BeanItem<SelectContactsDlg>( this );

		panelLines = new Panel();
		panelLines.setStyleName( "borderless light" );

		VerticalLayout rows = new VerticalLayout();
		rows.setWidth( "100%" );
		rows.setSpacing( true );

		checksLines = new ArrayList<CheckBox>();
		
		for ( CompanyContact contact : contacts )
		{
			if ( !contact.getEmail().isEmpty() && !isAlreadyIncluded( contact.getEmail() ) )
			{
				CheckBox checkLine = new CheckBox( contact.getName() + " - " + contact.getEmail() );
				checkLine.setData( contact );
				rows.addComponent( checkLine );
				checksLines.add( checkLine );
			}
		}

		panelLines.setContent( rows );
		
		Button buttonOk = new Button( context.getString( "words.ok" ) );
		buttonOk.setClickShortcut( KeyCode.ENTER );
		buttonOk.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = -2190098384878545411L;

			public void buttonClick(ClickEvent event) 
            {
				accepted = true;
				
				for ( CheckBox check : checksLines )
					if ( check.getValue().booleanValue() )
						selected_contacts.add( (CompanyContact)check.getData() );

				close();
            }
        });
		
		Button buttonCancel = new Button( context.getString( "words.cancel" ) );
		buttonCancel.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = -7893392115697888958L;

			public void buttonClick(ClickEvent event) 
            {
				close();
            }
        });
		
		Label dummy = new Label();
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( panelLines );
		
		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( dummy );
		row3.addComponent( buttonOk );
		row3.addComponent( buttonCancel );
		row3.setExpandRatio( dummy, 1.0f );

		layout.addComponent( row1 );
		layout.addComponent( row3 );
	}
}
