package es.pryades.erp.dal;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Constants;
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
		query.setOrderby( "invoice_date" );
		query.setOrder( "desc" );

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
	
	@Override
	public byte[] exportListXls( AppContext ctx, InvoiceQuery query ) throws Throwable
	{
		query.setOrderby( "invoice_date" );
		query.setOrder( "desc" );

		@SuppressWarnings("resource")
		Workbook workbook = new XSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
		
		int i = 0;
		
		Row sheetRow = sheet.createRow( i++ );
		
        CellStyle styleHeader = workbook.createCellStyle();
        Font fontHeader = workbook.createFont();
        fontHeader.setBoldweight( Font.BOLDWEIGHT_BOLD );
        styleHeader.setAlignment(CellStyle.ALIGN_CENTER );
        styleHeader.setFont( fontHeader );

        CellStyle styleFooter = workbook.createCellStyle();
        Font fontFooter = workbook.createFont();
        fontFooter.setBoldweight( Font.BOLDWEIGHT_BOLD );
        styleFooter.setAlignment(CellStyle.ALIGN_RIGHT );
        styleFooter.setFont( fontFooter);
        
        CellStyle styleCurrency = workbook.createCellStyle();
        Font fontCurrency = workbook.createFont();
        styleCurrency.setAlignment(CellStyle.ALIGN_RIGHT );
        styleCurrency.setFont( fontCurrency );
        styleCurrency.setDataFormat( workbook.createDataFormat().getFormat( Constants.moneyFormat ) );

        CellStyle styleFooterCurrency = workbook.createCellStyle();
        Font fontFooterCurrency = workbook.createFont();
        fontFooterCurrency.setBoldweight( Font.BOLDWEIGHT_BOLD );
        styleFooterCurrency.setAlignment(CellStyle.ALIGN_RIGHT );
        styleFooterCurrency.setFont( fontFooter);
        styleFooterCurrency.setDataFormat( workbook.createDataFormat().getFormat( Constants.moneyFormat ) );
        
		int j = 0;
        Cell cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.invoices.date" ) );

		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.invoices.invoice_number" ) );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.invoices.title" ) );

		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.invoices.customer" ) );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.invoices.tax_id" ) );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.invoices.total" ) );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.invoices.tax_rate" ) );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.invoices.taxes" ) );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.invoices.grand.total" ) );
		
		@SuppressWarnings("unchecked")
		List<Invoice> invoices = IOCManager._InvoicesManager.getRows( ctx, query );
    	
		double totalBase = 0, totalTaxes = 0, totalGross = 0; 
    	for ( Invoice invoice : invoices )
    	{
			sheetRow = sheet.createRow( i++ );
			
			j = 0;
			cell = sheetRow.createCell( j++ );
			cell.setCellValue( invoice.getFormattedDate() );

			cell = sheetRow.createCell( j++ );
			cell.setCellValue( invoice.getFormattedNumber() );
			
			cell = sheetRow.createCell( j++ );
			cell.setCellValue( invoice.getTitle() );

			cell = sheetRow.createCell( j++ );
			cell.setCellValue( invoice.getQuotation().getCustomer().getName() );
			
			cell = sheetRow.createCell( j++ );
			cell.setCellValue( invoice.getQuotation().getCustomer().getTax_id() );
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleCurrency);
			cell.setCellValue( invoice.getGrandTotalInvoice() );
			
			cell = sheetRow.createCell( j++ );
			cell.setCellValue( invoice.getTaxRate() );
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleCurrency);
			cell.setCellValue( invoice.getTotalTaxes() );
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleCurrency);
			cell.setCellValue( invoice.getGrandTotalInvoiceAfterTaxes() );
			
			totalBase += invoice.getGrandTotalInvoice();
			totalTaxes += invoice.getTotalTaxes();
			totalGross += invoice.getGrandTotalInvoiceAfterTaxes();
		}
		
		sheetRow = sheet.createRow( i++ );
		sheetRow = sheet.createRow( i++ );
		
		j = 0;
		cell = sheetRow.createCell( j++ );
		cell = sheetRow.createCell( j++ );
		cell = sheetRow.createCell( j++ );
		cell = sheetRow.createCell( j++ );
		
		cell = sheetRow.createCell( j++);
        cell.setCellStyle(styleFooter);
		cell.setCellValue( ctx.getString( "template.list.invoices.totals" ) );

		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleFooterCurrency);
		cell.setCellValue( totalBase );
	
		cell = sheetRow.createCell( j++ );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleFooterCurrency);
		cell.setCellValue( totalTaxes );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleFooterCurrency);
		cell.setCellValue( totalGross );

		for ( int col = 0; col < j; col++ )
			sheet.autoSizeColumn( col );
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		workbook.write( os );
		
		return os.toByteArray();
	}
	
}
