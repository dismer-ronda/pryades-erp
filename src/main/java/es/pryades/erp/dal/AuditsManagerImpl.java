package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.AuditMapper;
import es.pryades.erp.dto.Audit;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class AuditsManagerImpl extends BaseManagerImpl implements AuditsManager
{
	private static final long serialVersionUID = -9029939979206104669L;
	
	private static final Logger LOG = Logger.getLogger( AuditsManagerImpl.class );

	public static BaseManager build()
	{
		return new AuditsManagerImpl();
	}

	public AuditsManagerImpl()
	{
		super( AuditMapper.class, Audit.class, LOG );
	}
}
