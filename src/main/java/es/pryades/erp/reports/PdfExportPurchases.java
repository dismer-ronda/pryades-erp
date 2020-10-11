package es.pryades.erp.reports;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.Purchase;
import es.pryades.erp.dto.query.PurchaseQuery;

/**
 * @author dismer.ronda
 *
 */

public class PdfExportPurchases extends PdfExport
{
	private static final long serialVersionUID = -2063352414768739467L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( PdfExportPurchases.class );
	
	private List<Purchase> purchases;
	private PurchaseQuery query;
	
	public PdfExportPurchases( List<Purchase> invoices, PurchaseQuery query )
	{
		this.purchases = invoices;
		this.query = query;
	}

	public VelocityContext createVelocityContext() throws BaseException
	{
		VelocityContext vcontext = super.createVelocityContext();
		
		vcontext.put( "title", getContext().getString( "template.list.purchases" ) );
		vcontext.put( "purchases", purchases );
		vcontext.put( "query", query );
		
		return vcontext;
	}
	
	public double getGrandTotal()
	{
		double total = 0;
		
		for ( Purchase invoice : purchases )
			total += invoice.getNet_price();

		return Utils.roundDouble( total, 2 );
	}

	public String getGrandTotalAsString()
	{
		return Utils.getFormattedCurrency( getGrandTotal() );
	}

	public double getGrandTotalTaxes()
	{
		double total = 0;
		
		for ( Purchase invoice : purchases )
			total += invoice.getNet_tax();

		return Utils.roundDouble( total, 2 );
	}

	public String getGrandTotalTaxesAsString()
	{
		return Utils.getFormattedCurrency( getGrandTotalTaxes() );
	}

	public double getGrandTotalAfterTaxes()
	{
		double total = 0;
		
		for ( Purchase invoice : purchases )
			total += invoice.getGrossPrice();

		return Utils.roundDouble( total, 2 );
	}
	
	public String getGrandTotalAfterTaxesAsString()
	{
		return Utils.getFormattedCurrency( getGrandTotalAfterTaxes() );
	}
}
