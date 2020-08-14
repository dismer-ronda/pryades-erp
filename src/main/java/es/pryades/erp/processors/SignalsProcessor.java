package es.pryades.erp.processors;

import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Queue;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.Task;
import es.pryades.erp.ioc.IOCManager;

public class SignalsProcessor extends Thread
{
    private static final Logger LOG = Logger.getLogger( SignalsProcessor.class );
    
    private volatile boolean ended;

	public static SignalsProcessor instance = null;
	
	public Queue objects;
	
	public SignalsProcessor()
	{
    	objects = new Queue();
    	ended = false;
	}

	public static void startProcessor()
	{		
		instance = new SignalsProcessor();
		
		instance.start();
	}
	
    public static void stopProcessor() 
    {
    	LOG.info( "stop command received" );
    	
    	if ( instance != null )
    	{
    		instance.stopInstance();

    		try 
			{
				instance.join();
			} 
			catch ( Throwable e ) 
			{
				if ( !(e instanceof BaseException) )
					new BaseException( e, LOG, BaseException.UNKNOWN );
			}
    	}
    }

    public static void dispatchSignal( Object object ) 
    {
    	if ( instance != null )
    		instance.objects.push( object, true );
    }

    public static SignalsProcessor getInstance()
	{
		return instance;
	}
	
	public synchronized void stopInstance()
	{
		ended = true;

		synchronized ( objects )
		{
			objects.notifyAll();
		}
	}
	
	public synchronized boolean isStopped()
	{
		return ended;
	}	
	
	@Override
	public void run()
	{
		LOG.info( "main thread started" );

		while ( true  )
		{
			try
			{
				LOG.info( "start waiting for signals ..." );
				
				synchronized ( objects )
				{
					objects.wait();
				}
				
				Object obj = objects.pop();
				
				if ( obj != null )
					new IncomingTaskDispatch( obj ).start();
				
			} 
			catch ( Throwable e ) 
			{
				Utils.logException( e, LOG );
			}

			if ( isStopped() )
				break;
		}
		
		LOG.info( "main thread finished" );
	}
	
	class IncomingTaskDispatch extends Thread 
	{
		private Object object;

		IncomingTaskDispatch( Object object ) 
	    {
    		this.object = object;
	    }
		
	    public void run () 
	    {
			LOG.info( "Dispatcher thread started" );
			
	    	try
	    	{
				AppContext ctx = new AppContext( "en" );
				IOCManager._ParametersManager.loadParameters( ctx );
				
				if ( object instanceof Task )
					IOCManager._TasksManager.doTask( ctx, (Task)object, true );
	    	}
	    	catch ( Throwable e )
	    	{
	    		Utils.logException( e, LOG );
	    	}
	    	
			LOG.info( "Dispatcher thread finished" );
	    }
	}
}
