package es.pryades.erp.reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.xhtmlrenderer.pdf.ITextRenderer;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;
import lombok.Getter;
import lombok.Setter;

/**
 * @author dismer.ronda
 *
 */

public class PdfExport extends ExportBase
{
	private static final long serialVersionUID = 633422593833984489L;

	private static final Logger LOG = Logger.getLogger( PdfExport.class );
	
	@Getter @Setter private String pagesize;
	@Getter @Setter private String orientation;
	@Getter @Setter private String template;
	
	public VelocityContext createVelocityContext() throws BaseException
	{
		VelocityContext vcontext = super.createVelocityContext();
		
		vcontext.put( "pagesize", pagesize ); 
		vcontext.put( "orientation", orientation ); 
		vcontext.put( "title", "Report title" );
		vcontext.put( "context", getContext() );
				
		return vcontext;
	}

	public void cleanupAfterGeneration() throws BaseException
	{
	}
	
	public boolean doExport( OutputStream resp ) throws BaseException
	{
		try 
		{
			InputStream in = createStreamTemplate( template );
			
			StringWriter stWriter = new StringWriter();
	        Reader templateReader = new BufferedReader( new InputStreamReader( in ) );	
	        
			VelocityContext vcontext = createVelocityContext();
			
			Velocity.evaluate( vcontext, stWriter, getClass().getSimpleName(), templateReader );

	        ITextRenderer renderer = new ITextRenderer();
	        
	        renderer.setDocumentFromString( stWriter.toString() );
	        renderer.layout();
	        renderer.createPDF( resp );
		} 
		catch ( Throwable e ) 
		{
			Utils.logException( e, LOG );
			
			if ( e instanceof IOException )
				throw new BaseException( e, LOG, BaseException.IO_ERROR );
			
			if ( e instanceof InterruptedException )
				throw new BaseException( e, LOG, BaseException.INTERRUPTED_ERROR );
			
			throw new BaseException( e, LOG, BaseException.UNKNOWN );
		}
		finally
		{
	        cleanupAfterGeneration();
		}
		
		return true;
	}
}
