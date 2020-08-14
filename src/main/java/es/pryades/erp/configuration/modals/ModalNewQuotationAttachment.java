package es.pryades.erp.configuration.modals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import es.pryades.erp.application.ShowExternalUrlDlg;
import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Authorization;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.ModalWindowsCRUD;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.QuotationAttachment;
import es.pryades.erp.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewQuotationAttachment extends ModalWindowsCRUD implements Receiver, SucceededListener
{
	private static final long serialVersionUID = 6211386099409894453L;

	private static final Logger LOG = Logger.getLogger( ModalNewQuotationAttachment.class );

	protected QuotationAttachment newAttachment;

	private TextField editTitle;
	private TextField editFormat;
	private Label labelAttachment;
	
	private String fileName;
	private ByteArrayOutputStream os;

	/**
	 * editReference_order
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewQuotationAttachment( AppContext context, Operation modalOperation, QuotationAttachment orgParameter, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgParameter );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		loadUserDefaults();

		try
		{
			newAttachment = (QuotationAttachment) Utils.clone( (QuotationAttachment) orgDto );
		}
		catch ( Throwable e1 )
		{
			newAttachment = new QuotationAttachment();
			newAttachment.setRef_quotation( ((ModalNewQuotation)getModalParent()).getNewQuotation().getId() );
		}

		bi = new BeanItem<BaseDto>( newAttachment );

		editTitle = new TextField( getContext().getString( "modalNewQuotationAttachment.editTitle" ), bi.getItemProperty( "title" ) );
		editTitle.setWidth( "100%" );
		editTitle.setNullRepresentation( "" );
		
		editFormat = new TextField( getContext().getString( "modalNewQuotationAttachment.editFormat" ), bi.getItemProperty( "format" ) );
		editFormat.setWidth( "100%" );
		editFormat.setNullRepresentation( "" );

		Upload upload = new Upload( null, this );
		upload.setImmediate( true );
		upload.setButtonCaption( getContext().getString( "modalNewQuotationAttachment.captionUploadButton" ) );
		upload.addSucceededListener( this );

		labelAttachment = new Label();

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( editTitle );
		row1.addComponent( editFormat );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setSpacing( true );
		row2.addComponent( labelAttachment );
		row2.setComponentAlignment( labelAttachment, Alignment.MIDDLE_CENTER );
		
		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		
		getCustomOperationsRow().addComponent( upload );
		
		if ( operation.equals( Operation.OP_MODIFY ) )
		{
			Button bttnView = new Button( getContext().getString( getWindowResourceKey() + ".operation.view" ) );
			bttnView.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = -2490601144444681572L;

				public void buttonClick( ClickEvent event )
				{
					showAttachment();
				}
			} );
			
			getDefaultOperationsRow().addComponentAsFirst( bttnView );
			getDefaultOperationsRow().setComponentAlignment( bttnView, Alignment.MIDDLE_LEFT );
		}
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewQuotationAttachment";
	}

	@Override
	protected void defaultFocus()
	{
		editTitle.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newAttachment.setId( null );

			IOCManager._QuotationsAttachmentsManager.setRow( getContext(), null, newAttachment );

			saveUserDefaults();

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onModify()
	{
		try
		{
			IOCManager._QuotationsAttachmentsManager.setRow( getContext(), (QuotationAttachment) orgDto, newAttachment );

			saveUserDefaults();

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onDelete()
	{
		try
		{
			IOCManager._QuotationsAttachmentsManager.delRow( getContext(), newAttachment );

			return true;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
			showErrorMessage( e );
		}

		return false;
	}

	private void loadUserDefaults()
	{
	}

	private void saveUserDefaults()
	{
	}
	
	@Override
	public OutputStream receiveUpload( String filename, String mimeType )
	{
		os = new ByteArrayOutputStream();
		fileName = filename;
		
		return os;
	}

	@Override
	public void uploadSucceeded( SucceededEvent event )
	{
		newAttachment.setData( os.toByteArray() );
		
		labelAttachment.setCaption( fileName + " " + getContext().getString( "words.success" ) );
	}
	
	public void showAttachment()
	{
		try
		{
			long ts = CalendarUtils.getTodayAsLong( "UTC" );
			
			String timeout = "60";
			
			String extra = "ts=" + ts + 
							"&id=" + newAttachment.getId() + 
							"&timeout=" + timeout;
			
			String user = getContext().getUser().getLogin();
			String password = getContext().getUser().getPwd();
			
			String token = "token=" + Authorization.getTokenString( "" + ts + timeout, password );
			String code = "code=" + Authorization.encrypt( extra, password ) ;

			LOG.debug( "extra " +  extra );
			
			String url = getContext().getData( "Url" ) + "/services/attachment" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
			
			String caption = getContext().getString( "template.quotation.quotation" );

			ShowExternalUrlDlg dlg = new ShowExternalUrlDlg(); 
	
			dlg.setContext( getContext() );
			dlg.setUrl( url );
			dlg.setCaption( caption );
			dlg.createComponents();
			
			getUI().addWindow( dlg );
		}

		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}

	@Override
	protected boolean hasDelete()
	{
		return getContext().hasRight( "configuration.quotations.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.quotations.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.quotations.modify" );
	}
}
