package es.pryades.erp.dal;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.ibatis.InvoiceMapper;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.dto.query.InvoiceQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.reports.PdfExportInvoice;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class InvoicesManagerImpl extends BaseManagerImpl implements InvoicesManager
{
	private static final long serialVersionUID = 7316344102356022841L;
	
	private static final Logger LOG = Logger.getLogger( InvoicesManagerImpl.class );

	public static BaseManager build()
	{
		return new InvoicesManagerImpl();
	}

	public InvoicesManagerImpl()
	{
		super( InvoiceMapper.class, Invoice.class, LOG );
	}

	@Override
	public boolean setEmptyToNull()
	{
		return false;
	}

	@Override
	public byte[] generatePdf( AppContext ctx, Invoice invoice ) throws Throwable
	{
    	String template = invoice.getQuotation().getCustomer().getTaxable().booleanValue() ? "national-invoice-template" : "international-invoice-template";
		
		PdfExportInvoice export = new PdfExportInvoice( invoice );
		
		export.setOrientation( "portrait" );
		export.setPagesize( "A4" );
		export.setTemplate( template );
	
		AppContext ctx1 = new AppContext( invoice.getQuotation().getCustomer().getLanguage() );
		IOCManager._ParametersManager.loadParameters( ctx1 );
		ctx1.setUser( ctx.getUser() );
		ctx1.addData( "Url", ctx.getData( "Url" ) );
    	ctx1.loadOwnerCompany();

		export.setContext( ctx1 );

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		export.doExport( os );
		
		return os.toByteArray();
	}

	@Override
	public byte[] generateListZip( AppContext ctx, InvoiceQuery query ) throws Throwable
	{
    	@SuppressWarnings("unchecked")
		List<Invoice> invoices = IOCManager._InvoicesManager.getRows( ctx, query );
    	
    	String fileNames = "";
    	for ( Invoice invoice : invoices )
    	{
    		String fileName = "/tmp/" + invoice.getFormattedNumber() + ".pdf";
    		
    		byte[] bytes = generatePdf( ctx, invoice );
    		
    		Utils.writeBinaryFile( fileName, bytes );
    		
    		if ( !fileNames.isEmpty() )
    			fileNames += " ";
    		fileNames += fileName;
    	}
    	
    	Utils.cmdExec( "zip -Dj /tmp/invoices-list.zip " + fileNames );
    	
    	byte[] bytes = Utils.readBinaryFile( "/tmp/invoices-list.zip" );
    	
    	String parts[] = fileNames.split( " " );
    	for ( String part : parts )
    		Utils.DeleteFile( part );
    	
    	Utils.DeleteFile( "/tmp/invoices-list.zip" );
    	
    	return bytes;
	}
}
