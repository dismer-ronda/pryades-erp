package es.pryades.erp.application;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Attachment;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.Parameter;
import lombok.Getter;
import lombok.Setter;

public class SendEmailDlg extends Window 
{
	private static final long serialVersionUID = 5157799289057010585L;

	private static final Logger LOG = Logger.getLogger( SendEmailDlg.class );

	private VerticalLayout layout;

	private TextField editTo;
	private TextField editCopy;
	private TextField editSubject;
	private TextArea editBody;
	private Label labelAttachments;
	private List<CheckBox> checksLines;

	@Getter
	private AppContext context;
	
	@Getter @Setter
	private String reply_to;
	@Getter @Setter
	private String to;
	@Getter @Setter
	private String copy;
	@Getter @Setter
	private String subject;
	@Getter @Setter
	private String body;
	@Getter @Setter
	private List<Attachment> attachments;
	@Getter @Setter
	private boolean success;

	protected BeanItem<SendEmailDlg> bi;

	public SendEmailDlg( AppContext ctx, String title, List<Attachment> attachments )
	{
		super( title );
		
		this.context = ctx;
		this.attachments = attachments;
		
		setContent( layout = new VerticalLayout() );
		
		layout.setMargin( true );
		layout.setSpacing( true );
		
		layout.setWidth( "100%" );
		
		addComponents();
		
		center();
		
		setModal( true );
		setResizable( false );
		setClosable( true );

		success = false;
		
		setWidth( "60%" );
	}

	private void addComponents() 
	{
		bi = new BeanItem<SendEmailDlg>( this );
		
		editTo = new TextField( getContext().getString( "SendEmailDlg.editTo" ), bi.getItemProperty( "to" ) );
		editTo.setWidth( "100%" );
		editTo.setNullRepresentation( "" );
		
		editCopy = new TextField( getContext().getString( "SendEmailDlg.editCopy" ), bi.getItemProperty( "copy" ) );
		editCopy.setWidth( "100%" );
		editCopy.setNullRepresentation( "" );
		
		editSubject = new TextField( getContext().getString( "SendEmailDlg.editSubject" ), bi.getItemProperty( "subject" ) );
		editSubject.setWidth( "100%" );
		editSubject.setNullRepresentation( "" );
		
		editBody = new TextArea( getContext().getString( "SendEmailDlg.editBody" ), bi.getItemProperty( "body" ) );
		editBody.setWidth( "100%" );
		editBody.setNullRepresentation( "" );
		editBody.setRows( 10 );
		
		Panel panelLines = new Panel();
		panelLines.setStyleName( "borderless light" );
		//panelLines.setHeight( "200px" );

		CssLayout col = new CssLayout();

		checksLines = new ArrayList<CheckBox>();

		for ( Attachment customer: attachments )
		{
			CheckBox checkLine = new CheckBox( customer.getName() );
			checkLine.setData( customer );
			checkLine.setValue( true ); 

			col.addComponent( checkLine );
			checksLines.add( checkLine );
		}
		
		panelLines.setContent( col );

		Button button1 = new Button( getContext().getString( "SendEmailDlg.bttnSend" ) );
		button1.setClickShortcut( KeyCode.ENTER );
		button1.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = 2677633150060557037L;

			public void buttonClick(ClickEvent event) 
            {
				sendEmail(); 
            }
        });
		
		Button button2 = new Button( getContext().getString( "SendEmailDlg.bttnCancel" ) );
		button2.addClickListener(new Button.ClickListener() 
		{
			private static final long serialVersionUID = 5251169961401920411L;

			public void buttonClick(ClickEvent event) 
            {
				close();
            }
        });
		
		HorizontalLayout row0 = new HorizontalLayout();
		row0.setWidth( "100%" );
		row0.setSpacing( true );
		row0.addComponent( editTo );

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( editCopy );
		
		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( editSubject );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( editBody );
		
		HorizontalLayout row4 = new HorizontalLayout();
		row4.setSpacing( true );
		row4.addComponent( button1 );
		row4.addComponent( button2 );

		layout.addComponent( row0 );
		layout.addComponent( row1 );
		layout.addComponent( row2 );
		layout.addComponent( row3 );
		layout.addComponent( panelLines );
		layout.addComponent( row4 );
		layout.setComponentAlignment( row4, Alignment.BOTTOM_RIGHT );
        
        editCopy.focus();
	}
	
	public void sendEmail()
	{
		String email = getContext().getParameter( Parameter.PAR_MAIL_SENDER_EMAIL );
		String user = getContext().getParameter( Parameter.PAR_MAIL_SENDER_USER );
		String pass = getContext().getParameter( Parameter.PAR_MAIL_SENDER_PASSWORD );
		String host = getContext().getParameter( Parameter.PAR_MAIL_HOST_ADDRESS );
		String port = getContext().getParameter( Parameter.PAR_MAIL_HOST_PORT );
		String auth = getContext().getParameter( Parameter.PAR_MAIL_AUTH );
		
		try
		{
			List<Attachment> atts = new ArrayList<Attachment>();
			for ( CheckBox check : checksLines )
			{
				if ( check.getValue().booleanValue() )
					atts.add( (Attachment)check.getData() );
			}
	
			Utils.sendMail( email, to, reply_to, subject, host, port, user, pass, body, atts, "", "", "true".equals( auth ) );
			
			Utils.showNotification( getContext(), getContext().getString( "SendEmailDlg.emailSent" ), Notification.Type.TRAY_NOTIFICATION );
			
			success = true;
			
			close();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );

			Utils.showNotification( getContext(), getContext().getString( "SendEmailDlg.emailError" ), Notification.Type.ERROR_MESSAGE );
		}
	}
}
