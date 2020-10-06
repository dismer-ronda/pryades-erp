package es.pryades.erp.application;

import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.AppUtils;
import es.pryades.erp.dto.CompanyContact;
import lombok.Getter;
import lombok.Setter;

public class SelectContactDlg extends Window 
{
	private static final long serialVersionUID = -8060700375740929738L;

	private VerticalLayout layout;

	@Getter private AppContext context;
	
	private ComboBox comboContacts;
	
	@Getter	private BeanItem<SelectContactDlg> bi;

	@Getter @Setter
	private List<CompanyContact> contacts;
	@Getter @Setter
	private CompanyContact contact;

	@Getter private boolean accepted = false;
	
	public SelectContactDlg( AppContext ctx, String title, List<CompanyContact> contacts )
	{
		super( title );
		
		this.context = ctx;
		this.contacts = contacts;
		
		setContent( layout = new VerticalLayout() );
		
		layout.setMargin( true );
		layout.setSpacing( true );
		
		layout.setWidth( "480px" );
		
		addComponents();
		
		center();
		
		setModal( true );
		setResizable( false );
		setClosable( true );
	}

	private void addComponents() 
	{
		bi = new BeanItem<SelectContactDlg>( this );
		
		comboContacts = new ComboBox(getContext().getString( "SelectContactDlg.comboContact" ) );
		comboContacts.setWidth( "100%" );
		comboContacts.setNullSelectionAllowed( false );
		comboContacts.setTextInputAllowed( false );
		comboContacts.setImmediate( true );
		comboContacts.setRequired( true );
		fillComboContacts();
		comboContacts.setPropertyDataSource( bi.getItemProperty( "contact" ) );

		Button button1 = AppUtils.createButton( context.getString( "words.ok" ), context.getString( "words.ok" ), "SelectContactDlg.ok", layout );
		button1.setClickShortcut( KeyCode.ENTER );
		button1.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = -2190098384878545411L;

			public void buttonClick(ClickEvent event) 
            {
				accepted = true;
				
				onOk();
            }
        });
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( comboContacts );
		
		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.addComponent( button1 );
		row3.setComponentAlignment( button1, Alignment.BOTTOM_RIGHT );

		layout.addComponent( row1 );
		layout.addComponent( row3 );
        
        comboContacts.focus();
	}

	private void onOk()
	{
		close();
    }

	private void fillComboContacts()
	{
		for ( CompanyContact contact : contacts )
		{
			comboContacts.addItem( contact );
			comboContacts.setItemCaption( contact, contact.getName() );
		}
	}
}
