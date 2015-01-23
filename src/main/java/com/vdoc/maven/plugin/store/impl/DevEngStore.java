package com.vdoc.maven.plugin.store.impl;

import com.vdoc.maven.plugin.store.Store;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;

import java.io.InputStream;

/**
 * Created by famaridon on 08/01/2015.
 */
public class DevEngStore implements Store {


    @Override
    public void close() throws Exception {

    }

    @Override
    public void put(InputStream uploadedInputStream, String filename, Artifact artifact) {

    }

    @Override
    public InputStream get(Dependency dependency) {
        return null;
    }
}
