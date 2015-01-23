package com.vdoc.maven.plugin;

import com.vdoc.maven.plugin.beans.CompletedModule;
import com.vdoc.maven.plugin.enums.PackagingType;
import com.vdoc.maven.plugin.store.Store;
import com.vdoc.maven.plugin.store.impl.DevEngStore;
import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * this task is used to create a project setup.
 */
@Mojo(name = "create-setup", threadSafe = false, defaultPhase = LifecyclePhase.PACKAGE)
public class CreateSetupMojo extends AbstractVDocMojo {

    public static final String BASE_ZIP_FOLDER = "";
    public static final String SETUP_SUFFIX = "setup";

    /**
     * this is used to synchronize multiple modules build on multiple thread
     */
    private static BlockingQueue<CompletedModule> completedModules = new LinkedBlockingQueue<>();
    /**
     * this lock is used to avoid multiple includeOtherModules use.
     */
    private static Boolean completedModulesLock = Boolean.FALSE;

    /**
     * the project packaging type actually <b>APPS</b> is the only supported value.
     */
    @Parameter(defaultValue = "APPS")
    private PackagingType packagingType;
    /**
     * the apps file name without extension. The setup is suffixed with <b>-setup</b>.
     */
    @Parameter(required = true)
    private String setupName;

    /**
     * where found dependency jars
     */
    @Parameter(required = true, defaultValue = "${project.build.directory}/lib")
    private File libFolder;
    /**
     * if true test jar should be included in setup
     */
    @Parameter(defaultValue = "true")
    private boolean includeTest;
    /**
     * if true test javadoc should be included in setup
     */
    @Parameter(defaultValue = "false")
    private boolean includeJavadoc;
    /**
     * if true source jar should be included in setup
     */
    @Parameter(defaultValue = "false")
    private boolean includeSource;
    /**
     * other modules should be merged into this project setup
     */
    @Parameter(defaultValue = "false")
    private boolean includeOtherModules;
    /**
     * dependencies setup should be merged into this project setup
     */
    @Parameter(defaultValue = "true")
    private boolean includeDependenciesSetups;
    /**
     * dependencies setup should be merged into this project setup
     */
    @Parameter(defaultValue = "true")
    private List<String> dependenciesSetupsGroupIds;

    private Store store;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ("pom".equalsIgnoreCase(this.project.getPackaging())) {
            getLog().warn("This mojo can't work for pom packaging project!");
            return;
        }

