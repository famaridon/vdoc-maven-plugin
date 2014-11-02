package com.vdoc.maven.plugin;

import com.vdoc.maven.plugin.enums.PackagingType;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * this task is used to create a project setup.
 * Created by famaridon on 19/05/2014.
 */
@Mojo(name = "create-setup", threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class CreateSetupMojo extends AbstractVDocMojo {

	/**
	 * Name of the generated JAR.
	 */
	@Parameter(alias = "packaging.type", defaultValue = "APPS")
	private PackagingType packagingType;

	@Parameter(alias = "setup.name", required = true)
	private String setupName;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {


	}

	public File createAppsSetup() {
		return null;
	}

	public File createCustomSetup() {
		return null;
	}

}
