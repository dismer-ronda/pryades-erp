package es.pryades.erp.common;

import java.util.List;

import com.vaadin.ui.Component;

import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;

public interface VtoControllerFactory 
{
	public GenericControlerVto getControlerVto( AppContext ctx );
	public BaseDto getFieldDto();
	public BaseManager getFieldManagerImp();
	public void preProcessRows( List<BaseDto> rows );
	public void onFieldEvent( Component component, String column );
}
