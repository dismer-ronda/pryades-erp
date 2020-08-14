package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.QuotationLineMapper;
import es.pryades.erp.dto.QuotationLine;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class QuotationsLinesManagerImpl extends BaseManagerImpl implements QuotationsLinesManager
{
	private static final long serialVersionUID = -8511512570837118712L;
	
	private static final Logger LOG = Logger.getLogger( QuotationsLinesManagerImpl.class );

	public static BaseManager build()
	{
		return new QuotationsLinesManagerImpl();
	}

	public QuotationsLinesManagerImpl()
	{
		super( QuotationLineMapper.class, QuotationLine.class, LOG );
	}
}
