package es.pryades.erp.common;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

public interface MinimizerContainer
{
	void minimizeWindow( Window window, Component icon );
	void restoreWindow( Component icon );
}
