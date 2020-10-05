package es.pryades.erp.reports;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.dto.Shipment;

/**
 * @author dismer.ronda
 *
 */

public class PdfExportLabels extends PdfExport
{
	private static final long serialVersionUID = 476503138754478490L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( PdfExportLabels.class );
	
	private Shipment shipment;
	private String rows;
	private String cols;
	private String type;
	
	public PdfExportLabels( Shipment quotation, String rows, String cols, String type )
	{
		this.shipment = quotation;
		this.rows = rows;
		this.cols = cols;
		this.type = type;
	}

	public VelocityContext createVelocityContext() throws BaseException
	{
		VelocityContext vcontext = super.createVelocityContext();
		
		vcontext.put( "title", getContext().getString( "template.shipment.labels" ) );
		vcontext.put( "shipment", shipment );
		vcontext.put( "rows", rows );
		vcontext.put( "cols", cols );
		vcontext.put( "type", type );
		
		return vcontext;
	}
}
