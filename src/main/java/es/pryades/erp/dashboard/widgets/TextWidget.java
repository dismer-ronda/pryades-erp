package es.pryades.erp.dashboard.widgets;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.dashboard.tabs.DashboardTab;

public abstract class TextWidget extends BaseWidget
{
	private static final long serialVersionUID = 765439733729717876L;

	private Label labelFigure;
	
	public TextWidget( AppContext context, DashboardTab dashboardTab )	
	{
		super( context, dashboardTab );
	}
	
	@Override
	public void showFigure( VerticalLayout col, Double value )
	{
		labelFigure = new Label( getPrintableValue( value ) );
		labelFigure.addStyleName( "widget_figure" );

		col.addComponent( labelFigure );
		col.setComponentAlignment( labelFigure, Alignment.MIDDLE_CENTER );
		
		col.setExpandRatio( labelFigure, 1.0f );
	}

	@Override
	public void updateFigure( Double value )
	{
		labelFigure.setValue( getPrintableValue( value )  );
	}
	
	@Override
	public void setMainLayoutSize( VerticalLayout layout )
	{
		layout.setWidth( "260px" );
		layout.setHeight( "260px" );
	}
}
