package com.vdoc.maven.plugin.store;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;

import java.io.InputStream;

/**
 * Created by famaridon on 08/01/2015.
 */
public interface Store extends AutoCloseable {

    public void put(InputStream uploadedInputStream, String filename, Artifact artifact);

    public InputStream get(Dependency dependency);

}
