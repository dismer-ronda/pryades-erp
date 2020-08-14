package es.pryades.erp.dashboard.widgets;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.dashboard.tabs.DashboardTab;

public abstract class GraphWidget extends BaseWidget
{
	private static final long serialVersionUID = 7921937246865198775L;

	private Chart chart;
	
	public abstract void setChartConfiguration( Configuration configuration );
	public abstract void setChartData( Configuration configuration, Double value );
	
	public GraphWidget( AppContext context, DashboardTab dashboardTab )	
	{
		super( context, dashboardTab );
	}
	
	@Override
	public void showFigure( VerticalLayout col, Double value )
	{
		chart = new Chart();
		chart.setSizeFull();
		
		Configuration configuration = chart.getConfiguration();
		
		setChartConfiguration( configuration );
		setChartData( configuration, value );
		
		chart.drawChart( configuration );
		
		col.addComponent( chart );
		col.setComponentAlignment( chart, Alignment.MIDDLE_CENTER );
		
		col.setExpandRatio( chart, 1.0f );
	}

	@Override
	public void updateFigure( Double value )
	{
		//labelFigure.setValue( getPrintableValue( value )  );
	}
	
	@Override
	public void setMainLayoutSize( VerticalLayout layout )
	{
		layout.setWidth( "260px" );
		layout.setHeight( "260px" );
	}
}
