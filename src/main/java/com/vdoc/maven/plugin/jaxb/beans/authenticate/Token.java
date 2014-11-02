package com.vdoc.maven.plugin.jaxb.beans.authenticate;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by famaridon on 20/05/2014.
 */
public class Token {
	protected String key;

	@XmlAttribute
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
