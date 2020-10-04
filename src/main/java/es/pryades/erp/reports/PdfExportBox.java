package es.pryades.erp.reports;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.dto.ShipmentBox;

/**
 * @author dismer.ronda
 *
 */

public class PdfExportBox extends PdfExport
{
	private static final long serialVersionUID = -7922719479277947300L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( PdfExportBox.class );
	
	private ShipmentBox box;
	
	public PdfExportBox( ShipmentBox box )
	{
		this.box = box;
	}

	public VelocityContext createVelocityContext() throws BaseException
	{
		VelocityContext vcontext = super.createVelocityContext();
		
		vcontext.put( "title", getContext().getString( "template.box.contents" ) );
		vcontext.put( "box", box );
		
		return vcontext;
	}
}
