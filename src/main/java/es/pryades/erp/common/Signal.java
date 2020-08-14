package es.pryades.erp.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Signal implements Serializable 
{
	private static final long serialVersionUID = 7412718170741323921L;

	public static final int EXECUTE_TASK 			= 1;
	public static final int GENERATE_REPORT 		= 2;
	
	public static final int RESPONSE_OK				= 1;
	public static final int RESPONSE_ERROR			= 0;
	
	private Integer type;
	private String data;
}
