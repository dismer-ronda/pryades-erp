package es.pryades.erp.application;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import lombok.Getter;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class UploadFileDlg extends Window implements Receiver, SucceededListener
{
	private static final long serialVersionUID = 3637744052046646334L;

	private VerticalLayout layout;

	@Getter
	private ByteArrayOutputStream os;

	public UploadFileDlg( String title, String label, String button )
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

		Upload upload = new Upload( label, this );
		upload.setButtonCaption( button );
		upload.addSucceededListener( this );
		
		layout.addComponent( upload );
	}

	@Override
	public OutputStream receiveUpload( String filename, String mimeType )
	{
		os = new ByteArrayOutputStream();
		
		return os;
	}

	@Override
	public void uploadSucceeded( SucceededEvent event )
	{
		close();
	}
}
