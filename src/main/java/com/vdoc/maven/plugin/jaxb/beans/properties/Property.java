package com.vdoc.maven.plugin.jaxb.beans.properties;

import com.vdoc.maven.plugin.jaxb.beans.adapter.EnvironmentAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by famaridon on 07/07/2014.
 */
@XmlRootElement
public class Property
{
	private String name;
	private Map<String,String> values = new HashMap<>();

	@XmlAttribute
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@XmlJavaTypeAdapter(EnvironmentAdapter.class)
	public Map<String, String> getValues()
	{
		return values;
	}

	public void setValues(Map<String, String> values)
	{
		this.values = values;
	}
}
