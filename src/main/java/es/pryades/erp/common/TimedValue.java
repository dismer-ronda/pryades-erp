package es.pryades.erp.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimedValue implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -59656710634700302L;
	long time;
	double value;
}
