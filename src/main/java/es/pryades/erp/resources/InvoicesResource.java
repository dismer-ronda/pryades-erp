package es.pryades.erp.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Authorization;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.Invoice;
import es.pryades.erp.dto.query.InvoiceQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.reports.PdfExportInvoices;
import es.pryades.erp.services.Return;
import es.pryades.erp.services.ReturnFactory;

public class InvoicesResource extends ServerResource 
{
	private static final Logger LOG = Logger.getLogger( InvoicesResource.class );

	public InvoicesResource() 
	{
		super();
	}

    @Override
    protected void doInit() throws ResourceException 
    {
        setExisting( true );
    }
	
	/**
	 * GET
	 */
	@Get("pdf")
    public Representation toJson() throws Exception 
    {
		Representation rep;
		
		HashMap<String,String> params = new HashMap<String,String>();
		
		Utils.getParameters( getRequest().getResourceRef().getQuery(), params );

		Return ret = new Return();
		
		try
		{
			final AppContext ctx = new AppContext("es");
			IOCManager._ParametersManager.loadParameters( ctx );
	    	ctx.loadOwnerCompany();

			String user = params.get( "user" );
			
	    	IOCManager._UsersManager.loadUsuario( ctx, user );
			
			String token = params.get( "token" );
			String password = ctx.getUser().getPwd();
			String code = params.get( "code" );
	
			if ( code != null )
			{
				code = Authorization.decrypt( code, password );
			
				params.clear();
		        Utils.getParameters( code, params );
			}
	        
	        String ts = params.get( "ts" );
	        final String pagesize = params.get( "pagesize" );
	        final String template = params.get( "template" );
	        final String q = params.get( "query" );
	        final String url = params.get( "url" );
	        final String language = params.get( "language" );
	        final String name = params.get( "name" );
	        long timeout = Utils.getLong( params.get( "timeout" ), 0 );
	        
			if ( Authorization.isValidRequest( token, ts+timeout, ts, password, timeout ) ) 
			{
				rep = new OutputRepresentation(MediaType.APPLICATION_PDF) 
				{
					@Override
					public void write( OutputStream arg0 ) throws IOException
					{
						try
						{
					    	InvoiceQuery query = (InvoiceQuery)Utils.toPojo( Utils.getUrlDecoded( q ), InvoiceQuery.class, false );
					    	query.setOrder( "asc" );
					    	query.setOrderby( "invoice_date" );
					    	
					    	List<Invoice> invoices = IOCManager._InvoicesManager.getRows( ctx, query );
							
					    	ctx.setLanguage( language );
							ctx.addData( "Url", url );

							PdfExportInvoices export = new PdfExportInvoices( invoices, query );
							
							export.setOrientation( "portrait" );
							export.setPagesize( pagesize );
							export.setTemplate( template );
							export.setContext( ctx );
						
							export.doExport( arg0 );
						}
						catch ( Throwable e )
						{
							Utils.logException( e, LOG );
						}
					}
				};				

				Disposition disp = new Disposition( Disposition.TYPE_INLINE );
				disp.setFilename( name );
				rep.setDisposition( disp );
			}
			else
			{
				ret.setCode( ReturnFactory.STATUS_4XX_FORBIDDEN );
				ret.setDesc( "Access denied" );
	
				rep = new StringRepresentation( ret.getDesc() );
			}
		}
		catch( Throwable e )
		{
			ret.setCode( ReturnFactory.STATUS_4XX_NOT_FOUND );
			ret.setDesc( "Resource not found" );

			rep = new StringRepresentation( ret.getDesc() ); 
			
			Utils.logException( e, LOG );
		}	
		
		getResponse().setStatus( Status.valueOf( ret.getCode() ) );
		
		return rep;
    }
}
