package com.vdoc.maven.plugin.setup;

/**
 * Created by famaridon on 04/02/15.
 */
public interface AppsSetup {
    public void appendApps(AppsPackaging packaging);

    public void appendUserAppsCustom(AppsPackaging packaging);

    public void appendFix(AppsPackaging packaging);

    public void appendDocumentation(AppsPackaging packaging);
}
