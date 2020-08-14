package es.pryades.erp.reports;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.dto.Invoice;

/**
 * @author dismer.ronda
 *
 */

public class PdfExportInvoice extends PdfExport
{
	private static final long serialVersionUID = -489414953503790742L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( PdfExportInvoice.class );
	
	private Invoice invoice;
	
	public PdfExportInvoice( Invoice quotation )
	{
		this.invoice = quotation;
	}

	public VelocityContext createVelocityContext() throws BaseException
	{
		VelocityContext vcontext = super.createVelocityContext();
		
		vcontext.put( "title", getContext().getString( "template.invoice.invoice" ) );
		vcontext.put( "invoice", invoice );
		
		return vcontext;
	}
}
