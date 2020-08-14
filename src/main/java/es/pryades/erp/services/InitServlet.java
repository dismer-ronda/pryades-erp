package es.pryades.erp.services;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.velocity.app.Velocity;

import es.pryades.erp.application.I18N;
import es.pryades.erp.common.Settings;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.ibatis.DalManager;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.processors.SignalsProcessor;
import es.pryades.erp.processors.TasksProcessor;

public class InitServlet extends HttpServlet
{
	private static final long serialVersionUID = 4644675061264784881L;
	
	private static final Logger LOG = Logger.getLogger( InitServlet.class );

	public void init() 
    {
        try 
		{
    		String logFile = Utils.getEnviroment( "LOGFILE" );
    		if ( logFile != null )
    		{
    			try
    			{
        			Logger rootLogger = Logger.getRootLogger();
        			rootLogger.setLevel( Level.INFO );
        			
					RollingFileAppender appender = new RollingFileAppender( new PatternLayout( "[LINDE %d{dd/MM HHmmss.SSS}] %c{1} %m%n" ), logFile );
					appender.setMaxFileSize( "10MB" );
					appender.setMaxBackupIndex( 7 );
					
    				rootLogger.addAppender( appender );
    			}
    			catch (Throwable e)
    			{
    				LOG.info( "Failed to add appender !!" );
    			}
    		}
            
			Settings.Init();
	    	IOCManager.Init();
	    	
	    	I18N.Init();

	    	DalManager.Init( Settings.DB_engine, 
	    			Settings.DB_driver, 
	    			Settings.DB_url, 
	    			Settings.DB_user, 
	    			Settings.DB_password );

	    	System.setProperty("java.net.preferIPv4Stack" , "true");
	    	
	    	Properties p = new Properties();
	    	p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
	    	Velocity.init( p );

	    	BootLoader.bootup();
	    	
    	    LOG.info( "started" );
		} 
		catch ( Throwable e ) 
		{
			Utils.logException( e, LOG );
		}
	}
	
	@Override
	public void destroy()
	{
		LOG.info( "destroy servlet called" );
		
    	TasksProcessor.stopProcessor();
    	SignalsProcessor.stopProcessor();

    	String prefix = getClass().getSimpleName() +" destroy() ";
	    ServletContext ctx1 = getServletContext();
	    
	    try 
	    {
	        Enumeration<Driver> drivers = DriverManager.getDrivers();
	        
	        while(drivers.hasMoreElements()) 
	        {
	            DriverManager.deregisterDriver(drivers.nextElement());
	        }
	    } 
	    catch(Exception e) 
	    {
	        ctx1.log( prefix + "Exception caught while deregistering JDBC drivers", e );
	    }
	    
	    ctx1.log( prefix + " complete" );

	    super.destroy();
	}
}
