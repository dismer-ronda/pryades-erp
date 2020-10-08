package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.OperationMapper;
import es.pryades.erp.dto.Operation;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class OperationsManagerImpl extends BaseManagerImpl implements OperationsManager
{
	private static final long serialVersionUID = 6816702027936730222L;
	
	private static final Logger LOG = Logger.getLogger( OperationsManagerImpl.class );

	public static BaseManager build()
	{
		return new OperationsManagerImpl();
	}

	public OperationsManagerImpl()
	{
		super( OperationMapper.class, Operation.class, LOG );
	}
}
