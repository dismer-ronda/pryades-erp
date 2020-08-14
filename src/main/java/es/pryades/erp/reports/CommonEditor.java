package es.pryades.erp.reports;

import java.io.Serializable;

import com.vaadin.data.util.BeanItem;

import es.pryades.erp.common.AppContext;
import lombok.Getter;
import lombok.Setter;

public abstract class CommonEditor implements Serializable
{
	private static final long serialVersionUID = 4048712728788163137L;

	@Getter @Setter AppContext context;

	@SuppressWarnings("rawtypes")
	protected BeanItem bi;

	public CommonEditor( AppContext context )
	{
		this.context = context;
	}
	
	protected String getString( String key )
	{
		return getContext().getString( key );
	}
}
