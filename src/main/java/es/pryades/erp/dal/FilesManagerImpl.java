package es.pryades.erp.dal;

import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.CalendarUtils;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.ibatis.FileMapper;
import es.pryades.erp.dto.File;
import es.pryades.erp.dto.Parameter;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class FilesManagerImpl extends BaseManagerImpl implements FilesManager
{
	private static final long serialVersionUID = -9097016134505590242L;
	
	private static final Logger LOG = Logger.getLogger( FilesManagerImpl.class );

	public static BaseManager build()
	{
		return new FilesManagerImpl();
	}

	public FilesManagerImpl()
	{
		super( FileMapper.class, File.class, LOG );
	}

	@Override
	public long getLogSetting() 
	{
		return Parameter.PAR_LOG_DEFAULT;
	}

	public Long saveFile( AppContext ctx, String fileName )
	{
		try
		{
			File file = new File();
			file.setFile_binary( Utils.readBinaryFile( fileName ) );
			file.setFile_date( CalendarUtils.getTodayAsLong( "UTC" ) );
			file.setFile_name( fileName );
			
			setRow( ctx, null, file );
			
			return file.getId();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return null;
	}

	public byte [] readFile( AppContext ctx, Long id )
	{
		try
		{
			File query = new File();
			query.setId( id );
			
			File file = (File)getRow( ctx, query );
			
			return file.getFile_binary();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		return null;
	}

	public void deleteFile( AppContext ctx, Long id )
	{
		try
		{
			File file = new File();
			file.setId( id );
			
			delRow( ctx, file );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
}
