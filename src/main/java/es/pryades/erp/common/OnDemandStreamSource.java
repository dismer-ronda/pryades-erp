package es.pryades.erp.common;

import com.vaadin.server.StreamResource.StreamSource;

public interface OnDemandStreamSource extends StreamSource
{
	boolean prepareDownload( OnDemandFileDownloader downloader );
	String getFilename();
}