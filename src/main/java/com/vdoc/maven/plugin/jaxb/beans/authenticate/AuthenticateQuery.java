package com.vdoc.maven.plugin.jaxb.beans.authenticate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by famaridon on 19/05/2014.
 */
@XmlRootElement(name = "authenticate")
public class AuthenticateQuery
{
	protected  Header header = new Header();
	protected Body body;

	public AuthenticateQuery()
	{
	}

	@XmlElement
	public Header getHeader()
	{
		return header;
	}

	public void setHeader(Header header)
	{
		this.header = header;
	}

	@XmlElement
	public Body getBody()
	{
		return body;
	}

	public void setBody(Body body)
	{
		this.body = body;
	}
}
