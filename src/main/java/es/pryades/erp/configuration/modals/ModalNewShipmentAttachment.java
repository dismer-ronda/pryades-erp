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
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.ProgressListener;
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
import es.pryades.erp.dto.ShipmentAttachment;
import es.pryades.erp.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewShipmentAttachment extends ModalWindowsCRUD implements Receiver, SucceededListener, FailedListener, ProgressListener
{
	private static final long serialVersionUID = 2922481050850757630L;

	private static final Logger LOG = Logger.getLogger( ModalNewShipmentAttachment.class );

	protected ShipmentAttachment newAttachment;

	private TextField editTitle;
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
	public ModalNewShipmentAttachment( AppContext context, OperationCRUD modalOperation, ShipmentAttachment orgParameter, ModalParent parentWindow )
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
			newAttachment = (ShipmentAttachment) Utils.clone( (ShipmentAttachment) orgDto );
		}
		catch ( Throwable e1 )
		{
			newAttachment = new ShipmentAttachment();
			newAttachment.setRef_shipment( ((ModalNewShipment)getModalParent()).getNewShipment().getId() );
		}

		bi = new BeanItem<BaseDto>( newAttachment );

		editTitle = new TextField( getContext().getString( "modalNewShipmentAttachment.editTitle" ), bi.getItemProperty( "title" ) );
		editTitle.setWidth( "100%" );
		editTitle.setNullRepresentation( "" );
		
		Upload upload = new Upload( null, this );
		upload.setImmediate( true );
		upload.setButtonCaption( getContext().getString( "modalNewShipmentAttachment.captionUploadButton" ) );
		upload.addSucceededListener( this );

		labelAttachment = new Label();

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( editTitle );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setSpacing( true );
		row2.addComponent( labelAttachment );
		row2.setComponentAlignment( labelAttachment, Alignment.MIDDLE_CENTER );
		
		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		
		getCustomOperationsRow().addComponent( upload );
		
		if ( operation.equals( OperationCRUD.OP_MODIFY ) )
		{
			Button bttnView = new Button( getContext().getString( getWindowResourceKey() + ".operation.view" ) );
			bttnView.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 4468479747577191721L;

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
		return "modalNewShipmentAttachment";
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

			IOCManager._ShipmentsAttachmentsManager.setRow( getContext(), null, newAttachment );

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
			IOCManager._ShipmentsAttachmentsManager.setRow( getContext(), (ShipmentAttachment) orgDto, newAttachment );

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
			IOCManager._ShipmentsAttachmentsManager.delRow( getContext(), newAttachment );

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
		
		LOG.error( "upload file " + filename );
		return os;
	}

	@Override
	public void uploadSucceeded( SucceededEvent event )
	{
		newAttachment.setData( os.toByteArray() );
		
		labelAttachment.setCaption( fileName + " " + getContext().getString( "words.success" ) );
	}
	
	@Override
	public void uploadFailed( FailedEvent event )
	{
		LOG.error( "Upload Fail <><> File: " + event.getFilename() );
		
		os = null;
		//upload.setEnabled( true );
		//editMd5.setEnabled( false );
	}
	
	@Override
	public void updateProgress( long readBytes, long contentLength )
	{
		LOG.info( "upload " + readBytes + " of " + contentLength );
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
			
			String url = getContext().getData( "Url" ) + "/services/shipment-attachment" + "?user=" + user + "&" + token + "&" + code + "&ts=" + ts;
			
			String caption = getContext().getString( "template.shipment.quotation" );

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
		return getContext().hasRight( "configuration.shipments.delete" );
	}

	@Override
	public boolean checkAddRight()
	{
		return getContext().hasRight( "configuration.shipments.add" );
	}

	@Override
	public boolean checkModifyRight()
	{
		return getContext().hasRight( "configuration.shipments.modify" );
	}
}
