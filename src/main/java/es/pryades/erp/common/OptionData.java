package es.pryades.erp.common;

import java.io.Serializable;
import java.util.ResourceBundle;

import lombok.Data;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
@Data 
@SuppressWarnings("rawtypes")
public class OptionData implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6063254209983470878L;
	private String id;
	private String caption;
	private String toolTip;
	private String icon;
	private Boolean enabled;
	private Class clazz;
	private Button button;
	private String right;
	
	public Button buildButton(ResourceBundle resource){
		if (caption == null){
			button = new Button();
		}else{
			button = new Button( AppUtils.getString( resource, caption ) );
		}

		button.setData( this );
		button.setEnabled( enabled );
		if (toolTip != null){
			button.setDescription( AppUtils.getString( resource, toolTip));
		}
		
		if (icon != null){
			button.setIcon(new ThemeResource(icon));
		}
		
		return button;
	}
}
