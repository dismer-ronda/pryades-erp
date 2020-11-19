package es.pryades.erp.reports;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.Shipment;

/**
 * @author dismer.ronda
 *
 */

public class PdfExportNoDoubleUserLetter extends PdfExport
{
	private static final long serialVersionUID = 5491508926330656203L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( PdfExportNoDoubleUserLetter.class );
	
	private Shipment shipment;
	
	public PdfExportNoDoubleUserLetter( Shipment quotation )
	{
		this.shipment = quotation;
	}

	public VelocityContext createVelocityContext() throws BaseException
	{
		VelocityContext vcontext = super.createVelocityContext();
		
		vcontext.put( "title", getContext().getString( "template.ndbu.letter.title" ) );
		vcontext.put( "shipment", shipment );
		
		return vcontext;
	}
	
	public String getLetterBody()
	{
		return Utils.getStringAsHtml( 
				getContext().getString( "template.ndbu.letter.body" ).
					replaceAll( "%invoices%", shipment.getInvoices( getContext() ) ).
					replaceAll( "%consignee%", shipment.getConsignee().getName() ) ).
					replaceAll( "%date%", shipment.getFormattedDate() );
	}
}
