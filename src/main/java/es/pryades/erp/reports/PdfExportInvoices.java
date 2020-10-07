package es.pryades.erp.reports;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.dto.query.InvoiceQuery;

/**
 * @author dismer.ronda
 *
 */

public class PdfExportInvoices extends PdfExport
{
	private static final long serialVersionUID = -1900040213329248720L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( PdfExportInvoices.class );
	
	private List<Invoice> invoices;
	private InvoiceQuery query;
	
	public PdfExportInvoices( List<Invoice> invoices, InvoiceQuery query )
	{
		this.invoices = invoices;
		this.query = query;
	}

	public VelocityContext createVelocityContext() throws BaseException
	{
		VelocityContext vcontext = super.createVelocityContext();
		
		vcontext.put( "title", getContext().getString( "template.list.invoices" ) );
		vcontext.put( "invoices", invoices );
		vcontext.put( "query", query );
		
		return vcontext;
	}
	
	public double getGrandTotal()
	{
		double total = 0;
		
		for ( Invoice invoice : invoices )
			total += invoice.getGrandTotalInvoice();

		return Utils.roundDouble( total, 2 );
	}

	public String getGrandTotalAsString()
	{
		return Utils.getFormattedCurrency( getGrandTotal() );
	}

	public double getGrandTotalTaxes()
	{
		double total = 0;
		
		for ( Invoice invoice : invoices )
			total += invoice.getGrandTotalTaxes();

		return Utils.roundDouble( total, 2 );
	}

	public String getGrandTotalTaxesAsString()
	{
		return Utils.getFormattedCurrency( getGrandTotalTaxes() );
	}

	public double getGrandTotalAfterTaxes()
	{
		double total = 0;
		
		for ( Invoice invoice : invoices )
			total += invoice.getGrandTotalInvoiceAfterTaxes();

		return Utils.roundDouble( total, 2 );
	}
	
	public String getGrandTotalAfterTaxesAsString()
	{
		return Utils.getFormattedCurrency( getGrandTotalAfterTaxes() );
	}
}
