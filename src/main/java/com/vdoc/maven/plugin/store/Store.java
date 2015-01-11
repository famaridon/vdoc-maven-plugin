package com.vdoc.maven.plugin.store;

/**
 * Created by famaridon on 08/01/2015.
 */
public interface Store extends AutoCloseable {

    public void put();

    public void get();

}
