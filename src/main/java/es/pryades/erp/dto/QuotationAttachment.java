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
public class QuotationAttachment extends BaseDto
{
	private static final long serialVersionUID = -6972141767960618718L;

	private Long ref_quotation;						

	private String title;
	private String format;
	private byte[] data;
}
