package com.vdoc.maven.plugin;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;

/**
 * Generate this documentation
 */
@Mojo(name = "generate-plugin-doc", threadSafe = false)
public class GeneratePluginDocMojo extends AbstractMojo {

    /**
     * the current running plugin description
     */
    protected PluginDescriptor pluginDescriptor;


    /**
     * the documentation output directory
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-site/jekyll")
    protected File outputDirectory;

    /**
     * a temp directory used to extract ftl files.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-site/temp")
    protected File tempDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        this.pluginDescriptor = ((PluginDescriptor) getPluginContext().get("pluginDescriptor"));

        tempDirectory.mkdirs();
        outputDirectory.mkdirs();

        try {
            // build the full pom
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_21);

            // Specify the data source where the template files come from. Here I set a
            // plain directory for it, but non-file-system are possible too:
            cfg.setDirectoryForTemplateLoading(tempDirectory);

            // Specify how templates will see the data-model. This is an advanced topic...
            // for now just use this:
            BeansWrapperBuilder wrapperBuilder = new BeansWrapperBuilder(Configuration.VERSION_2_3_21);
            wrapperBuilder.setStrict(false);
            cfg.setObjectWrapper(wrapperBuilder.build());

            // Set your preferred charset template files are stored in. UTF-8 is
            // a good choice in most applications:
            cfg.setDefaultEncoding("UTF-8");

            // Sets how errors will appear. Here we assume we are developing HTML pages.
            // For production systems TemplateExceptionHandler.RETHROW_HANDLER is better.
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            // At least in new projects, specify that you want the fixes that aren't
            // 100% backward compatible too (these are very low-risk changes as far as the
            // 1st and 2nd version number remains):
            cfg.setIncompatibleImprovements(Configuration.VERSION_2_3_21);  // FreeMarker 2.3.20

            // copy the template from jar freemaker can't read stream
            getLog().debug("get the template localy.");
            String ftlName = "mojo-doc.ftl";
            try (
                    InputStream input = getClass().getClassLoader().getResourceAsStream("documentation/" + ftlName);
                    FileOutputStream outputStream = new FileOutputStream(new File(this.tempDirectory, ftlName));
            ) {
                IOUtils.copy(input, outputStream);
                outputStream.flush();
            }

            getLog().debug("Parse the  ftl.");
            Template temp = cfg.getTemplate(ftlName);

            for (MojoDescriptor mojoDescriptor : pluginDescriptor.getMojos()) {
                getLog().info("Build documentation into " + this.outputDirectory.getAbsolutePath() + "/" + mojoDescriptor.getGoal() + ".html");
                File pom = new File(this.outputDirectory, mojoDescriptor.getGoal() + ".html");
                try (Writer out = new FileWriter(pom);) {
                    temp.process(mojoDescriptor, out);
                    out.flush();
                }
            }

        } catch (IOException | TemplateException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }


    }

    protected void generateMojoDoc(MojoDescriptor mojoDescriptor) throws MojoExecutionException {

    }

}
