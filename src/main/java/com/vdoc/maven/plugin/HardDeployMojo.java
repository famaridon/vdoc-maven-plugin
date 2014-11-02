package com.vdoc.maven.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * this task is used to deploy a project to the target vdoc install.
 * Created by famaridon on 19/05/2014.
 */
@Mojo(name = "hard-deploy", threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class HardDeployMojo extends AbstractVDocMojo {

	protected File vdocEAR;

	/**
	 * custom folder must be updated
	 */
	@Parameter(property = "with.custom", required = true, defaultValue = "true", alias = "withCustom")
	protected boolean withCustom;

	/**
	 * test jar must be deployed
	 */
	@Parameter(property = "include.test", required = false, defaultValue = "true", alias = "includeTest")
	protected boolean includeTest;

	/**
	 * source jar must be deployed
	 */
	@Parameter(property = "include.source", required = false, defaultValue = "false", alias = "includeSource")
	protected boolean includeSource;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		this.vdocEAR = new File(this.vdocHome, "/JBoss/server/all/deploy/vdoc.ear/");
		File jar = getJarFile(jarDirectory, jarName, null);
		try {
			// copy jars
			if (jar.exists()) {

				File libDirectory = new File(vdocEAR, "lib");
				getLog().info(String.format("Copy %1$s to %2$s", jar.getAbsolutePath(), libDirectory.getAbsolutePath()));
				FileUtils.copyFileToDirectory(jar, libDirectory);

				if (this.includeTest) {

					File testJar = getJarFile(jarDirectory, jarName, "test");
					getLog().info(String.format("Copy test jar %1$s to %2$s", testJar.getAbsolutePath(), libDirectory.getAbsolutePath()));
					FileUtils.copyFileToDirectory(testJar, libDirectory);
				}

				if (this.includeSource) {

					File sourceJar = getJarFile(jarDirectory, jarName, "source");
					getLog().info(String.format("Copy source jar %1$s to %2$s", sourceJar.getAbsolutePath(), libDirectory.getAbsolutePath()));
					FileUtils.copyFileToDirectory(sourceJar, libDirectory);

				}
			}

			// copy custom
			File targetCustomFolder = new File(vdocEAR, "vdoc.war/WEB-INF/storage/custom/");
			File targetWebappFolder = new File(vdocEAR, "vdoc.war/");
			for (String sourceRootPath : this.project.getCompileSourceRoots()) {

				File sourceRoot = new File(sourceRootPath);
				File customFolder = new File(sourceRoot.getParentFile(), "custom");
				File customWebappFolder = new File(customFolder, "webapp");

				if (customFolder.exists()) {
					getLog().info(String.format("Copy custom %1$s to %2$s", customFolder.getAbsolutePath(), targetCustomFolder.getAbsolutePath()));
					FileUtils.copyDirectory(customFolder, targetCustomFolder, notWebAppFolderFileFilter);
					getLog().info(String.format("Copy webapp %1$s to %2$s", customWebappFolder.getAbsolutePath(), targetWebappFolder.getAbsolutePath()));
					FileUtils.copyDirectory(customWebappFolder, targetWebappFolder);
				}
			}


		} catch (IOException e) {
			throw new MojoFailureException("Deploy fail :", e);
		}

	}

}
