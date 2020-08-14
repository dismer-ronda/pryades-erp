package es.pryades.erp.transports;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import lombok.Data;

import org.apache.log4j.Logger;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;

@Data
public abstract class DeviceTransport implements Serializable
{ 
	private static final long serialVersionUID = 6979826424353523056L;

	public Logger logger;

    public ObjectInputStream inputStream;
    public ObjectOutputStream outputStream;
    
    private boolean broken = false;

    public abstract void connect( Address address ) throws Exception;
    public abstract int available();
    
    public DeviceTransport()
    {
    	inputStream = null;
    	outputStream = null;
    }
    
    public Object readObject() throws ClassNotFoundException, IOException
    {
    	return inputStream.readObject();
    }

    public Object readObject( long timeout ) throws ClassNotFoundException, IOException
    {
		long start = System.currentTimeMillis();
		
		while ( true )
		{
			if ( available() > 0 )
				return inputStream.readObject();
			
			Utils.Sleep( 100 );
			
			if ( System.currentTimeMillis() - start >= timeout )
				break;
		}
    	
    	return null;
    }
    
    public void writeObject( Object object ) throws IOException
    {
    	outputStream.writeObject( object );
    }
    
	public void closeStreams()
    {
		try
		{
			if ( getOutputStream() != null )
				getOutputStream().close();
			if ( getInputStream() != null )
				getInputStream().close();
		}
		catch ( Throwable e )
		{
			new BaseException( e, logger, BaseException.UNKNOWN );
		}
    }

	public synchronized void disconnect() 
    {
		try
		{
			closeStreams();
		}
		catch ( Throwable e )
		{
			new BaseException( e, logger, BaseException.IO_ERROR );
		}
    }
}