package com.vdoc.maven.plugin;

import com.vdoc.maven.plugin.beans.DeployFileConfiguration;
import com.vdoc.maven.plugin.utils.OSUtils;
import com.vdoc.maven.plugin.utils.StreamGobbler;
import com.vdoc.maven.plugin.utils.impl.MojoLoggerAdapter;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * this task can deploy all VDoc jars into a repository
 */
@Mojo(name = "deploy-vdoc", threadSafe = false, requiresProject = false, requiresDirectInvocation = true)
public class DeployVDocMojo extends AbstractVDocMojo {

	/**
	 * the current running plugin description
	 */
	protected PluginDescriptor pluginDescriptor;

	/**
	 * the VDoc ear folder (prefer the configurator's ear to avoid upload not needed jar)
	 */
	@Parameter(property = "earFolder", required = true)
	protected File earFolder;

	/**
	 * the VDoc upload version
	 */
	@Parameter(property = "targetVersion", required = true)
	protected String targetVersion;

	/**
	 * the VDoc group id
	 */
	@Parameter(property = "targetGroupId", required = true, defaultValue = "com.vdoc.engineering")
	protected String targetGroupId;

	/**
	 * the repository id used if password is needed for upload
	 */
	@Parameter(property = "repositoryId", required = true)
	protected String repositoryId;

	/**
	 * the repository url
	 */
	@Parameter(property = "repositoryUrl", required = true)
	protected String repositoryUrl;

	/**
	 * snapshot must used unique version
	 */
	@Parameter(property = "uniqueVersion", required = false, defaultValue = "true")
	protected boolean uniqueVersion;

	/**
	 * the maven home folder
	 */
	@Parameter(property = "mavenHome", required = false, defaultValue = "${env.M2_HOME}")
	protected File mavenHome;

	/**
	 * set to true for only deploy parents pom
	 */
	@Parameter(property = "onlyParentPom", required = false, defaultValue = "false")
	protected boolean onlyParentPom;

	protected List<DeployFileConfiguration> dependencies = new ArrayList<>();

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		this.pluginDescriptor = ((PluginDescriptor) getPluginContext().get("pluginDescriptor"));

		if (this.mavenHome == null )
		{
			String mavenEnv = System.getenv("M2_HOME");
			Validate.notEmpty(mavenEnv, "M2_HOME is not set you can used the maven-home configuration!");
			mavenHome = new File(mavenEnv);
		}

		if (!mavenHome.exists() )
		{
			throw new IllegalArgumentException("maven home (M2_HOME or maven-home configuration) is set to bad location : " + mavenHome.getAbsolutePath());
		}

		OrFileFilter prefixFileFilter = new OrFileFilter();
		prefixFileFilter.addFileFilter(new PrefixFileFilter("VDoc"));
		prefixFileFilter.addFileFilter(new PrefixFileFilter("VDP"));

		AndFileFilter fileFilter = new AndFileFilter();
		fileFilter.addFileFilter(prefixFileFilter);
		fileFilter.addFileFilter(new SuffixFileFilter(".jar"));

		File[] earFiles = earFolder.listFiles((FileFilter) fileFilter);
		getLog().info("Scan the vdoc.ear folder");
		deployFiles(earFiles);
		getLog().info("Scan the vdoc.ear/lib folder");
		File[] earLibFiles = new File(earFolder, "lib").listFiles((FileFilter) fileFilter);
		deployFiles(earLibFiles);

