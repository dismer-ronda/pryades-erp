package es.pryades.erp.common;

import java.io.Serializable;

import org.apache.http.HttpHost;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MirrorRequest implements Serializable
{
	private static final long serialVersionUID = -1506296084131497682L;
	
	private String url;
	private String text;
}
