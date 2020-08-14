package es.pryades.erp.resources;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.pryades.erp.services.ReturnFactory;

import org.apache.log4j.Logger;

public class TestResource extends ServerResource 
{
    private static final Logger LOG = Logger.getLogger( TestResource.class );

	public TestResource() 
	{
		super();
	}

    @Override
    protected void doInit() throws ResourceException 
    {
        setExisting( true  );
    }
    
    @Post("xml")
    public void doPut( Representation entity ) throws Exception 
    {
		String text = entity != null ? entity.getText() : "";
		
		LOG.info( "**********************************" );
		LOG.info( text );
		LOG.info( "**********************************" );

		getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_2XX_OK ) );
		getResponse().setEntity( new StringRepresentation( "OK" ) );
    }
}
