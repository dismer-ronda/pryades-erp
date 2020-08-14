package es.pryades.erp.transports;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import es.pryades.erp.common.BaseException;
import es.pryades.erp.common.Utils;

public class TcpServerTransport extends DeviceTransport
{
	private static final long serialVersionUID = -2428029085841581079L;

	private static final Logger LOG = Logger.getLogger( TcpServerTransport.class );

    Socket socket;

	public TcpServerTransport()
	{
		setLogger( LOG );
		
		socket = null;
	}
	
	public TcpServerTransport bind( Socket connection ) throws BaseException
	{
		this.socket = connection;
		
		connect( null );
		
		return this;
	}
	
    public void connect( Address address ) throws BaseException 
    {
    	try 
    	{
    		setOutputStream( new ObjectOutputStream( socket.getOutputStream() ) );
    		getOutputStream().flush();
    		
    		setInputStream( new ObjectInputStream( socket.getInputStream() ) ); 
    	}
    	catch ( Throwable e )
    	{
    		Utils.logException( e, LOG );
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
