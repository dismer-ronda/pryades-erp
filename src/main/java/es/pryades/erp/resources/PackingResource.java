package es.pryades.erp.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.log4j.Logger;
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
import es.pryades.erp.dto.Shipment;
import es.pryades.erp.dto.query.ShipmentQuery;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.reports.PdfExportPacking;
import es.pryades.erp.services.Return;
import es.pryades.erp.services.ReturnFactory;

public class PackingResource extends ServerResource 
{
	private static final Logger LOG = Logger.getLogger( PackingResource.class );

	public PackingResource() 
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
	        final String id = params.get( "id" );
	        final String url = params.get( "url" );
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
					    	ShipmentQuery query = new ShipmentQuery();
					    	query.setId( Long.parseLong( id ) );
					    	
					    	Shipment shipment = (Shipment)IOCManager._ShipmentsManager.getRow( ctx, query );
					    	
							AppContext ctx1 = new AppContext( "en" );
							
							IOCManager._ParametersManager.loadParameters( ctx1 );
							ctx1.setUser( ctx.getUser() );
							ctx1.addData( "Url", url );
					    	ctx1.loadOwnerCompany();

							PdfExportPacking export = new PdfExportPacking( shipment );
							
							export.setOrientation( "portrait" );
							export.setPagesize( pagesize );
							export.setTemplate( template );
							export.setContext( ctx1 );
						
							export.doExport( arg0 );
						}
						catch ( Throwable e )
						{
							Utils.logException( e, LOG );
						}
					}
				};				
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
