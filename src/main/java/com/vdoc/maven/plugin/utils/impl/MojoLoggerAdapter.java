package com.vdoc.maven.plugin.utils.impl;

import com.vdoc.maven.plugin.utils.GobblerAdapter;
import org.apache.maven.plugin.logging.Log;

/**
 * Created by famaridon on 06/07/2014.
 */
public class MojoLoggerAdapter implements GobblerAdapter
{
	protected Log log;
	protected String level;

	public MojoLoggerAdapter(Log log)
	{
		this(log,"debug");
	}

	public MojoLoggerAdapter(Log log, String level)
	{
		this.log = log;
		this.level =level;
	}

	@Override
	public void println(String message)
	{
		switch (level)
		{
			case "debug":
				log.debug(message);
				break;
			case "info":
				log.info(message);
				break;
			case "error":
				log.error(message);
				break;
			default:
				log.debug(message);
		}

	}
}
