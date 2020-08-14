package es.pryades.erp.dal;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.TaskAction;
import es.pryades.erp.dto.Task;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface TasksManager extends BaseManager
{
	public TaskAction getTaskAction( Task task );
	public TaskAction getTaskAction( int task );
	public void doTask( AppContext ctx, Task task, boolean forced );
}
