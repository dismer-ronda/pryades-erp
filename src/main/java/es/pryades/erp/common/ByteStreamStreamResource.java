package es.pryades.erp.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.vaadin.server.StreamResource;

public class ByteStreamStreamResource implements StreamResource.StreamSource 
{
	private static final long serialVersionUID = 154321468634695513L;

	private byte[] source;

	public ByteStreamStreamResource( byte[] source )
	{
		this.source = source;
	}

	public InputStream getStream () 
	{
		return new ByteArrayInputStream( source );
	}
}
