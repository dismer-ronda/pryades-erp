package es.pryades.erp.dal.ibatis;

import java.util.ArrayList;

import es.pryades.erp.dto.BaseDto;
import es.pryades.erp.dto.Query;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public interface BaseMapper
{
    public void addRow( BaseDto row );
    public void setRow( BaseDto row );
    
    public void delRow( BaseDto row );
    
    public long getNumberOfRows( Query query );

    public ArrayList<BaseDto> getRows( Query query );
    public ArrayList<BaseDto> getPage( Query query );
    
    public BaseDto getLastRow( BaseDto query );
    public BaseDto getRow( BaseDto row );
    public BaseDto getNextRow( BaseDto query );

    public void delAllRows( Query row );
}
