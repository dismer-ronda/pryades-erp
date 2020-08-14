package es.pryades.erp.dto;

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

	private Integer box_type;		
	private String label;		
	
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
}
