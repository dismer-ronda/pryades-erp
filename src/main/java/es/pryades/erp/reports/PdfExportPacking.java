package es.pryades.erp.reports;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.dto.Shipment;

/**
 * @author dismer.ronda
 *
 */

public class PdfExportPacking extends PdfExport
{
	private static final long serialVersionUID = -303445815995676299L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( PdfExportPacking.class );
	
	private Shipment shipment;
	
	public PdfExportPacking( Shipment quotation )
	{
		this.shipment = quotation;
	}

	public VelocityContext createVelocityContext() throws BaseException
	{
		VelocityContext vcontext = super.createVelocityContext();
		
		vcontext.put( "title", getContext().getString( "template.shipment.packing" ) );
		vcontext.put( "shipment", shipment );
		
		return vcontext;
	}
}
