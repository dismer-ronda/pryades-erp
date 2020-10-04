package es.pryades.erp.resources;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Authorization;
import es.pryades.erp.common.Utils;
import es.pryades.erp.ioc.IOCManager;
import es.pryades.erp.services.Return;
import es.pryades.erp.services.ReturnFactory;

public class QrResource extends ServerResource 
{
	private static final Logger LOG = Logger.getLogger( QrResource.class );

	public QrResource() 
	{
		super();
	}

    @Override
    protected void doInit() throws ResourceException 
    {
        setExisting( true );
    }
	
	/**
	 * GET
	 */
	@Get("png")
    public Representation toJson() throws Exception 
    {
		Representation rep;
		
		HashMap<String,String> params = new HashMap<String,String>();
		Utils.getParameters( getRequest().getResourceRef().getQuery(), params );

		Return ret = new Return();
		
		try
		{
			final AppContext ctx = new AppContext("es");
			IOCManager._ParametersManager.loadParameters( ctx );

			String user = params.get( "user" );
	    	IOCManager._UsersManager.loadUsuario( ctx, user );
			
			String token = params.get( "token" );
			String password = ctx.getUser().getPwd();
			String code = params.get( "code" );
	
			if ( code != null )
			{
				code = Authorization.decrypt( code, password );
		        LOG.info( "code = " + code );
			
				params.clear();
		        Utils.getParameters( code, params );
			}
	        
	        String ts = params.get( "ts" );
	        long timeout = Utils.getLong( params.get( "timeout" ), 0 );
	        final String text = params.get( "text" );
	        
	        LOG.info( "ts = " + ts);
	        LOG.info( "timeout = " + timeout );
	        LOG.info( "text = " + Utils.getUrlDecoded( text ) );
	        
			if ( Authorization.isValidRequest( token, ts+timeout, ts, password, timeout ) ) 
			{
				rep = new OutputRepresentation(MediaType.IMAGE_PNG ) 
				{
					@Override
					public void write( OutputStream arg0 ) throws IOException
					{
						try
						{
							Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
							hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
							
							// Now with zxing version 3.2.1 you could change border size (white border size to just 1)
							hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
							hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
				 
							QRCodeWriter qrCodeWriter = new QRCodeWriter();
							BitMatrix byteMatrix = qrCodeWriter.encode(Utils.getUrlDecoded( text ), BarcodeFormat.QR_CODE, 256, 256, hintMap);
							int CrunchifyWidth = byteMatrix.getWidth();
							BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth, BufferedImage.TYPE_INT_RGB);
							image.createGraphics();
				 
							Graphics2D graphics = (Graphics2D) image.getGraphics();
							graphics.setColor(Color.WHITE);
							graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
							graphics.setColor(Color.BLACK);
				 
							for (int i = 0; i < CrunchifyWidth; i++) {
								for (int j = 0; j < CrunchifyWidth; j++) {
									if (byteMatrix.get(i, j)) {
										graphics.fillRect(i, j, 1, 1);
									}
								}
							}
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ImageIO.write(image, "png", baos);
							arg0.write( baos.toByteArray() );
						}
						catch ( Throwable e )
						{
							Utils.logException( e, LOG );
						}
					}
				};				
			}
			else
			{
				ret.setCode( ReturnFactory.STATUS_4XX_FORBIDDEN );
				ret.setDesc( "Access denied" );
	
				rep = new StringRepresentation( ret.getDesc() );
			}
		}
		catch( Throwable e )
		{
			ret.setCode( ReturnFactory.STATUS_4XX_NOT_FOUND );
			ret.setDesc( "Resource not found" );

			rep = new StringRepresentation( ret.getDesc() ); 
			
			Utils.logException( e, LOG );
		}	
		
		getResponse().setStatus( Status.valueOf( ret.getCode() ) );
		
		return rep;
    }
}
