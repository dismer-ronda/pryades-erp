package es.pryades.erp.transports;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import lombok.Getter;

import org.apache.log4j.Logger;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;

public class TcpIpTransport extends DeviceTransport
{
	private static final long serialVersionUID = 3587168031196707580L;

	private static final Logger LOG = Logger.getLogger( TcpIpTransport.class );

    @Getter private Socket socket;
    
	public TcpIpTransport()
	{
		setLogger( LOG );
		
		socket = null;
	}
	
    public void connect( Address address ) throws BaseException 
    {
    	try 
    	{
    		socket = new Socket( ((TcpIpAddress)address).getIp(), ((TcpIpAddress)address).getPort() );
    		
    		setOutputStream( new ObjectOutputStream( socket.getOutputStream() ) );
    		getOutputStream().flush();
    		
    		setInputStream( new ObjectInputStream( socket.getInputStream() ) ); 
    	}
    	catch ( Throwable e )
    	{
    		Utils.logException( e, LOG );
    		
    		throw new BaseException( e, LOG, BaseException.IO_ERROR );
    	}
    }

    public synchronized void disconnect() 
    {
    	super.disconnect();
		
    	try
		{
			if ( socket != null )
				socket.close();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
   }

	@Override
	public int available() 
	{
		try 
		{
			return socket.getInputStream().available();
		} 
		catch (IOException e) 
		{
		}
		
		return 0;
	}
}
