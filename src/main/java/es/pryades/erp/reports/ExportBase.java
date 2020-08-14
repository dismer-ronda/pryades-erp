package es.pryades.erp.reports;

import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import lombok.Getter;
import lombok.Setter;

import org.apache.velocity.VelocityContext;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;

/**
 * @author dismer.ronda
 *
 */

public abstract class ExportBase implements Serializable
{
	private static final long serialVersionUID = -6807932381217828958L;
	
	@Getter @Setter private AppContext context;
	
	public InputStream createStreamTemplate( String id ) throws BaseException, UnsupportedEncodingException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    return classLoader.getResourceAsStream( id + ".xhtml" );
	}
	
	public VelocityContext createVelocityContext() throws BaseException
	{
		return new VelocityContext();
	}
}
