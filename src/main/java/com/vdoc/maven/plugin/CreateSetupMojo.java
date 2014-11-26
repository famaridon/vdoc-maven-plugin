package com.vdoc.maven.plugin;

import com.vdoc.maven.plugin.enums.PackagingType;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * this task is used to create a project setup.
 * Created by famaridon on 19/05/2014.
 */
@Mojo(name = "create-setup", threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class CreateSetupMojo extends AbstractVDocMojo {

	public static final String BASE_ZIP_FOLDER = "";
	/**
	 * Name of the generated JAR.
	 */
	@Parameter(defaultValue = "APPS")
	private PackagingType packagingType;

	@Parameter(required = true)
	private String setupName;
	@Parameter(required = true, defaultValue = "${project.build.directory}/lib")
	private File libFolder;
	@Parameter(defaultValue = "false")
	private boolean includeTest;
	@Parameter(defaultValue = "false")
	private boolean includeJavadoc;
	@Parameter(defaultValue = "false")
	private boolean includeSource;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			switch (this.packagingType) {
				case APPS:
					this.createAppsSetup();
					break;
				case CUSTOM:
					this.createCustomSetup();
					break;
				default:
					throw new IllegalArgumentException("Unsupported packaging type !");
			}

		} catch (IOException e) {
			throw new MojoFailureException("Zip File can't be build", e);
		}

	}

	public File createAppsSetup() throws IOException {

		getLog().info("create the VDoc apps packaging Zip.");
		File vdocAppOutput = new File(buildDirectory, setupName + ".zip");
		try (ZipArchiveOutputStream output = new ZipArchiveOutputStream(vdocAppOutput);) {
			for (Resource r : this.project.getResources()) {
				File customFolder = new File(r.getDirectory() + "/../custom/");
				if (customFolder.isDirectory()) {
					File[] customFolders = new File(r.getDirectory() + "/../custom/").listFiles((FileFilter) DirectoryFileFilter.INSTANCE);
					for (File f : customFolders) {
						this.compressDirectory(output, f, BASE_ZIP_FOLDER);
					}
				}
			}
			this.compressDirectory(output, this.getJarFile(buildDirectory, jarName, null), "lib/");
			if (this.libFolder.exists()) {
				this.compressDirectory(output, libFolder, BASE_ZIP_FOLDER);
			}

			if (this.includeTest) {
				this.compressDirectory(output, this.getJarFile(buildDirectory, jarName, "test"), "lib/");
			}

			if (this.includeSource) {
				this.compressDirectory(output, this.getJarFile(buildDirectory, jarName, "source"), "lib/");
			}

			if (this.includeJavadoc) {
				this.compressDirectory(output, this.getJarFile(buildDirectory, jarName, "javadoc"), "lib/");
			}
		}

		getLog().info("create the meta setup zip with apps, documentation, fix, ...");
		File metaAppOutput = new File(buildDirectory, setupName + "-" + this.packagingType.toString().toLowerCase() + ".zip");
		try (ZipArchiveOutputStream output = new ZipArchiveOutputStream(metaAppOutput);) {
			for (Resource r : this.project.getResources()) {
				File userAppsCustomFolder = new File(r.getDirectory() + "/../user_apps_custom/");
				if (userAppsCustomFolder.isDirectory()) {
					File[] customFolders = new File(r.getDirectory() + "/../user_apps_custom/").listFiles((FileFilter) DirectoryFileFilter.INSTANCE);
					for (File f : customFolders) {
						this.compressDirectory(output, f, "custom/");
					}
				}
			}
			this.compressDirectory(output, vdocAppOutput, "apps/");

			File fix = new File(this.project.getBasedir(), "fix");
			if (fix.exists()) {
				getLog().debug("add fix folder");
				this.compressDirectory(output, fix, "fix/");
			}

			File documentation = new File(this.project.getBasedir(), "documentation");
			if (documentation.exists()) {
				getLog().debug("add documentation folder");
				this.compressDirectory(output, documentation, "documentation/");
			}
		}

		return metaAppOutput;
	}

	public File createCustomSetup() {
		throw new NotImplementedException("Currently not implemented!");
	}

	private void compressDirectory(ArchiveOutputStream outputStream, File directory, String base) throws IOException {

		if (!directory.exists()) {
			throw new IllegalArgumentException("directory '" + directory.getPath() + "' to compress not found!");
		}
		String entryName = base + directory.getName();
		getLog().debug("Add Zip entry : " + entryName);
		ArchiveEntry tarEntry = outputStream.createArchiveEntry(directory, entryName);
		outputStream.putArchiveEntry(tarEntry);

		if (directory.isFile()) {
			try (FileInputStream fis = new FileInputStream(directory)) {
				IOUtils.copy(fis, outputStream);
			}
			outputStream.closeArchiveEntry();
		} else {
			outputStream.closeArchiveEntry();
			File[] children = directory.listFiles();
			if (children != null) {
				for (File child : children) {
					compressDirectory(outputStream, child, entryName + "/");
				}
			}
		}
	}


}
