package es.pryades.erp.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Attachment implements Serializable
{
	private static final long serialVersionUID = 6805176933223212125L;

	private String name;
	private String type;
	private byte content[];
}
