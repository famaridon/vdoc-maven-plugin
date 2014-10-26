package com.vdoc.maven.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * this task is used to deploy a project to the target vdoc install.
 * Created by famaridon on 19/05/2014.
 */
@Mojo(name = "hard-deploy", threadSafe = true)
public class HardDeployMojo extends AbstractMojo
{
	private static final String WILDCARD_WEB_APP = FilenameUtils.separatorsToSystem("*custom/webapp*");
	public FileFilter notWebAppFolderFileFilter;

	/**
	 * The Maven project.
	 */
	@Parameter(defaultValue = "${project}", required = true)
	protected MavenProject project;

	@Parameter(defaultValue = "${session}", required = true)
	protected MavenSession session;

	@Parameter(property = "vdoc.home", required = true, alias = "vdocHome")
	protected File vdocHome;

	protected File vdocEAR;

	@Parameter(property = "with.custom", required = true, defaultValue = "true", alias = "withCustom")
	protected boolean withCustom;

	@Parameter(property = "include.test", required = false, defaultValue = "true", alias = "includeTest")
	protected boolean includeTest;

	@Parameter(property = "include.source", required = false, defaultValue = "false", alias = "includeSource")
	protected boolean includeSource;

	/**
	 * Directory containing the generated JAR.
	 */
	@Parameter(defaultValue = "${project.build.directory}", required = true)
	private File sourceCustom;
	/**
	 * Directory containing the generated JAR.
	 */
	@Parameter(defaultValue = "${project.build.directory}", required = true)
	private File jarDirectory;

	/**
	 * Name of the generated JAR.
	 */
	@Parameter(alias = "jarName", property = "jar.finalName", defaultValue = "${project.build.finalName}")
	private String jarName;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		initFileFilters();

		this.vdocEAR = new File(this.vdocHome, "/JBoss/server/all/deploy/vdoc.ear/");
		File jar = getJarFile(jarDirectory, jarName, null);
		try
		{
			// copy jars
			if ( jar.exists() )
			{
				File libDirectory = new File(vdocEAR, "lib");
				getLog().info(String.format("Copy %1$s to %2$s", jar.getAbsolutePath(), libDirectory.getAbsolutePath()));
				FileUtils.copyFileToDirectory(jar, libDirectory);
				if ( this.includeTest )
				{
					File testJar = getJarFile(jarDirectory, jarName, "test");
					getLog().info(String.format("Copy test jar %1$s to %2$s", testJar.getAbsolutePath(), libDirectory.getAbsolutePath()));
					FileUtils.copyFileToDirectory(testJar, libDirectory);
				}
				if ( this.includeSource )
				{
					File sourceJar = getJarFile(jarDirectory, jarName, "source");
					getLog().info(String.format("Copy source jar %1$s to %2$s", sourceJar.getAbsolutePath(), libDirectory.getAbsolutePath()));
					FileUtils.copyFileToDirectory(sourceJar, libDirectory);
				}
			}

			// copy custom
			File targetCustomFolder = new File(vdocEAR, "vdoc.war/WEB-INF/storage/custom/");
			File targetWebappFolder = new File(vdocEAR, "vdoc.war/");
			for (String sourceRootPath : this.project.getCompileSourceRoots())
			{
				File sourceRoot = new File(sourceRootPath);
				File customFolder = new File(sourceRoot.getParentFile(), "custom");
				File customWebappFolder = new File(customFolder, "webapp");

				if ( customFolder.exists() )
				{
					getLog().info(String.format("Copy custom %1$s to %2$s", customFolder.getAbsolutePath(), targetCustomFolder.getAbsolutePath()));
					FileUtils.copyDirectory(customFolder, targetCustomFolder, notWebAppFolderFileFilter);
					getLog().info(String.format("Copy webapp %1$s to %2$s", customWebappFolder.getAbsolutePath(), targetWebappFolder.getAbsolutePath()));
					FileUtils.copyDirectory(customWebappFolder, targetWebappFolder);
				}
			}


		} catch (IOException e)
		{
			throw new MojoFailureException("Copy fail :", e);
		}

	}

	protected void initFileFilters()
	{

		notWebAppFolderFileFilter = new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				return !FilenameUtils.wildcardMatch(file.getAbsolutePath(), WILDCARD_WEB_APP, IOCase.INSENSITIVE);
			}
		};
	}

	/**
	 * clone from maven jar plugin.
	 * <p/>
	 * svn : http://svn.apache.org/repos/asf/maven/plugins/tags/maven-jar-plugin-2.5
	 *
	 * @param basedir
	 * @param finalName
	 * @param classifier
	 * @return
	 */
	protected static File getJarFile(File basedir, String finalName, String classifier)
	{
		if ( classifier == null )
		{
			classifier = "";
		} else if ( classifier.trim().length() > 0 && !classifier.startsWith("-") )
		{
			classifier = "-" + classifier;
		}

		return new File(basedir, finalName + classifier + ".jar");
	}

}
