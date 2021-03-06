package com.vdoc.maven.plugin;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by famaridon on 19/05/2014.
 */
public abstract class AbstractVDocMojo extends AbstractMojo {

	private static final String WILDCARD_WEB_APP = FilenameUtils.separatorsToSystem("*custom/webapp*");

	/**
	 * a {@Link FileFilter} to select all file in custom directory but not webapp sub folder
	 */
	public final FileFilter notWebAppFolderFileFilter;

	/**
	 * The Maven project.
	 */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	protected MavenProject project;
	/**
	 * The Maven session.
	 */
	@Parameter(defaultValue = "${session}", required = true, readonly = true)
	protected MavenSession session;

	/**
	 * Name of the generated JAR.
	 */
	@Parameter(defaultValue = "${project.build.finalName}")
	protected String jarName;
	/**
	 * Directory containing the generated JAR.
	 */
	@Parameter(defaultValue = "${project.build.directory}", required = true)
	protected File buildDirectory;

	protected AbstractVDocMojo() {
        super();
        notWebAppFolderFileFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
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
	protected static File getJarFile(File basedir, String finalName, String classifier) {
		if (classifier == null) {
			classifier = "";
		} else if (classifier.trim().length() > 0 && !classifier.startsWith("-")) {
			classifier = "-" + classifier;
		}

		return new File(basedir, finalName + classifier + ".jar");
	}

	public MavenProject getProject() {
		return project;
	}

	public MavenSession getSession() {
		return session;
	}

	public File getBuildDirectory() {
		return buildDirectory;
	}

	public String getJarName() {
		return jarName;
	}

}
