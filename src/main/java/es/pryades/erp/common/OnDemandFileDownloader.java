package es.pryades.erp.common;

import java.io.IOException;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;

public class OnDemandFileDownloader extends FileDownloader
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1112909251192411784L;
	private final OnDemandStreamSource onDemandStreamSource;
	
	public OnDemandFileDownloader( OnDemandStreamSource onDemandStreamSource )
	{
		super( new StreamResource( onDemandStreamSource, "" ) );

		this.onDemandStreamSource = onDemandStreamSource;
	}

	@Override
	public boolean handleConnectorRequest( VaadinRequest request, VaadinResponse response, String path ) throws IOException
	{
		if ( !onDemandStreamSource.prepareDownload( this ) )
			return false;
		
		((StreamResource)getFileDownloadResource()).setFilename( onDemandStreamSource.getFilename() );
        
		return super.handleConnectorRequest( request, response, path );
	}
}