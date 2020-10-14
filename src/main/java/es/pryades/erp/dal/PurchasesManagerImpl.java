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
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.ibatis.PurchaseMapper;
import es.pryades.erp.dto.Purchase;
import es.pryades.erp.dto.query.PurchaseQuery;
import es.pryades.erp.ioc.IOCManager;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class PurchasesManagerImpl extends BaseManagerImpl implements PurchasesManager
{
	private static final long serialVersionUID = -3882554937723336630L;
	
	private static final Logger LOG = Logger.getLogger( PurchasesManagerImpl.class );

	public static final String moneyFormat = "0.00 [$€-C0A];[RED]-0.00 [$€-C0A]";

	public static BaseManager build()
	{
		return new PurchasesManagerImpl();
	}

	public PurchasesManagerImpl()
	{
		super( PurchaseMapper.class, Purchase.class, LOG );
	}

	@Override
	public byte[] generateListZip( AppContext ctx, PurchaseQuery query ) throws Throwable
	{
    	@SuppressWarnings("unchecked")
		List<Purchase> purchases = IOCManager._PurchasesManager.getRows( ctx, query );
    	
    	String fileNames = "";
    	for ( Purchase purchase : purchases )
    	{
    		PurchaseQuery query1 = new PurchaseQuery();
    		query1.setId( purchase.getId() );
    		
    		Purchase purchase1 = (Purchase)IOCManager._PurchasesManager.getRow( ctx, query1);
    		
    		if ( purchase1.getInvoice() != null )
    		{
	    		String fileName = "/tmp/" + purchase1.getFormattedNumber() + ".pdf";
	    		
	    		Utils.writeBinaryFile( fileName, purchase1.getInvoice() );
	    		
	    		if ( !fileNames.isEmpty() )
	    			fileNames += " ";
	    		fileNames += fileName;
    		}
    	}
    	
    	Utils.cmdExec( "zip -Dj /tmp/purchases-list.zip " + fileNames );
    	
    	byte[] bytes = Utils.readBinaryFile( "/tmp/purchases-list.zip" );
    	
    	String parts[] = fileNames.split( " " );
    	for ( String part : parts )
    		Utils.DeleteFile( part );
    	
    	Utils.DeleteFile( "/tmp/purchases-list.zip" );
    	
    	return bytes;
	}

	@Override
	public boolean duplicatePurchase( AppContext ctx, Purchase src )
	{
		try
		{
			Purchase copy = new Purchase();
	
			copy.setPurchase_type( src.getPurchase_type() );
			copy.setTitle( src.getTitle() );
			copy.setDescription( src.getDescription() );
			copy.setPurchase_date( src.getPurchase_date() );
			copy.setRegister_date( src.getRegister_date() );
			copy.setNet_price( src.getNet_price() );
			copy.setNet_tax( src.getNet_tax() );
			copy.setNet_retention( src.getNet_retention() );
			copy.setPayed( 0.0 );
			copy.setStatus( Purchase.STATUS_CREATED );
			
			copy.setRef_operation( src.getRef_operation() ); 
			copy.setRef_buyer( src.getRef_buyer() ); 
			copy.setRef_provider( src.getRef_provider() ); 
			copy.setRef_contact( src.getRef_contact() ); 
	
			IOCManager._PurchasesManager.setRow( ctx, null, copy );
			
			return true;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return false;
	}
	
	private boolean hasRetentions( List<Purchase> purchases )
	{
		for ( Purchase purchase : purchases )
			if ( purchase.getNet_retention() != 0 )
				return true;

		return false;
	}

	@Override
	public byte[] exportListXls( AppContext ctx, PurchaseQuery query ) throws Throwable
	{
		query.setOrderby( "purchase_date" );
		query.setOrder( "desc" );

		@SuppressWarnings("resource")
		Workbook workbook = new XSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
		
		int i = 0;
		
		Row sheetRow = sheet.createRow( i++ );

		@SuppressWarnings("unchecked")
		List<Purchase> purchases = IOCManager._PurchasesManager.getRows( ctx, query );
		
		boolean hasRetentions = hasRetentions( purchases );
    	
		int j = 0;
		
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
        styleCurrency.setDataFormat( workbook.createDataFormat().getFormat( moneyFormat ) );

        CellStyle styleFooterCurrency = workbook.createCellStyle();
        Font fontFooterCurrency = workbook.createFont();
        fontFooterCurrency.setBoldweight( Font.BOLDWEIGHT_BOLD );
        styleFooterCurrency.setAlignment(CellStyle.ALIGN_RIGHT );
        styleFooterCurrency.setFont( fontFooter);
        styleFooterCurrency.setDataFormat( workbook.createDataFormat().getFormat( moneyFormat ) );

		Cell cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.purchases.date" ) );

		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.purchases.number" ) );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.purchases.title" ) );

		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.purchases.provider" ) );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.purchases.tax_id" ) );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.purchases.invoice_number" ) );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.purchases.total" ) );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.purchases.tax_rate" ) );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.purchases.taxes" ) );
		
		if ( hasRetentions )
		{
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.purchases.retention_rate" ) );
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleHeader);
			cell.setCellValue( ctx.getString( "template.list.purchases.retention" ) );
		}		

		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleHeader);
		cell.setCellValue( ctx.getString( "template.list.invoices.grand.total" ) );
		
		double totalBase = 0, totalTaxes = 0, totalRetentions = 0, totalGross = 0; 
    	for ( Purchase purchase : purchases )
    	{
			sheetRow = sheet.createRow( i++ );
			
			j = 0;
			cell = sheetRow.createCell( j++ );
			cell.setCellValue( purchase.getFormattedDate() );

			cell = sheetRow.createCell( j++ );
			cell.setCellValue( purchase.getFormattedNumber() );
			
			cell = sheetRow.createCell( j++ );
			cell.setCellValue( purchase.getTitle() );

			cell = sheetRow.createCell( j++ );
			cell.setCellValue( purchase.getProvider().getName() );
			
			cell = sheetRow.createCell( j++ );
			cell.setCellValue( purchase.getProvider().getTax_id() );
			
			cell = sheetRow.createCell( j++ );
			cell.setCellValue( purchase.getInvoice_number() );
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleCurrency);
			cell.setCellValue( purchase.getNet_price() );
			
			cell = sheetRow.createCell( j++ );
			cell.setCellValue( purchase.getTaxRate() );
			
			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleCurrency);
			cell.setCellValue( purchase.getNet_tax() );
			
			if ( hasRetentions )
			{
				cell = sheetRow.createCell( j++ );
				cell.setCellValue( purchase.getRetentionRate() );
				
				cell = sheetRow.createCell( j++ );
		        cell.setCellStyle(styleCurrency);
				cell.setCellValue( purchase.getNet_retention() );
			}		

			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleCurrency);
			cell.setCellValue( purchase.getGrossPrice() );
			
			totalBase += purchase.getNet_price();
			totalTaxes += purchase.getNet_tax();
			totalRetentions += purchase.getNet_retention();
			totalGross += purchase.getGrossPrice();
		}
		
		sheetRow = sheet.createRow( i++ );
		sheetRow = sheet.createRow( i++ );
		
		j = 0;
		cell = sheetRow.createCell( j++ );
		cell = sheetRow.createCell( j++ );
		cell = sheetRow.createCell( j++ );
		cell = sheetRow.createCell( j++ );
		cell = sheetRow.createCell( j++ );
		
		cell = sheetRow.createCell( j++);
        cell.setCellStyle(styleFooter);
		cell.setCellValue( ctx.getString( "template.list.purchases.totals" ) );

		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleFooterCurrency);
		cell.setCellValue( totalBase );
	
		cell = sheetRow.createCell( j++ );
		
		cell = sheetRow.createCell( j++ );
        cell.setCellStyle(styleFooterCurrency);
		cell.setCellValue( totalTaxes );
		
		if ( hasRetentions )
		{
			cell = sheetRow.createCell( j++ );

			cell = sheetRow.createCell( j++ );
	        cell.setCellStyle(styleFooterCurrency);
			cell.setCellValue( totalRetentions );
		}

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
