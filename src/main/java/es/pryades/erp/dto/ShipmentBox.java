package es.pryades.erp.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.XmlUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class ShipmentBox extends BaseDto
{
	private static final long serialVersionUID = -6714043338893000224L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ShipmentBox.class );
	
	public static final int TYPE_CONTAINER			= 1;
	public static final int TYPE_PALLET				= 2;
	public static final int TYPE_WOOD_BOX			= 3;
	public static final int TYPE_CARDBOARD_BOX		= 4;
	public static final int TYPE_BULK				= 5;

	public static final int LABEL_ALL				= -1;
	public static final int LABEL_NONE 				= 0;
	public static final int LABEL_DETAIL			= 1;
	public static final int LABEL_SIMPLE			= 2;

	private Integer box_type;		
	private String label;		
	private Integer label_type;		
	
	private Long ref_shipment;						
	private Long ref_container;						

	private Double width; 
	private Double length; 
	private Double height;
	
	private List<ShipmentBox> sub_boxes;
	private List<ShipmentBoxLine> lines;
	
	public Double getGrossWeight()
	{
		double weight = 0;
		
		for ( ShipmentBox box : sub_boxes )
			weight += box.getGrossWeight();
		
		for ( ShipmentBoxLine line : lines )
			weight += line.getGross_weight();
		
		return weight;
	}
	
	public Double getNetWeight()
	{
		return 0.0;
	}
	
	public String getBoxContents( AppContext ctx )
	{
		try
		{
			String rows = "";
			for ( ShipmentBox box : sub_boxes )
			{
				Integer count = (Integer)ctx.getData( box.getBox_type().toString() );
				count++;
			
				String type = XmlUtils.getDiv( "", 
						XmlUtils.getDiv( "padding_left padding_right padding_top text_regular_size text_center text_bold", ctx.getString( "shipment.box.type.alias." + box.getBox_type() ) ) + 
						XmlUtils.getDiv( "padding_left padding_right padding_bottom text_regular_size text_center text_bold", count.toString() ) );
	
				String row = "";
				row += XmlUtils.getTableCol( "48px", "borde",  type  );
				row += XmlUtils.getTableCol( "", "text_regular_size borde", box.getBoxContents( ctx ) );
	
				ctx.addData( box.getBox_type().toString(), count );
				
				rows += XmlUtils.getTableRow( "", row );
			}
			
			if ( lines.size() > 0 )
			{
				for ( ShipmentBoxLine line : lines )
					rows += XmlUtils.getTableRow( "", line.getLineContents( ctx ) );
			}
	
			String ret = XmlUtils.getTable( "100%", "border_no_spacing", rows );
			
			return ret;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}

		return "";
	}

	public String getBoxTable( AppContext ctx )
	{
		try
		{
			String headers = "";
			
			headers += XmlUtils.getTableCol( "", "", "" );
			headers += XmlUtils.getTableCol( "48px", "text_regular_size text_center borde", XmlUtils.getDiv( "padding_bottom padding_top text_bold", ctx.getString( "template.shipment.packing.net" ) ) );
			headers += XmlUtils.getTableCol( "48px", "text_regular_size text_center borde", XmlUtils.getDiv( "padding_bottom padding_top text_bold", ctx.getString( "template.shipment.packing.gross" ) ) );
			headers += XmlUtils.getTableCol( "44px", "text_regular_size text_center borde", XmlUtils.getDiv( "padding_bottom padding_top text_bold", ctx.getString( "template.shipment.packing.quantity" ) ) );

			String rows = "";
			for ( ShipmentBox box : sub_boxes )
			{
				String type = XmlUtils.getDiv( "", 
						XmlUtils.getDiv( "padding_left padding_right padding_top text_regular_size text_center text_bold", ctx.getString( "shipment.box.type.alias." + box.getBox_type() ) ) + 
						XmlUtils.getDiv( "padding_left padding_right padding_bottom text_regular_size text_center text_bold", "" ) );
	
				String row = "";
				row += XmlUtils.getTableCol( "48px", "borde",  type  );
				row += XmlUtils.getTableCol( "", "text_regular_size borde", box.getBoxContents( ctx ) );
	
				rows += XmlUtils.getTableRow( "", row );
			}
			
			if ( lines.size() > 0 )
			{
				for ( ShipmentBoxLine line : lines )
					rows += XmlUtils.getTableRow( "", line.getLineContents( ctx ) );
			}
	
			String ret = XmlUtils.getTable( "100%", "border_no_spacing", XmlUtils.getTableRow( "", headers ) ) + XmlUtils.getTable( "100%", "border_no_spacing", rows );
	
			return ret;
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}

		return "";
	}

	@Override
	public void removePrivateFields()	
	{
		super.removePrivateFields();
		
		for ( ShipmentBox box : sub_boxes )
			box.removePrivateFields();
		
		for ( ShipmentBoxLine line : lines )
			line.removePrivateFields();
	}

	public boolean findBoxNumber( AppContext ctx, ShipmentBox box )
	{
		ctx.addData( Integer.toString( box.getBox_type() ), new Integer( 0 ) );
		
		for ( ShipmentBox box1 : sub_boxes )
		{
			if ( box1.getBox_type().equals( box.getBox_type() ) )
				ctx.addData( box.getBox_type().toString(), (Integer)ctx.getData( box1.getBox_type().toString() ) + 1 );

			if ( box1.getId().equals( box.getId() ) )
				return true;
			
			for ( ShipmentBox sub_box : box1.getSub_boxes() )
			{
				if ( sub_box.findBoxNumber( ctx, box ) )
					return true;
			}
		}
		
		return false;
	}

	public void countBoxes( AppContext ctx, HashMap<Integer, Integer> totals )
	{
		totals.put( getBox_type(), totals.get( getBox_type() ) + 1 );
		
		for ( ShipmentBox box : sub_boxes )
			box.countBoxes( ctx, totals );
	}

	public void generateDetailedLabels( AppContext ctx, Shipment shipment, ArrayList<String> labels, HashMap<Integer, Integer> totals, HashMap<Integer, Integer> count, int copies, int colWidth )
	{	
		count.put( getBox_type(), count.get( getBox_type() ) + 1 );

		if ( getLabel_type().equals( LABEL_DETAIL ) )
		{
			String logo 		= XmlUtils.getDiv( "", "<img src=\"" + ctx.getLogoUrl() + "\" width=\"" + colWidth / 2  + "mm\"> </img>" ) ;
			String from 		= XmlUtils.getDiv( "text_left indent", ctx.getString( "words.from" ) + ": " + ctx.getOwner().getName() );
			String to 			= XmlUtils.getDiv( "text_left indent", ctx.getString( "words.to" ) + ": " + shipment.getConsignee().getName() );
			String notify 		= XmlUtils.getDiv( "text_left indent", ctx.getString( "template.shipment.labels.notify" ) + ": " + shipment.getNotify_contact().getName() );
			String phone 		= XmlUtils.getDiv( "text_left indent", ctx.getString( "template.shipment.labels.phone" ) + ": " + shipment.getNotify_contact().getPhone() );
			String references 	= XmlUtils.getDiv( "text_left indent", ctx.getString( "template.shipment.labels.reference" ) + ": " + shipment.getOrders( ctx ) );
			String weight 		= XmlUtils.getDiv( "text_left indent", ctx.getString( "template.shipment.labels.weight" ) + ":" );
			String number 		= XmlUtils.getDiv( "", ctx.getString( "shipment.box.type." + getBox_type() ) + count.get( getBox_type() ) + "&nbsp;/&nbsp;" + totals.get( getBox_type() ) );

			for ( int i = 0; i < copies; i++ )
			{
				labels.add( 
					logo + "<br/>" + 
					from + 
					to + 
					notify +
					phone + "<br/>" + 
					references + "<br/>" + 
					weight + "<br/>" + 
					number );
			}
		}
			
		for ( ShipmentBox box : sub_boxes )
			box.generateDetailedLabels( ctx, shipment, labels, totals, count, copies, colWidth );
	}
	
	public void generateSimpleLabels( AppContext ctx, Shipment shipment, ArrayList<String> labels, HashMap<Integer, Integer> totals, HashMap<Integer, Integer> count, int copies )
	{	
		count.put( getBox_type(), count.get( getBox_type() ) + 1 );

		if ( getLabel_type().equals( LABEL_SIMPLE ) )
		{
			String contents = !getLabel().isEmpty() ? XmlUtils.getDiv( "", ctx.getString( "template.shipment.labels.contents" ) + ":&nbsp" + getLabel() ) : "";
			String weight = XmlUtils.getDiv( "", ctx.getString( "template.shipment.labels.weight" ) + ":&nbsp;" + getGrossWeight() + "&nbsp;kg" );
			String number = XmlUtils.getDiv( "", ctx.getString( "shipment.box.type." + getBox_type() ) + "&nbsp;" + count.get( getBox_type() ) + "&nbsp;/&nbsp;" + totals.get( getBox_type() ) );
			
			for ( int i = 0; i < copies; i++ )
				labels.add( contents + weight + "<br/><br/>" + number);
		}
			
		for ( ShipmentBox box : sub_boxes )
			box.generateSimpleLabels( ctx, shipment, labels, totals, count, copies );
	}

	public void generateAllLabels( AppContext ctx, Shipment shipment, ArrayList<String> labels, HashMap<Integer, Integer> totals, HashMap<Integer, Integer> count, int copies, int colWidth )
	{	
		count.put( getBox_type(), count.get( getBox_type() ) + 1 );

		if ( getLabel_type().equals( LABEL_DETAIL ) )
		{
			String logo 		= XmlUtils.getDiv( "", "<img src=\"" + ctx.getLogoUrl() + "\" width=\"" + colWidth / 2  + "mm\"> </img>" ) ;
			String from 		= XmlUtils.getDiv( "text_left indent", ctx.getString( "words.from" ) + ": " + ctx.getOwner().getName() );
			String to 			= XmlUtils.getDiv( "text_left indent", ctx.getString( "words.to" ) + ": " + shipment.getConsignee().getName() );
			String notify 		= XmlUtils.getDiv( "text_left indent", ctx.getString( "template.shipment.labels.notify" ) + ": " + shipment.getNotify_contact().getName() );
			String phone 		= XmlUtils.getDiv( "text_left indent", ctx.getString( "template.shipment.labels.phone" ) + ": " + shipment.getNotify_contact().getPhone() );
			String references 	= XmlUtils.getDiv( "text_left indent", ctx.getString( "template.shipment.labels.reference" ) + ": " + shipment.getOrders( ctx ) );
			String weight 		= XmlUtils.getDiv( "text_left indent", ctx.getString( "template.shipment.labels.weight" ) + ":" );
			String number 		= XmlUtils.getDiv( "", ctx.getString( "shipment.box.type." + getBox_type() ) + count.get( getBox_type() ) + "&nbsp;/&nbsp;" + totals.get( getBox_type() ) );

			for ( int i = 0; i < copies; i++ )
			{
				labels.add( 
					logo + "<br/>" + 
					from + 
					to + 
					notify +
					phone + "<br/>" + 
					references + "<br/>" + 
					weight + "<br/>" + 
					number );
			}
		}
		else if ( getLabel_type().equals( LABEL_SIMPLE ) )
		{
			String contents = !getLabel().isEmpty() ? XmlUtils.getDiv( "", ctx.getString( "template.shipment.labels.contents" ) + ":&nbsp" + getLabel() ) : "";
			String weight = XmlUtils.getDiv( "", ctx.getString( "template.shipment.labels.weight" ) + ":&nbsp;" + getGrossWeight() + "&nbsp;kg" );
			String number = XmlUtils.getDiv( "", ctx.getString( "shipment.box.type." + getBox_type() ) + "&nbsp;" + count.get( getBox_type() ) + "&nbsp;/&nbsp;" + totals.get( getBox_type() ) );

			for ( int i = 0; i < copies; i++ )
				labels.add( contents + weight + "<br/><br/>" + number);
		}
			
		for ( ShipmentBox box : sub_boxes )
			box.generateAllLabels( ctx, shipment, labels, totals, count, copies, colWidth );
	}
	
}
