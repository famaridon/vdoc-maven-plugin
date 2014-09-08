package com.vdoc.maven.plugin;

import com.vdoc.maven.plugin.jaxb.beans.authenticate.AuthenticateQuery;
import com.vdoc.maven.plugin.jaxb.beans.generator.ResourceDefinitionGeneratorQuery;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by famaridon on 19/05/2014.
 */
@Mojo(name = "GenerateClass", threadSafe = true)
public class GenerateClass extends AbstractVDocMojo
{

	@Parameter(alias = "vdoc-url",required = true)
	protected String vdocURL;

	@Parameter(alias = "login",required = true)
	protected String login;

	@Parameter(alias = "password",required = true)
	protected String password;

	@Parameter(alias = "project-uri",required = true)
	protected String projectURI;

	@Parameter(alias = "export-package",required = false)
	protected String exportPackage;
	@Parameter(alias = "used-sub-package-by-catalog-type",required = false)
	protected boolean usedSubPackageByCatalogType;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		if(vdocURL.endsWith("/"))
		{
			vdocURL = vdocURL+"/";
		}

		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(AuthenticateQuery.class);
			Marshaller authenticationMarshaller = jaxbContext.createMarshaller();
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			AuthenticateQuery query = new AuthenticateQuery();
			query.getHeader().setLogin(this.login);
			query.getHeader().setPassword(this.password);

			URL authenticationURL = new URL(vdocURL+"navigation/flow?module=portal&cmd=authenticate");

			HttpURLConnection authenticationConnection = (HttpURLConnection) authenticationURL.openConnection();

			//add reuqest header
			authenticationConnection.setRequestMethod("POST");
			authenticationConnection.setRequestProperty("Content-Type", "application/xml");
			authenticationConnection.setDoOutput(true);
			authenticationConnection.setDoInput(true);

			authenticationMarshaller.marshal(query, authenticationConnection.getOutputStream());

			if(authenticationConnection.getResponseCode() != 200)
			{
				throw new MojoFailureException("Can't authenticate to VDoc serveur!");
			}

			AuthenticateQuery response = (AuthenticateQuery) unmarshaller.unmarshal(authenticationConnection.getInputStream());

			getLog().info("token is : "+response.getBody().getToken().getKey());

			URL generatorURL = new URL(vdocURL+"navigation/lab/generator?_AuthenticationKey="+response.getBody()
					.getToken().getKey());

			JAXBContext jaxbContext2 = JAXBContext.newInstance(ResourceDefinitionGeneratorQuery.class);
			Marshaller generatorMarshaller = jaxbContext2.createMarshaller();


			ResourceDefinitionGeneratorQuery generatorQuery = new ResourceDefinitionGeneratorQuery();
			generatorQuery.getHeader().getProject().setProtocolUri(this.projectURI);
			generatorQuery.getHeader().getConfiguration().put(ResourceDefinitionGeneratorQuery.EXPORT_PACKAGE,
					this.exportPackage);
			generatorQuery.getHeader().getConfiguration().put(ResourceDefinitionGeneratorQuery
					.USED_SUB_PACKAGE_BY_TYPE,Boolean.toString(this.usedSubPackageByCatalogType));

			HttpURLConnection generatorConnection = (HttpURLConnection) generatorURL.openConnection();

			//add reuqest header
			generatorConnection.setRequestMethod("POST");
			generatorConnection.setRequestProperty("Content-Type", "application/xml");
			generatorConnection.setDoOutput(true);
			generatorConnection.setDoInput(true);

			generatorMarshaller.marshal(generatorQuery, generatorConnection.getOutputStream());

			if(generatorConnection.getResponseCode() != 200)
			{
				throw new MojoFailureException("Can't generate VDoc interfaces!");
			}

			try
			{
				Thread.sleep(5000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			try(ZipInputStream zipInputStream = new ZipInputStream(generatorConnection.getInputStream());)
			{
				this.unzip(zipInputStream,this.outputDirectory);
			}

		} catch (JAXBException e)
		{
			getLog().error("Can't init the JAXB context : ",e);
			throw new MojoExecutionException("Can't init the JAXB context : "+e.getMessage());
		} catch (IOException e)
		{
			getLog().error("Can't join the VDoc server : ", e);
			throw new MojoExecutionException("Can't join the VDoc server : "+e.getMessage());
		}


	}

	public void unzip(ZipInputStream stream, File output) throws IOException
	{
			// create a buffer to improve copy performance later.
			byte[] buffer = new byte[2048];

			// now iterate through each item in the stream. The get next
			// entry call will return a ZipEntry for each file in the
			// stream
			ZipEntry entry;
			while((entry = stream.getNextEntry())!=null)
			{
				// Once we get the entry from the stream, the stream is
				// positioned read to read the raw data, and we keep
				// reading until read returns 0 or less.
				File targetFile = new File((String) this.project.getCompileSourceRoots().iterator().next(),
						entry.getName());
				targetFile.getParentFile().mkdirs();
				getLog().info("Create file : "+targetFile.getPath());
				try(FileOutputStream outputStream = new FileOutputStream(targetFile);)
				{
					int len = 0;
					while ((len = stream.read(buffer)) > 0)
					{
						outputStream.write(buffer, 0, len);
					}
					outputStream.flush();
				}
			}
	}
}
