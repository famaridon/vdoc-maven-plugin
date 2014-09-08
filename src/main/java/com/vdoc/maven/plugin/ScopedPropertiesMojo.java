package com.vdoc.maven.plugin;

import com.vdoc.maven.plugin.beans.DeployFileConfiguration;
import com.vdoc.maven.plugin.jaxb.beans.Wrapper;
import com.vdoc.maven.plugin.jaxb.beans.properties.Property;
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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Created by famaridon on 19/05/2014.
 */
@Mojo(name = "build-properties-files", threadSafe = false, requiresProject = false, requiresDirectInvocation = true)
public class ScopedPropertiesMojo extends AbstractMojo
{

	@Parameter(property = "propertiesXmlFolder", required = true)
	protected File propertiesXmlFolder;

	@Parameter(property = "outputFolder", required = true)
	protected File outputFolder;

	@Parameter(property = "targetScope", required = true)
	protected String targetScope;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{

		JAXBContext jaxbContext = null;
		Unmarshaller unmarshaller = null;
		try
		{
			jaxbContext = JAXBContext.newInstance(Wrapper.class, Property.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e)
		{
			throw new MojoExecutionException(e.getMessage());
		}


		File[] propertiesXmlFiles = propertiesXmlFolder.listFiles((FileFilter) new SuffixFileFilter(".properties.xml"));
		for (File proprtiesXml : propertiesXmlFiles)
		{
			try
			{
				Wrapper<Property> wrapper = (Wrapper<Property>) unmarshaller.unmarshal(proprtiesXml);

				Properties properties = new Properties();
				for(Property property : wrapper.getItems())
				{
					properties.setProperty(property.getName(),property.getValues().get(targetScope));
				}

				try(FileWriter writer = new FileWriter(new File(outputFolder ,FilenameUtils.getBaseName(proprtiesXml.getName()))))
				{
					properties.store(writer, "Maven plugin building file for scope : "+targetScope);
				} catch (IOException e)
				{
					throw new MojoFailureException("can't write properties file",e);
				}


			} catch (JAXBException e)
			{
				throw new MojoFailureException("can't read xml file : "+proprtiesXml.getAbsolutePath(),e);
			}

		}


	}

	public File getPropertiesXmlFolder()
	{
		return propertiesXmlFolder;
	}

	public void setPropertiesXmlFolder(File propertiesXmlFolder)
	{
		this.propertiesXmlFolder = propertiesXmlFolder;
	}

	public File getOutputFolder()
	{
		return outputFolder;
	}

	public void setOutputFolder(File outputFolder)
	{
		this.outputFolder = outputFolder;
	}

	public String getTargetScope()
	{
		return targetScope;
	}

	public void setTargetScope(String targetScope)
	{
		this.targetScope = targetScope;
	}
}
