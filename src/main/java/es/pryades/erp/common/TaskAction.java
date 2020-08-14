package es.pryades.erp.common;

import es.pryades.erp.dto.Task;
import es.pryades.erp.reports.CommonEditor;

public interface TaskAction 
{
	public void doTask( AppContext ctx, Task task ) throws BaseException;
	public CommonEditor getTaskDataEditor( AppContext context );
	public boolean isUserEnabledForTask( AppContext context );
}
