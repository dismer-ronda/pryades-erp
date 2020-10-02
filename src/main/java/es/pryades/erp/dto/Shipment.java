package es.pryades.erp.dto;

import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.Utils;
import es.pryades.erp.common.XmlUtils;
import es.pryades.erp.dto.query.InvoiceQuery;
import es.pryades.erp.ioc.IOCManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class Shipment extends BaseDto
{
	private static final long serialVersionUID = 8747570593616523898L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( Shipment.class );
	
	public static final int STATUS_CREATED 		= 0;
	public static final int STATUS_SENT 		= 1;
	public static final int STATUS_TRANSIT		= 2;
	public static final int STATUS_DELIVERED	= 3;
	
	private Integer number;		
	
	private Long shipment_date;						
	private Long departure_date;						

	private String incoterms;

	private String title;
	private String description;
	
	private String departure_port;
	private String arrival_port;

	private String carrier;
	private String tracking;
	
	private Long ref_consignee;
	private Long ref_notify;
	private Long ref_transporter;

	private Integer status;

	private String consignee_name;
	private String consignee_address;
	private String consignee_tax_id;
	private String consignee_email;
	private String consignee_phone;
	private Boolean consignee_taxable;
	private String consignee_language;
	private Boolean consignee_signature;
	private String consignee_contact_person;

	private String notify_name;
	private String notify_address;
	private String notify_tax_id;
	private String notify_email;
	private String notify_phone;
	private Boolean notify_taxable;
	private String notify_language;
	private Boolean notify_signature;
	private String notify_contact_person;
	
	private List<ShipmentBox> boxes;

	public String getFormattedNumber() 
	{
		int year = number / 10000;
		int index = number % 10000;
		
		return Integer.toString( year ) + "-" + String.format("%04d", index );
	}
	
	public String getConsigneeAddressAsHtml()
	{
		return Utils.getStringAsHtml( Utils.getStringAsHtml( consignee_address ) );
	}
	
	public String getFormattedDate()
	{
		return CalendarUtils.getDateFromLongAsString( getShipment_date(), "dd-MM-yyyy" );
	}

	public String getFormattedDepartureDate()
	{
		return CalendarUtils.getDateFromLongAsString( getDeparture_date(), "dd-MM-yyyy" );
	}

	public Double getGrossWeight()
	{
		double weight = 0;
		
		for ( ShipmentBox box : boxes )
			weight += box.getGrossWeight();
		
		return weight;
	}

	public String getPackingTable( AppContext ctx )
	{
		ctx.addData( Integer.toString( ShipmentBox.TYPE_CONTAINER ), new Integer( 0 ) );
		ctx.addData( Integer.toString( ShipmentBox.TYPE_PALLET ), new Integer( 0 ) );
		ctx.addData( Integer.toString( ShipmentBox.TYPE_WOOD_BOX ), new Integer( 0 ) );
		ctx.addData( Integer.toString( ShipmentBox.TYPE_CARDBOARD_BOX), new Integer( 0 ) );
		ctx.addData( Integer.toString( ShipmentBox.TYPE_BULK), new Integer( 0 ) );

		String headers = "";
		
		String invoices = XmlUtils.getBlockDiv( 
				XmlUtils.getDiv( "padding_all text_regular_size text_bold text_left vtop", ctx.getString( "template.shipment.packing.invoices" ) + ":" ) +
				XmlUtils.getDiv( "padding_all text_regular_size", getInvoices( ctx ) ) );
		String orders = XmlUtils.getBlockDiv( 
				XmlUtils.getDiv( "padding_all text_regular_size text_bold text_left vtop", ctx.getString( "template.shipment.packing.orders" ) + ":" ) +
				XmlUtils.getDiv( "padding_all text_regular_size", getOrders( ctx ) ) );


		headers += XmlUtils.getTableCol( "", "borde", invoices );
		headers += XmlUtils.getTableCol( "", "borde", orders  );
		headers += XmlUtils.getTableCol( "48px", "text_regular_size text_center borde", XmlUtils.getDiv( "padding_bottom padding_top text_bold", ctx.getString( "template.shipment.packing.net" ) ) );
		headers += XmlUtils.getTableCol( "48px", "text_regular_size text_center borde", XmlUtils.getDiv( "padding_bottom padding_top text_bold", ctx.getString( "template.shipment.packing.gross" ) ) );
		headers += XmlUtils.getTableCol( "44px", "text_regular_size text_center borde", XmlUtils.getDiv( "padding_bottom padding_top text_bold", ctx.getString( "template.shipment.packing.quantity" ) ) );
		headers += XmlUtils.getTableCol( "44px", "text_regular_size text_center borde", XmlUtils.getDiv( "padding_bottom padding_top text_bold", ctx.getString( "template.shipment.packing.length" ) ) );
		headers += XmlUtils.getTableCol( "44px", "text_regular_size text_center borde", XmlUtils.getDiv( "padding_bottom padding_top text_bold", ctx.getString( "template.shipment.packing.width" ) ) );
		headers += XmlUtils.getTableCol( "44px", "text_regular_size text_center borde", XmlUtils.getDiv( "padding_bottom padding_top text_bold", ctx.getString( "template.shipment.packing.height" ) ) );
		headers += XmlUtils.getTableCol( "44px", "text_regular_size text_center borde", XmlUtils.getDiv( "padding_bottom padding_top text_bold", ctx.getString( "template.shipment.packing.weight" ) ) );

		String rows = "";
		for ( ShipmentBox box : boxes )
		{
			Integer count = (Integer)ctx.getData( box.getBox_type().toString() );
			count++;

			String type = XmlUtils.getDiv( "", 
					XmlUtils.getDiv( "padding_left padding_right padding_top text_regular_size text_center text_bold", ctx.getString( "shipment.box.type.alias." + box.getBox_type() ) ) + 
					XmlUtils.getDiv( "padding_left padding_right padding_bottom text_regular_size text_center text_bold", count.toString() ) );

			String row = "";
			row += XmlUtils.getTableCol( "48px", "borde ", type );
			row += XmlUtils.getTableCol( "", 	 "text_regular_size borde", box.getBoxContents( ctx ) );
			row += XmlUtils.getTableCol( "44px", "text_regular_size text_center borde", box.getLength().toString() );
			row += XmlUtils.getTableCol( "44px", "text_regular_size text_center borde", box.getWidth().toString() );
			row += XmlUtils.getTableCol( "44px", "text_regular_size text_center borde", box.getHeight().toString() );
			row += XmlUtils.getTableCol( "44px", "text_regular_size text_center borde", Double.toString( Utils.roundDouble( box.getGrossWeight(), 1 ) ) );

			ctx.addData( box.getBox_type().toString(), count );
			
			rows += XmlUtils.getTableRow( "", row );
		}
		
		String footers = "";
		footers += XmlUtils.getTableCol( "", "text_regular_size text_center ", "" );
		footers += XmlUtils.getTableCol( "", "", "" );
		footers += XmlUtils.getTableCol( "", "", "" );
		footers += XmlUtils.getTableCol( "", "", "" );
		footers += XmlUtils.getTableCol( "", "", "" );
		footers += XmlUtils.getTableCol( "128px", "", XmlUtils.getDiv( "text_regular_size text_right padding_v text_bold", ctx.getString( "template.shipment.packing.total.gross" ) + ":" ) );
		footers += XmlUtils.getTableCol( "44px", "", XmlUtils.getDiv( "text_regular_size text_center padding_v text_bold", Double.toString( Utils.roundDouble( getGrossWeight(), 1 ) ) ) );
		
		String ret = XmlUtils.getTable( "100%", "border_no_spacing", XmlUtils.getTableRow( "", headers ) ) + XmlUtils.getTable( "100%", "border_no_spacing", rows ) + XmlUtils.getTable( "100%", "border_no_spacing", XmlUtils.getTableRow( "", footers ) );
		
		//ret += XmlUtils.newline();
		
		String leyends = "";
		if ( (Integer)ctx.getData( Integer.toString( ShipmentBox.TYPE_CONTAINER ) ) > 0 )
		{
			String tmp = XmlUtils.getBlockDiv( 
					XmlUtils.getDiv( "padding_all text_regular_size text_bold text_left vtop", ctx.getString( "shipment.box.type.alias." + ShipmentBox.TYPE_CONTAINER ) + ":" ) +
					XmlUtils.getDiv( "padding_all text_regular_size", ctx.getString( "shipment.box.type." + ShipmentBox.TYPE_CONTAINER ) ) );

			leyends += XmlUtils.getTableCol( "", "borde", tmp );

			//leyends += XmlUtils.getTableCol( "48px", "borde", XmlUtils.getDiv( CLASS_DIV_LEYEND_LEFT, ctx.getString( "shipment.box.type.alias." + ShipmentBox.TYPE_CONTAINER ) + ":" ) );
			//leyends += XmlUtils.getTableCol( "", "", XmlUtils.getDiv(  CLASS_DIV_LEYEND_RIGHT, ctx.getString( "shipment.box.type." + ShipmentBox.TYPE_CONTAINER ) ) );
		}

		if ( (Integer)ctx.getData( Integer.toString( ShipmentBox.TYPE_PALLET ) ) > 0 )
		{
			String tmp = XmlUtils.getBlockDiv( 
					XmlUtils.getDiv( "padding_all text_regular_size text_bold text_left vtop", ctx.getString( "shipment.box.type.alias." + ShipmentBox.TYPE_PALLET ) + ":" ) +
					XmlUtils.getDiv( "padding_all text_regular_size", ctx.getString( "shipment.box.type." + ShipmentBox.TYPE_PALLET ) ) );

			leyends += XmlUtils.getTableCol( "", "borde", tmp );

			/*leyends += XmlUtils.getTableCol( "48px", "borde", XmlUtils.getDiv(  CLASS_DIV_LEYEND_LEFT, ctx.getString( "shipment.box.type.alias." + ShipmentBox.TYPE_PALLET ) + ":" ) );
			leyends += XmlUtils.getTableCol( "", "", XmlUtils.getDiv(  CLASS_DIV_LEYEND_RIGHT, ctx.getString( "shipment.box.type." + ShipmentBox.TYPE_PALLET ) ) );*/
		}

		if ( (Integer)ctx.getData( Integer.toString( ShipmentBox.TYPE_WOOD_BOX ) ) > 0 )
		{
			String tmp = XmlUtils.getBlockDiv( 
					XmlUtils.getDiv( "padding_all text_regular_size text_bold text_left vtop", ctx.getString( "shipment.box.type.alias." + ShipmentBox.TYPE_WOOD_BOX ) + ":" ) +
					XmlUtils.getDiv( "padding_all text_regular_size", ctx.getString( "shipment.box.type." + ShipmentBox.TYPE_WOOD_BOX ) ) );

			leyends += XmlUtils.getTableCol( "", "borde", tmp );

			//leyends += XmlUtils.getTableCol( "48px", "borde", XmlUtils.getDiv(  CLASS_DIV_LEYEND_LEFT, ctx.getString( "shipment.box.type.alias." + ShipmentBox.TYPE_WOOD_BOX ) + ":" ) );
			//leyends += XmlUtils.getTableCol( "", "", XmlUtils.getDiv(  CLASS_DIV_LEYEND_RIGHT, ctx.getString( "shipment.box.type." + ShipmentBox.TYPE_WOOD_BOX ) ) );
		}

		if ( (Integer)ctx.getData( Integer.toString( ShipmentBox.TYPE_CARDBOARD_BOX ) ) > 0 )
		{
			String tmp = XmlUtils.getBlockDiv( 
					XmlUtils.getDiv( "padding_all text_regular_size text_bold text_left vtop", ctx.getString( "shipment.box.type.alias." + ShipmentBox.TYPE_CARDBOARD_BOX ) + ":" ) +
					XmlUtils.getDiv( "padding_all text_regular_size", ctx.getString( "shipment.box.type." + ShipmentBox.TYPE_CARDBOARD_BOX ) ) );

			leyends += XmlUtils.getTableCol( "", "borde", tmp );
			
			/*leyends += XmlUtils.getTableCol( "48px", "borde", XmlUtils.getDiv(  CLASS_DIV_LEYEND_LEFT, ctx.getString( "shipment.box.type.alias." + ShipmentBox.TYPE_CARDBOARD_BOX ) + ":" ) );
			leyends += XmlUtils.getTableCol( "", "" , XmlUtils.getDiv(  CLASS_DIV_LEYEND_RIGHT, ctx.getString( "shipment.box.type." + ShipmentBox.TYPE_CARDBOARD_BOX ) ) );*/
		}

		if ( (Integer)ctx.getData( Integer.toString( ShipmentBox.TYPE_BULK ) ) > 0 )
		{
			String tmp = XmlUtils.getBlockDiv( 
					XmlUtils.getDiv( "padding_all text_regular_size text_bold text_left vtop", ctx.getString( "shipment.box.type.alias." + ShipmentBox.TYPE_BULK ) + ":" ) +
					XmlUtils.getDiv( "padding_all text_regular_size", ctx.getString( "shipment.box.type." + ShipmentBox.TYPE_BULK ) ) );

			leyends += XmlUtils.getTableCol( "", "borde", tmp );

			//leyends += XmlUtils.getTableCol( "48px", "borde", XmlUtils.getDiv(  CLASS_DIV_LEYEND_LEFT, ctx.getString( "shipment.box.type.alias." + ShipmentBox.TYPE_BULK ) + ":" ) );
			//leyends += XmlUtils.getTableCol( "", "" , XmlUtils.getDiv(  CLASS_DIV_LEYEND_RIGHT, ctx.getString( "shipment.box.type." + ShipmentBox.TYPE_BULK ) ) );
		}

		ret += XmlUtils.getTable( "100%", "border_no_spacing",
				XmlUtils.getTableRow( "", 
						XmlUtils.getTableCol( "20%", "padding_all", ctx.getString( "template.shipment.packing.leyend" ) ) +
						XmlUtils.getTableCol( "20%", "", "" ) +
						XmlUtils.getTableCol( "20%", "", "" ) +
						XmlUtils.getTableCol( "20%", "", "" ) +
						XmlUtils.getTableCol( "20%", "", "" )
				) +
				XmlUtils.getTableRow( "", leyends ) );

		return ret;
	}
	
	public String getInvoices( AppContext ctx )
	{
		try
		{
			InvoiceQuery query = new InvoiceQuery();
			query.setRef_shipment( getId() );
			
			@SuppressWarnings("unchecked")
			List <Invoice> invoices = IOCManager._InvoicesManager.getRows( ctx, query );
			
			String ret = "";
			for ( Invoice invoice : invoices )
			{
				if ( !ret.isEmpty() )
					ret += ", ";
				ret += invoice.getFormattedNumber();
			}
			
			return Utils.getStringAsHtml( ret );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
		
		return "";
	}

	public String getOrders( AppContext ctx )
	{
		try
		{
			InvoiceQuery query = new InvoiceQuery();
			query.setRef_shipment( getId() );
			
			@SuppressWarnings("unchecked")
			List <Invoice> invoices = IOCManager._InvoicesManager.getRows( ctx, query );
			
			String ret = "";
			for ( Invoice invoice : invoices )
			{
				if ( !ret.isEmpty() )
					ret += ", ";
				ret += invoice.getQuotation().getReference_order();
			}
			
			return Utils.getStringAsHtml( ret );
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
		
		return "";
	}

	public String getCustomerDataAsHtml( AppContext ctx )
	{
		String data = getConsignee_contact_person() + "\n" + 
				getConsignee_name() + "\n" +
				ctx.getString( "template.common.tax_id" ) + ": " + getConsignee_tax_id() + "\n" + 
				getConsignee_address() + "\n" +
				ctx.getString( "template.common.phone" ) + ": " + getConsignee_phone() + "\n" + 
				ctx.getString( "template.common.email" ) + ": " + getConsignee_email();
		
		return Utils.getStringAsHtml( StringEscapeUtils.escapeXml( data ) );
	}
}
 