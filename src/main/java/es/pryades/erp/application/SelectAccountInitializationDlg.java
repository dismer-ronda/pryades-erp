package es.pryades.erp.application;

import java.util.Date;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.erp.common.AppContext;
import lombok.Getter;
import lombok.Setter;

public class SelectAccountInitializationDlg extends Window 
{
	private static final long serialVersionUID = -4180417145289855782L;

	private VerticalLayout layout;

	private PopupDateField paymentDateField;
	private TextField editAmount;
	
	@Getter
	private AppContext context;
	
	@Getter @Setter
	private boolean success = false;

	@Getter @Setter private Date date;
	@Getter @Setter private Double amount;
	
	@SuppressWarnings("rawtypes")
	private BeanItem bi = new BeanItem<SelectAccountInitializationDlg>( this );

	public SelectAccountInitializationDlg( AppContext ctx )
	{
		super( ctx.getString( "selectAccountInitializationDlg.title" ) );
		
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
		paymentDateField = new PopupDateField(getContext().getString( "selectTransactionDlg.popupDate" ), bi.getItemProperty( "date" ) );
		paymentDateField.setResolution( Resolution.DAY );
		paymentDateField.setDateFormat( "dd-MM-yyyy" );
		paymentDateField.setWidth( "100%" );
		paymentDateField.setRequired( true );
		paymentDateField.setValue( new Date() );
		paymentDateField.setRequiredError( getContext().getString( "words.required" ) );
		paymentDateField.setInvalidCommitted( true );
		
		editAmount = new TextField( getContext().getString( "selectAccountInitializationDlg.editAmount" ), bi.getItemProperty( "amount" ) );
		editAmount.setWidth( "100%" );
		editAmount.setNullRepresentation( "" );
		
		Button button1 = new Button( getContext().getString( "words.ok" ) );
		button1.setClickShortcut( KeyCode.ENTER );
		button1.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = 3824851200590527012L;

			public void buttonClick(ClickEvent event) 
            {
				onOk(); 
            }
        });
		
		Button button2 = new Button( getContext().getString( "words.cancel" ) );
		button2.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = 8623368183815565963L;

			public void buttonClick(ClickEvent event) 
            {
				close();
            }
        });
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( paymentDateField );
		row1.addComponent( editAmount );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setSpacing( true );
		row2.addComponent( button1 );
		row2.addComponent( button2 );

		layout.addComponent( row1 );
		layout.addComponent( row2 );
		layout.setComponentAlignment( row2, Alignment.BOTTOM_RIGHT );
        
        editAmount.focus();
	}

	private void onOk()
	{
		success = true;
		
		close();
    }
}
