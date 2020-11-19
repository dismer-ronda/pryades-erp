package es.pryades.erp.dal;

import java.util.List;

import es.pryades.erp.common.AppContext;
import es.pryades.erp.common.Attachment;
import es.pryades.erp.dto.Shipment;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface ShipmentsManager extends BaseManager
{
	public byte[] generatePacking( AppContext ctx, Shipment org ) throws Throwable;	
	public byte[] generateInstructions( AppContext ctx, Shipment org ) throws Throwable;
	public byte[] generateNoDoubleUseLetter( AppContext ctx, Shipment org ) throws Throwable;
	
	public void getShipmentRequestDocuments( AppContext ctx, Shipment shipment, List<Attachment> attachments ) throws Throwable;
	public void getShipmentNotificationDocuments( AppContext ctx, Shipment shipment, List<Attachment> attachments ) throws Throwable;
}
