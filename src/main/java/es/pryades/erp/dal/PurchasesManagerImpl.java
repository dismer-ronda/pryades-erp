package es.pryades.erp.dal;

import java.util.List;

import org.apache.log4j.Logger;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Utils;
import es.pryades.erp.dal.ibatis.PurchaseMapper;
import es.pryades.erp.dto.Purchase;
import es.pryades.erp.dto.query.PurchaseQuery;
import es.pryades.erp.ioc.IOCManager;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class PurchasesManagerImpl extends BaseManagerImpl implements PurchasesManager
{
	private static final long serialVersionUID = -3882554937723336630L;
	
	private static final Logger LOG = Logger.getLogger( PurchasesManagerImpl.class );

	public static BaseManager build()
	{
		return new PurchasesManagerImpl();
	}

	public PurchasesManagerImpl()
	{
		super( PurchaseMapper.class, Purchase.class, LOG );
	}

	@Override
	public byte[] generateListZip( AppContext ctx, PurchaseQuery query ) throws Throwable
	{
    	@SuppressWarnings("unchecked")
		List<Purchase> purchases = IOCManager._PurchasesManager.getRows( ctx, query );
    	
    	String fileNames = "";
    	for ( Purchase purchase : purchases )
    	{
    		PurchaseQuery query1 = new PurchaseQuery();
    		query1.setId( purchase.getId() );
    		
    		Purchase purchase1 = (Purchase)IOCManager._PurchasesManager.getRow( ctx, query1);
    		
    		if ( purchase1.getInvoice() != null )
    		{
	    		String fileName = "/tmp/" + purchase1.getFormattedNumber() + ".pdf";
	    		
	    		Utils.writeBinaryFile( fileName, purchase1.getInvoice() );
	    		
	    		if ( !fileNames.isEmpty() )
	    			fileNames += " ";
	    		fileNames += fileName;
    		}
    	}
    	
    	Utils.cmdExec( "zip -Dj /tmp/purchases-list.zip " + fileNames );
    	
    	byte[] bytes = Utils.readBinaryFile( "/tmp/purchases-list.zip" );
    	
    	String parts[] = fileNames.split( " " );
    	for ( String part : parts )
    		Utils.DeleteFile( part );
    	
    	Utils.DeleteFile( "/tmp/purchases-list.zip" );
    	
    	return bytes;
	}
}
