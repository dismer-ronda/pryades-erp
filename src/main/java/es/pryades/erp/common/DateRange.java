package es.pryades.erp.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DateRange implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2161966503963240923L;
	private Long from;
	private Long to;
}
