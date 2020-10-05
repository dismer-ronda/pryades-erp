package es.pryades.erp.reports;

import org.apache.velocity.VelocityContext;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.dto.Quotation;
import org.apache.log4j.Logger;

/**
 * @author dismer.ronda
 *
 */

public class PdfExportQuotation extends PdfExport
{
	private static final long serialVersionUID = -9013237500205307633L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( PdfExportQuotation.class );
	
	private Quotation quotation;
	
	public PdfExportQuotation( Quotation quotation )
	{
		this.quotation = quotation;
	}

	public VelocityContext createVelocityContext() throws BaseException
	{
		VelocityContext vcontext = super.createVelocityContext();
		
		vcontext.put( "title", getContext().getString( "reports.cylinders.title" ) );
		vcontext.put( "quotation", quotation );
		
		return vcontext;
	}
}
