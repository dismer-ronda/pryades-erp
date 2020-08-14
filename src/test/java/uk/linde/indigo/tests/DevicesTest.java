package uk.linde.indigo.tests;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.apache.velocity.app.Velocity;

import es.pryades.erp.application.I18N;
import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Settings;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.ibatis.DalManager;
import es.pryades.erp.ioc.IOCManager;

/**
 * Unit test for simple App.
 */
public class DevicesTest extends TestCase
{
    private static final Logger LOG = Logger.getLogger( DevicesTest.class );
    
	public void setUp() 
	{
		
	}

	public void tearDown() 
	{
	}
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DevicesTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     * @throws IOException 
     */
    public static Test suite() throws IOException
    {
        return new TestSuite( DevicesTest.class );
    }
    
    /**
     * Rigourous Test :-)
     * @throws Exception 
     */
    public void testApp() throws Exception
    {
    	System.out.println( Utils.getProtocolFromUrl( "https://www.a.b?c=1" ) );
    	System.out.println( Utils.getHostFromUrl( "https://www.a.b?c=1" ) );
    	System.out.println( Utils.getPortFromUrl( "http://www.a.b/test?c=1" ) );
    	System.out.println( Utils.getUriFromUrl( "http://www.a.b/test?c=1" ) );
    }
}

