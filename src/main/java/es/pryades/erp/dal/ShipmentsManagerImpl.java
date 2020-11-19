package es.pryades.erp.dal;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Attachment;
import es.pryades.erp.dal.ibatis.ShipmentMapper;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.dto.Shipment;
import es.pryades.erp.dto.ShipmentAttachment;
import es.pryades.erp.dto.query.InvoiceQuery;
import es.pryades.erp.dto.query.ShipmentQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.reports.PdfExportPacking;
import es.pryades.erp.reports.PdfExportNoDoubleUserLetter;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class ShipmentsManagerImpl extends BaseManagerImpl implements ShipmentsManager
{
	private static final long serialVersionUID = 6036350353726846646L;

	private static final Logger LOG = Logger.getLogger( ShipmentsManagerImpl.class );

	public static BaseManager build()
	{
		return new ShipmentsManagerImpl();
	}

	public ShipmentsManagerImpl()
	{
		super( ShipmentMapper.class, Shipment.class, LOG );
	}

	@SuppressWarnings("unchecked")
	private List<Invoice> getShipmentInvoices( AppContext ctx, Shipment shipment ) throws Throwable
	{
		InvoiceQuery queryInvoice = new InvoiceQuery();
		queryInvoice.setRef_shipment( shipment.getId() );
		
		return IOCManager._InvoicesManager.getRows( ctx, queryInvoice );
	}

	@Override
	public byte[] generatePacking( AppContext ctx, Shipment org ) throws Throwable
	{
		ShipmentQuery queryQuotation = new ShipmentQuery();
		queryQuotation.setId( org.getId() );
		Shipment shipment = (Shipment)IOCManager._ShipmentsManager.getRow( ctx, queryQuotation );
	
		String template = shipment.getConsignee().getTaxable().booleanValue() ? "national-packing-template" : "international-packing-template";
		
		PdfExportPacking export = new PdfExportPacking( shipment );
		
		export.setOrientation( "portrait" );
		export.setPagesize( "A4" );
		export.setTemplate( template );
		export.setContext( ctx );
	
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		export.doExport( os );
		
		return os.toByteArray();
	}

	@Override
	public byte[] generateNoDoubleUseLetter( AppContext ctx, Shipment org ) throws Throwable
	{
		ShipmentQuery queryQuotation = new ShipmentQuery();
		queryQuotation.setId( org.getId() );
		Shipment shipment = (Shipment)IOCManager._ShipmentsManager.getRow( ctx, queryQuotation );
	
		String template = "no-double-use-letter-template";
		
		PdfExportNoDoubleUserLetter export = new PdfExportNoDoubleUserLetter( shipment );
		
		export.setOrientation( "portrait" );
		export.setPagesize( "A4" );
		export.setTemplate( template );
		export.setContext( ctx );
	
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		export.doExport( os );
		
		return os.toByteArray();
	}

	@Override
	public byte[] generateInstructions( AppContext ctx, Shipment org ) throws Throwable
	{
		ShipmentQuery queryQuotation = new ShipmentQuery();
		queryQuotation.setId( org.getId() );
		Shipment shipment = (Shipment)IOCManager._ShipmentsManager.getRow( ctx, queryQuotation );
	
		String template = shipment.getConsignee().getTaxable().booleanValue() ? "national-packing-template" : "international-packing-template";
		
		PdfExportPacking export = new PdfExportPacking( shipment );
		
		export.setOrientation( "portrait" );
		export.setPagesize( "A4" );
		export.setTemplate( template );
		export.setContext( ctx );
	
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		export.doExport( os );
		
		return os.toByteArray();
	}

	@Override
	public void getShipmentNotificationDocuments( AppContext ctx, Shipment shipment, List<Attachment> attachments ) throws Throwable
	{
		attachments.add( new Attachment( "PKL-" + shipment.getFormattedNumber() + ".pdf", "application/pdf", generatePacking( ctx, shipment ) ) );

		List<Invoice> invoices = getShipmentInvoices( ctx, shipment );
		
		for ( Invoice invoice : invoices )
		{
			byte pdf[] = IOCManager._InvoicesManager.generatePdf( ctx, invoice );
			
			attachments.add(  new Attachment( "INV-" + invoice.getFormattedNumber() + ".pdf", "application/pdf", pdf ) );
		}
		
		for ( ShipmentAttachment attachment : shipment.getAttachments() )
		{
			ShipmentAttachment query = new ShipmentAttachment();
			query.setId( attachment.getId() );
			
			ShipmentAttachment att = (ShipmentAttachment)IOCManager._ShipmentsAttachmentsManager.getRow( ctx, query );
			
			attachments.add( new Attachment( "ATT-" + att.getTitle() + ".pdf", "application/pdf", att.getData() ) );
		}
	}

	@Override
	public void getShipmentRequestDocuments( AppContext ctx, Shipment shipment, List<Attachment> attachments ) throws Throwable
	{
		attachments.add( new Attachment( "PKL-" + shipment.getFormattedNumber() + ".pdf", "application/pdf", generatePacking( ctx, shipment ) ) );
		attachments.add( new Attachment( "NDU-" + shipment.getFormattedNumber() + ".pdf", "application/pdf", generateNoDoubleUseLetter( ctx, shipment ) ) );
//		attachments.add( new Attachment( "INS-" + shipment.getFormattedNumber() + ".df", "application/pdf", generateInstructions( ctx, shipment ) ) );
		
		List<Invoice> invoices = getShipmentInvoices( ctx, shipment );
		
		for ( Invoice invoice : invoices )
		{
			byte pdf[] = IOCManager._InvoicesManager.generatePdf( ctx, invoice );
			
			attachments.add( new Attachment( "INV-" + invoice.getFormattedNumber() + ".pdf", "application/pdf", pdf ) );
		}
		
		for ( ShipmentAttachment attachment : shipment.getAttachments() )
		{
			ShipmentAttachment query = new ShipmentAttachment();
			query.setId( attachment.getId() );
			
			ShipmentAttachment att = (ShipmentAttachment)IOCManager._ShipmentsAttachmentsManager.getRow( ctx, query );
			
			attachments.add( new Attachment( "ATT-" + att.getTitle() + ".pdf", "application/pdf", att.getData() ) );
		}
	}

}
