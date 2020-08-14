package es.pryades.erp.tasks;

import java.io.Serializable;

import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.TaskAction;
import es.pryades.erp.dto.Task;
import es.pryades.erp.reports.CommonEditor;

public class ClearLogTaskAction implements TaskAction, Serializable
{
	private static final long serialVersionUID = 840749656207510915L;
	
	private static final Logger LOG = Logger.getLogger( ClearLogTaskAction.class );

    public ClearLogTaskAction()
	{
	}

	@Override
	public void doTask( AppContext ctx, Task task ) throws BaseException
	{
		LOG.info( "-------- started" );
		
		//ClearLogTaskQuery query = (ClearLogTaskQuery) Utils.toPojo( task.getDetails(), ClearLogTaskQuery.class );

		/*DateRange dateRange = Utils.getDateRange( query.getDate(), CalendarUtils.getCurrentCalendar( report.getTimezone() ) );
		
    	PdfExportSmartStations export = new PdfExportSmartStations();
		
		export.setContext( ctx );
		export.setOrientation( report.getOrientation().equals( 0 ) ? "portrait" : "landscape" );
		export.setSize( Utils.getPaperSize( report.getSize() ) );
		export.setDate( CalendarUtils.getTodayAsString( report.getTimezone(), "yyyyMMddHHmmss" ) );
		export.setTemplate( "smart-stations-template" );
		export.setOrderby( report.getOrder_field() );
		export.setOrder( report.getOrder_type().equals( 0 ) ? "asc" : "desc" );
		export.setFrom_date( dateRange.getFrom() );
		export.setTo_date( dateRange.getTo() );
		export.setPlant( query.getRef_plant() );
		export.setRegion( query.getRef_region() );
		export.setValid( Boolean.FALSE );

		export.doExport( os );*/

		LOG.info( "-------- finished" );
	}

	@Override
	public CommonEditor getTaskDataEditor( AppContext context )
	{
		return null; 
	}

	@Override
	public boolean isUserEnabledForTask( AppContext context )
	{
		return true;
	}
}
