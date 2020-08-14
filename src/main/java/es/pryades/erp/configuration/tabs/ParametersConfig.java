package es.pryades.erp.configuration.tabs;

import java.util.List;

import com.vaadin.ui.Component;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.BaseTable;
import es.pryades.erp.common.GenericControlerVto;
import es.pryades.erp.common.ModalParent;
import es.pryades.erp.common.PagedContent;
import es.pryades.erp.common.PagedTable;
import es.pryades.erp.common.ModalWindowsCRUD.Operation;
import es.pryades.erp.configuration.modals.ModalNewParameter;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.Query;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.ParameterVto;
import es.pryades.erp.vto.controlers.ParameterControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class ParametersConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -8161715876828351299L;

	public ParametersConfig( AppContext ctx )
	{
		super( ctx );
	}

	@Override
	public String getResourceKey()
	{
		return "parametersConfig";
	}

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "description", "value" };
	}

	public String[] getSortableCols()
	{
		return null;
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( ParameterVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	@Override
	public Query getQueryObject()
	{
		return new Parameter();
	}

	@Override
	public void onOperationNew()
	{
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewParameter( getContext(), Operation.OP_MODIFY, (Parameter)dto, ParametersConfig.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new ParameterControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Parameter();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._ParametersManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
	}

	@Override
	public boolean hasAddRight()
	{
		return false;
	}

	@Override
	public void updateComponent()
	{
	}

	@Override
	public void onFieldEvent( Component component, String column )
	{
	}
}
