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
import es.pryades.erp.configuration.modals.ModalNewProfile;
import es.pryades.erp.dal.BaseManager;
import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.Profile;
import es.pryades.erp.dto.Query;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.vto.ProfileVto;
import es.pryades.erp.vto.controlers.ProfileControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class ProfilesConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -2172770039396245503L;

	public ProfilesConfig( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "id" );
		setOrder( "asc" );
	}

	@Override
	public String getResourceKey()
	{
		return "profilesConfig";
	}

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "id", "description" };
	}

	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( ProfileVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	@Override
	public Component getQueryComponent()
	{
		return null;
	}
	
	@Override
	public Query getQueryObject()
	{
		return new Profile();
	}

	@Override
	public void onOperationNew()
	{
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewProfile( getContext(), Operation.OP_MODIFY, (Profile)dto, ProfilesConfig.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new ProfileControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Profile();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._ProfilesManager;
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
