package es.pryades.erp.resources;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.restlet.data.Status;
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
import es.pryades.erp.services.Return;
import es.pryades.erp.services.ReturnFactory;

public class PackingObjectResource extends ServerResource 
{
	private static final Logger LOG = Logger.getLogger( PackingObjectResource.class );

	public PackingObjectResource() 
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
	@Get("json")
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
	        final String id = params.get( "id" );
	        long timeout = Utils.getLong( params.get( "timeout" ), 0 );
	        
			if ( Authorization.isValidRequest( token, ts+timeout, ts, password, timeout ) ) 
			{
		    	ShipmentQuery query = new ShipmentQuery();
		    	query.setId( Long.parseLong( id ) );
		    	
		    	Shipment shipment = (Shipment)IOCManager._ShipmentsManager.getRow( ctx, query );

		    	shipment.removePrivateFields();
		    	
		    	rep = new StringRepresentation( Utils.toJson( shipment ) );
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
