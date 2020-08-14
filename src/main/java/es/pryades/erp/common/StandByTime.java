package es.pryades.erp.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StandByTime implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2147873777050553315L;
	private Long standBy;
	private Long total;
	
	public Long getDeliveryTime()
	{
		return total - standBy;
	}
}
