package com.vdoc.maven.plugin.beans;

import java.io.File;

/**
 * Created by famaridon on 06/01/2015.
 */
public class CompletedModule {
	private final String artifactId;
	private final File setup;

	public CompletedModule(String artifactId, File setup) {
		this.artifactId = artifactId;
		this.setup = setup;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public File getSetup() {
		return setup;
	}
}
