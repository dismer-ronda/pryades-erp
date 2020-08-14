package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.dal.ibatis.InvoiceLineMapper;
import es.pryades.erp.dto.InvoiceLine;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class InvoicesLinesManagerImpl extends BaseManagerImpl implements InvoicesLinesManager
{
	private static final long serialVersionUID = 2467944415077201714L;
	
	private static final Logger LOG = Logger.getLogger( InvoicesLinesManagerImpl.class );

	public static BaseManager build()
	{
		return new InvoicesLinesManagerImpl();
	}

	public InvoicesLinesManagerImpl()
	{
		super( InvoiceLineMapper.class, InvoiceLine.class, LOG );
	}

	@Override
	public boolean setEmptyToNull()
	{
		return false;
	}
}