		buildParentPom("sdk");
		buildParentPom("sdk.advanced");

	}

	/**
	 * used to build a parent pom from ftl file
	 * @param artifactId
	 * @throws MojoExecutionException
	 */
	protected void buildParentPom(String artifactId) throws MojoExecutionException {
		getLog().info("Create the " + artifactId + " pom file");
		try
		{
			// build the full pom
			Configuration cfg = new Configuration();

			// Specify the data source where the template files come from. Here I set a
			// plain directory for it, but non-file-system are possible too:
			cfg.setDirectoryForTemplateLoading(mavenHome);

			// Specify how templates will see the data-model. This is an advanced topic...
			// for now just use this:
			cfg.setObjectWrapper(new BeansWrapper());

			// Set your preferred charset template files are stored in. UTF-8 is
			// a good choice in most applications:
			cfg.setDefaultEncoding("UTF-8");

			// Sets how errors will appear. Here we assume we are developing HTML pages.
			// For production systems TemplateExceptionHandler.RETHROW_HANDLER is better.
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

			// At least in new projects, specify that you want the fixes that aren't
			// 100% backward compatible too (these are very low-risk changes as far as the
			// 1st and 2nd version number remains):
			cfg.setIncompatibleImprovements(new Version(2, 3, 20));  // FreeMarker 2.3.20

			// copy the template from jar freemaker can't read stream
			getLog().debug("get the sdk template localy.");
			String ftlName = artifactId + ".ftl";
			try (
					InputStream input = getClass().getClassLoader().getResourceAsStream("pom/" + ftlName);
                    FileOutputStream outputStream = new FileOutputStream(new File(this.mavenHome, ftlName))
            ) {
				IOUtils.copy(input, outputStream);
				outputStream.flush();
			}

			getLog().debug("Parse the  ftl.");
			Template temp = cfg.getTemplate(ftlName);

			File pom = new File(this.mavenHome, "pom.xml");
            try (Writer out = new FileWriter(pom)) {
                temp.process(this, out);
				out.flush();
			}

			DeployFileConfiguration deployFileConfiguration = new DeployFileConfiguration(pom, repositoryId);
			deployFileConfiguration.setArtifactId(artifactId);
			deployFileConfiguration.setGroupId("com.vdoc.engineering");
			deployFileConfiguration.setVersion(targetVersion);
			deployFileConfiguration.setUniqueVersion(uniqueVersion);
			deployFileConfiguration.setUrl(repositoryUrl);
			deployFileConfiguration.setPackaging("pom");
			this.deployFileToNexus(deployFileConfiguration);

			pom.renameTo(new File(this.mavenHome, artifactId + ".xml"));


		} catch (IOException | TemplateException e)
		{
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	protected void deployFiles(File[] vdocFiles) throws MojoExecutionException {
		if (vdocFiles == null )
		{
			return;
		}
		for (File jar : vdocFiles)
		{
			getLog().debug("parsing file : " + jar.getName());
			DeployFileConfiguration deployFileConfiguration = new DeployFileConfiguration(jar, repositoryId);
			deployFileConfiguration.setArtifactId(StringUtils.substringBefore(FilenameUtils.getBaseName(jar.getName()), "-suite"));
			deployFileConfiguration.setGroupId(targetGroupId);
			deployFileConfiguration.setVersion(targetVersion);
			deployFileConfiguration.setUniqueVersion(uniqueVersion);
			deployFileConfiguration.setUrl(repositoryUrl);

			getLog().debug("search javadoc");
			try
			{
				this.splitJar(deployFileConfiguration, jar);
				dependencies.add(deployFileConfiguration);
				if (!this.onlyParentPom) {
					deployFileToNexus(deployFileConfiguration);
				}

			} finally
			{
				getLog().debug("delete javadoc jar");
				if (deployFileConfiguration.getJavadoc() != null)
				{
					deployFileConfiguration.getJavadoc().delete();
				}

				if (deployFileConfiguration.getSources() != null)
				{
					deployFileConfiguration.getSources().delete();
				}
			}
		}
	}

	/**
	 * // org.apache.maven.plugins:maven-deploy-plugin:2.8.1:deploy-file
	 *
	 * @param deployFileConfiguration
	 * @throws MojoExecutionException
	 */
	protected void deployFileToNexus(DeployFileConfiguration deployFileConfiguration) throws MojoExecutionException
	{
		System.out.println(deployFileConfiguration.toCmd());
		try
		{
			List<String> cmd = deployFileConfiguration.toCmd();
			cmd.add(0, "deploy:deploy-file");
			cmd.add(0, "-X");
			cmd.add(0, new File(this.mavenHome, "/bin/mvn" + (OSUtils.isWindows() ? ".bat" : "")).getAbsolutePath());

			getLog().info(cmd.toString());

			ProcessBuilder builder = new ProcessBuilder(cmd);
			Process process = builder.start();

			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), new MojoLoggerAdapter(this.getLog(), "error"), deployFileConfiguration.getArtifactId());

			StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), new MojoLoggerAdapter(this.getLog(), "info"), deployFileConfiguration.getArtifactId());

			// start gobblers
			outputGobbler.start();
			errorGobbler.start();

			int code = process.waitFor();
			if (code != 0)
			{
				throw new MojoExecutionException("" + deployFileConfiguration.toCmd().toString());
			}

		} catch (IOException | InterruptedException e)
		{
			throw new MojoExecutionException(e.getMessage());
		}
	}

	protected void copyEntry(JarInputStream jarInputStream, JarOutputStream javadocOutputStream, ZipEntry archiveEntry, ZipEntry newEntry) throws IOException
	{
		javadocOutputStream.putNextEntry(newEntry);
		if (archiveEntry.getSize() > 0 )
		{
			newEntry.setSize(archiveEntry.getSize());
			byte[] bytes = new byte[2048];
			int read = 0;
			while ((read += jarInputStream.read(bytes, 0, nextRead(archiveEntry.getSize(), read, bytes.length))) < archiveEntry.getSize())
			{
				javadocOutputStream.write(bytes);
			}
		}
		javadocOutputStream.closeEntry();
	}

	protected int nextRead(long total, int read, int buffer)
	{
		int next = (int) total - read;
		return next > buffer ? buffer : next;
	}

	/**
	 * split jar into source and javadoc jar
	 * @param deployFileConfiguration
	 * @param jar
	 * @throws MojoExecutionException
	 */
	protected void splitJar(DeployFileConfiguration deployFileConfiguration, File jar) throws MojoExecutionException
	{
		File javadoc = new File(jar.getParentFile(), FilenameUtils.getBaseName(jar.getName()) + "-javadoc.jar");
		File source = new File(jar.getParentFile(), FilenameUtils.getBaseName(jar.getName()) + "-source.jar");

		try (FileInputStream jarFileInputStream = new FileInputStream(jar);
			 JarInputStream jarInputStream = new JarInputStream(jarFileInputStream);

			 FileOutputStream javadocFileOutputStream = new FileOutputStream(javadoc);
			 JarOutputStream javadocOutputStream = new JarOutputStream(javadocFileOutputStream);

			 FileOutputStream sourceFileOutputStream = new FileOutputStream(source);
             JarOutputStream sourceOutputStream = new JarOutputStream(sourceFileOutputStream)
        ) {

			ZipEntry archiveEntry;
			while ((archiveEntry = jarInputStream.getNextEntry()) != null) {
				if (archiveEntry.getName().startsWith("apidocs/") && !archiveEntry.getName().equals("apidocs/"))
				{
					getLog().debug("javadoc : " + archiveEntry.getName());

					ZipEntry newEntry = new ZipEntry(StringUtils.substringAfter(archiveEntry.getName(), "apidocs/"));
					copyEntry(jarInputStream, javadocOutputStream, archiveEntry, newEntry);
				} else if (archiveEntry.getName().endsWith(".java"))
				{
					getLog().debug("source : " + archiveEntry.getName());

					ZipEntry newEntry = new ZipEntry(archiveEntry.getName());
					copyEntry(jarInputStream, sourceOutputStream, archiveEntry, newEntry);
				} else
				{
					getLog().debug("class : " + archiveEntry.getName());
					if (archiveEntry.getSize() > 0) {
                        if (archiveEntry.getSize() != jarInputStream.skip(archiveEntry.getSize())) {
                            throw new IllegalStateException("the archive reader cursore have not skip the right number of byte!");
                        }
                    }
				}
			}

		} catch (IOException e)
		{
			throw new MojoExecutionException(e.getMessage(), e);
		}

		if (javadoc.exists() )
		{
			deployFileConfiguration.setJavadoc(javadoc);
		}
		if (source.exists() )
		{
			deployFileConfiguration.setSources(source);
		}
	}

	public MavenProject getProject()
	{
		return project;
	}

	public void setProject(MavenProject project)
	{
		this.project = project;
	}

	public MavenSession getSession()
	{
		return session;
	}

	public void setSession(MavenSession session)
	{
		this.session = session;
	}

	public File getEarFolder()
	{
		return earFolder;
	}

	public void setEarFolder(File earFolder)
	{
		this.earFolder = earFolder;
	}

	public String getTargetVersion()
	{
		return targetVersion;
	}

	public void setTargetVersion(String targetVersion)
	{
		this.targetVersion = targetVersion;
	}

	public String getTargetGroupId()
	{
		return targetGroupId;
	}

	public void setTargetGroupId(String targetGroupId)
	{
		this.targetGroupId = targetGroupId;
	}

	public String getRepositoryId()
	{
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	public boolean isUniqueVersion()
	{
		return uniqueVersion;
	}

	public void setUniqueVersion(boolean uniqueVersion)
	{
		this.uniqueVersion = uniqueVersion;
	}

	public String getRepositoryUrl()
	{
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl)
	{
		this.repositoryUrl = repositoryUrl;
	}

	public File getMavenHome()
	{
		return mavenHome;
	}

	public void setMavenHome(File mavenHome)
	{
		this.mavenHome = mavenHome;
	}

	public List<DeployFileConfiguration> getDependencies()
	{
		return dependencies;
	}

	public void setDependencies(List<DeployFileConfiguration> dependencies)
	{
		this.dependencies = dependencies;
	}

	public PluginDescriptor getPluginDescriptor() {
		return pluginDescriptor;
	}

	public void setPluginDescriptor(PluginDescriptor pluginDescriptor) {
		this.pluginDescriptor = pluginDescriptor;
	}

	public boolean isOnlyParentPom() {
		return onlyParentPom;
	}

	public void setOnlyParentPom(boolean onlyParentPom) {
		this.onlyParentPom = onlyParentPom;
	}
}
