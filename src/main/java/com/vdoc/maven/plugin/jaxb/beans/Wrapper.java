package com.vdoc.maven.plugin.jaxb.beans;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * a simple class to wrap jaxb root list
 * Created by famaridon on 07/07/2014.
 */
@XmlRootElement(name = "properties")
public class Wrapper<T>
{

	private List<T> items;

	public Wrapper()
	{
		items = new ArrayList<T>();
	}

	public Wrapper(List<T> items)
	{
		this.items = items;
	}

	@XmlAnyElement(lax = true)
	public List<T> getItems()
	{
		return items;
	}

}