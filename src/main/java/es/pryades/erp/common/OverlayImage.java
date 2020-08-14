package es.pryades.erp.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OverlayImage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4134250909560656057L;
	boolean global;
	private int x;
	private int y;
	private String text;
}
