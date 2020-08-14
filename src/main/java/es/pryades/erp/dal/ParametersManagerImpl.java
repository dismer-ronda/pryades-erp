package es.pryades.erp.dal;

import java.util.HashMap;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.ibatis.ParameterMapper;
import es.pryades.erp.dto.Parameter;
import es.pryades.erp.dto.Query;

import org.apache.log4j.Logger;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class ParametersManagerImpl extends BaseManagerImpl implements ParametersManager
{
	private static final long serialVersionUID = -5028886986098594934L;

	private static final Logger LOG = Logger.getLogger( ParametersManagerImpl.class );

	public static BaseManager build()
	{
		return new ParametersManagerImpl();
	}

	public ParametersManagerImpl()
	{
		super( ParameterMapper.class, Parameter.class, LOG );
	}
	
	public void loadParameters( AppContext ctx )
	{
		ctx.setParameters( getParameters( ctx ) );
	}

	@Override
	public HashMap<Long, Parameter> getParameters(AppContext ctx) 
	{
		HashMap<Long, Parameter> ret = new HashMap<Long, Parameter>();
		
		try 
		{
			@SuppressWarnings("unchecked")
			List<Parameter> parameters = getRows(ctx, new Query() );
			
			for ( Parameter parameter : parameters )
				ret.put( parameter.getId(), parameter );
		} 
		catch ( Throwable e ) 
		{
			Utils.logException( e, LOG );
		}
		
		return ret;
	}

	@Override
	public boolean isLogEnabled( AppContext ctx, String action )
	{
		return action.equals( "U" );
	}
	
	@Override
	public boolean setEmptyToNull()
	{
		return false;
	}

}