        File createdSetup;
        try {

            switch (this.packagingType) {
                case APPS:
                    createdSetup = this.createAppsSetup();
                    break;
                case CUSTOM:
                    createdSetup = this.createCustomSetup();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported packaging type !");
            }

        } catch (IOException e) {
            throw new MojoFailureException("Zip File can't be build", e);
        }
        this.complete(createdSetup);
    }

    public File createAppsSetup() throws IOException, MojoExecutionException {

        getLog().info("Create the VDoc apps packaging Zip.");
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
                File testJar = this.getJarFile(buildDirectory, jarName, "test");
                if (testJar.exists()) {
                    this.compressDirectory(output, testJar, "lib/");
                } else {
                    getLog().warn("Test jar not found!");
                }
            }

            if (this.includeSource) {
                this.compressDirectory(output, this.getJarFile(buildDirectory, jarName, "source"), "lib/");
            }

            if (this.includeJavadoc) {
                this.compressDirectory(output, this.getJarFile(buildDirectory, jarName, "javadoc"), "lib/");
            }
        }


        getLog().info("create the meta setup zip with apps, documentation, fix, ...");
        File metaAppOutput = new File(buildDirectory, setupName + "-" + SETUP_SUFFIX + ".zip");
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

            // include linked apps
            if (includeDependenciesSetups) {
                getLog().warn("not implemented");
                this.store = new DevEngStore();
                this.includeDependenciesSetups(output);
            }


            // include vdoc fix
            File fix = new File(this.project.getBasedir(), "fix");
            if (fix.exists()) {
                getLog().debug("add fix folder");
                this.compressDirectory(output, fix, "fix/");
            }

            // include documentation
            File documentation = new File(this.project.getBasedir(), "documentation");
            if (documentation.exists()) {
                getLog().debug("add documentation folder");
                this.compressDirectory(output, documentation, "documentation/");
            }

            this.includeOtherModules(output);

        }

        return metaAppOutput;
    }

    protected void includeDependenciesSetups(ZipArchiveOutputStream output) throws IOException, MojoExecutionException {
        for (Dependency dependency : this.project.getDependencies()) {
            getLog().debug(dependency.toString());
            if (dependenciesSetupsGroupIds.contains(dependency.getGroupId())) {
                getLog().info("Search setup for : " + dependency.toString());
                try (InputStream inputStream = store.get(dependency)) {
                    if (inputStream == null) {
                        getLog().warn("No setup found for : " + dependency.toString());

                    } else {
                        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                             ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(bufferedInputStream);) {

                            this.mergeArchive(output, input);

                        } catch (ArchiveException e) {
                            throw new MojoExecutionException("Can't read setup for : " + dependency, e);
                        }
                    }
                }
            }
        }
    }

    /**
     * wait for others modules and merge it into the current module setup
     *
     * @param output
     * @throws MojoExecutionException
     * @throws IOException
     */
    protected void includeOtherModules(ZipArchiveOutputStream output) throws MojoExecutionException, IOException {
        // include other modules
        if (this.includeOtherModules) {

            getLog().info("Join for other modules");
            // only 1 module can join others tack lock
            synchronized (completedModulesLock) {
                if (completedModulesLock == Boolean.TRUE) {
                    throw new MojoExecutionException("Too many project use includeOtherModules = true");
                }
                completedModulesLock = Boolean.TRUE;
            }

            // join other modules if multi-thread else it should be the last compiled module.
            int modulesCount = this.getProject().getParent().getModules().size() - 1;
            do {
                try {
                    CompletedModule completedModule = completedModules.take();
                    getLog().info("Join module " + completedModule.getArtifactId() + " merge setup file " + completedModule.getSetup().getName());

                    if (completedModule.getSetup() == null) {
                        getLog().warn(completedModule.getArtifactId() + " have fail!");
                        throw new MojoExecutionException(completedModule.getArtifactId() + " have fail!");
                    }

                    try (FileInputStream fileInputStream = new FileInputStream(completedModule.getSetup());
                         BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                         ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(bufferedInputStream);) {

                        this.mergeArchive(output, input);

                    } catch (ArchiveException e) {
                        throw new MojoExecutionException("Can't read module '" + completedModule.getArtifactId() + "' setup file '" + completedModule.getArtifactId() + "'!", e);
                    }

                } catch (InterruptedException e) {
                    throw new MojoExecutionException("Waiting for other module fail!", e);
                }

                modulesCount--;
            } while (modulesCount > 0);
        }
    }

    /**
     * copy any entry of the <b>from</b> archive into the <b>to</b>.
     *
     * @param to   the output archive
     * @param from the source archive
     * @throws IOException
     * @throws MojoExecutionException
     */
    protected void mergeArchive(ArchiveOutputStream to, ArchiveInputStream from) throws IOException, MojoExecutionException {

        long offset = 0l;
        ArchiveEntry entry;
        while ((entry = from.getNextEntry()) != null) {
            if (!to.canWriteEntryData(entry)) {
                throw new MojoExecutionException("Can't merge setup files!");
            }
            getLog().debug("merge entry : " + entry.getName());
            to.putArchiveEntry(entry);
            IOUtils.copyLarge(from, to, offset, entry.getSize());
            to.closeArchiveEntry();
            offset += entry.getSize();
        }


    }

    /**
     * compress a directory to an archive
     *
     * @param outputStream the archive stream
     * @param directory    the source directory
     * @param base         the zip base directory
     * @throws IOException
     */
    protected void compressDirectory(ArchiveOutputStream outputStream, File directory, String base) throws IOException {

        Validate.notNull(outputStream);
        Validate.notNull(directory);
        Validate.notNull(base);

        if (!directory.exists()) {
            throw new IllegalArgumentException("directory '" + directory.getPath() + "' to compress not found!");
        }

        // we must remove first / for base archive entry else we get a blank directory.
        if (base.startsWith("/")) {
            base = StringUtils.substring(base, 1);
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


    public File createCustomSetup() {
        throw new NotImplementedException("Currently not implemented!");
    }

    /**
     * flag this module as completed
     *
     * @param setup
     * @throws MojoExecutionException
     */
    private void complete(File setup) throws MojoExecutionException {
        synchronized (completedModules) {
            try {
                completedModules.put(new CompletedModule(this.getProject().getArtifactId(), setup));
            } catch (InterruptedException e) {
                throw new MojoExecutionException("Can't complete this module!", e);
            }
        }
    }

    public PackagingType getPackagingType() {
        return packagingType;
    }

    public void setPackagingType(PackagingType packagingType) {
        this.packagingType = packagingType;
    }

    public String getSetupName() {
        return setupName;
    }

    public void setSetupName(String setupName) {
        this.setupName = setupName;
    }

    public File getLibFolder() {
        return libFolder;
    }

    public void setLibFolder(File libFolder) {
        this.libFolder = libFolder;
    }

    public boolean isIncludeTest() {
        return includeTest;
    }

    public void setIncludeTest(boolean includeTest) {
        this.includeTest = includeTest;
    }

    public boolean isIncludeJavadoc() {
        return includeJavadoc;
    }

    public void setIncludeJavadoc(boolean includeJavadoc) {
        this.includeJavadoc = includeJavadoc;
    }

    public boolean isIncludeSource() {
        return includeSource;
    }

    public void setIncludeSource(boolean includeSource) {
        this.includeSource = includeSource;
    }

    public boolean isIncludeOtherModules() {
        return includeOtherModules;
    }

    public void setIncludeOtherModules(boolean includeOtherModules) {
        this.includeOtherModules = includeOtherModules;
    }

    public boolean isIncludeDependenciesSetups() {
        return includeDependenciesSetups;
    }

    public void setIncludeDependenciesSetups(boolean includeDependenciesSetups) {
        this.includeDependenciesSetups = includeDependenciesSetups;
    }

    public List<String> getDependenciesSetupsGroupIds() {
        return dependenciesSetupsGroupIds;
    }

    public void setDependenciesSetupsGroupIds(List<String> dependenciesSetupsGroupIds) {
        this.dependenciesSetupsGroupIds = dependenciesSetupsGroupIds;
    }
}
