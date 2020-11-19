package es.pryades.erp.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class ShipmentAttachment extends BaseDto
{
	private static final long serialVersionUID = -1273587047163223779L;

	private Long ref_shipment;						

	private String title;
	private byte[] data;
}
