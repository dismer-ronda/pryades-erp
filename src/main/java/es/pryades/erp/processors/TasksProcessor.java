package es.pryades.erp.processors;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dto.Task;
import es.pryades.erp.dto.query.TaskQuery;
import es.pryades.erp.ioc.IOCManager;

/**
 * 
 * @author dismer.ronda
 * @version 
 * @since Jul, 2010
 */

@SuppressWarnings({"unchecked"})
public class TasksProcessor extends TimerTask
{
    private static final Logger LOG = Logger.getLogger( TasksProcessor.class );
    
    private static TasksProcessor processor = null;

    private Timer timer; 
    
	public static void startProcessor() throws BaseException
	{
		processor = new TasksProcessor();

		processor.init();
	}

	public static void stopProcessor()
	{
		synchronized ( processor )
		{
			processor.stopRequest();
		}		
	}
	
	private TasksProcessor()
	{
		super();
	}

	private void init() throws BaseException
	{
    	timer = new Timer();

    	long wait = Utils.getHowMuchToWaitForFirstSecondOfNextHour(); 
		long repeat = Utils.ONE_HOUR; 

		timer.schedule( this, wait, repeat );

		LOG.info( "----- started. Waiting " + wait/1000 + " seconds" );
	}
	
	private void stopRequest()
	{
		synchronized ( this )
		{
			timer.cancel();

			LOG.info( "----- finished" );
		}
	}

	@Override
	public void run()
	{
		try 
		{
			AppContext ctx = new AppContext( "en" );
			IOCManager._ParametersManager.loadParameters( ctx );
	    	ctx.loadOwnerCompany();
			
			List<Task> tasks = IOCManager._TasksManager.getRows( ctx, new TaskQuery() );

			for ( Task task : tasks )
				IOCManager._TasksManager.doTask( ctx, task, false );
		} 
		catch ( Throwable e ) 
		{
			Utils.logException( e, LOG );
			
			if ( !(e instanceof BaseException) )
				new BaseException( e, LOG, BaseException.UNKNOWN );
		}
		
		LOG.info( "timer finished" );
	}
}
