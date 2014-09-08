package com.vdoc.maven.plugin.jaxb.beans.authenticate;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by famaridon on 19/05/2014.
 */
public class Header
{
	protected String login;
	protected String password;
	protected Integer timeout;

	@XmlAttribute
	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	@XmlAttribute
	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	@XmlAttribute
	public Integer getTimeout()
	{
		return timeout;
	}

	public void setTimeout(Integer timeout)
	{
		this.timeout = timeout;
	}
}
