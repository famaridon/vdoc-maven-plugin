package com.vdoc.maven.plugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

import java.io.File;

/**
 * Created by famaridon on 19/05/2014.
 */
public abstract class AbstractVDocMojo extends AbstractMojo
{

	/**
	 * The Maven project.
	 */
	@Parameter(defaultValue="${project}", required = true,readonly = true)
	protected MavenProject project;

	@Parameter(defaultValue="${session}", required = true,readonly = true)
	protected MavenSession session;

	@Parameter
	private XmlPlexusConfiguration configuration;

	@Component
	protected BuildPluginManager pluginManager;

	/**
	 * Directory containing the generated JAR.
	 */
	@Parameter(defaultValue="${project.build.directory}",required = true)
	protected File outputDirectory;

	/**
	 * Name of the generated JAR.
	 */
	@Parameter(alias="jarName", defaultValue="${project.build.directory}${project.build.finalName}.jar", required = true)
	protected File finalName;

}
