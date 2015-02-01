package com.vdoc.maven.plugin;

import com.vdoc.maven.plugin.jaxb.beans.authenticate.AuthenticateQuery;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
import java.io.StringWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Generate VDoc classes
 */
@Mojo(name = "generate-classes", threadSafe = true)
public class GenerateClass extends AbstractVDocMojo {

	/**
	 * the VDoc url where found data model
	 */
	@Parameter(alias = "vdoc-url", required = true)
	protected String vdocURL;
	/**
	 * A VDoc user login
	 */
	@Parameter(alias = "login", required = true)
	protected String login;
	/**
	 * the user's password
	 */
	@Parameter(alias = "password", required = true)
	protected String password;
	/**
	 * Base classes must be generated ?
	 */
	@Parameter(alias = "base-classes", required = false, defaultValue = "true")
	protected boolean baseClasses;
	/**
	 * where classes must be output. You can prefer a specific folder for generated classes but don't forgot to add this folder into compile plugin.
	 */
	@Parameter(alias = "output-directory", required = false, defaultValue = "${project.build.sourceDirectory}")
	protected File outputDirectory;
	/**
	 * custom classes must be generated. (it's recommended to turn it on only first time)
	 */
	@Parameter(alias = "custom-classes", required = false, defaultValue = "false")
	protected boolean customClasses;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// start by checking the url
		if (vdocURL.endsWith("/")) {
			vdocURL = vdocURL + "/";
		}

		CloseableHttpClient httpClient = HttpClients.createSystem();
		try {
			// we start by creating authentication key
			AuthenticateQuery response = getAuthenticateToken(httpClient);

			// we get the classes
			StringBuilder getClassesURL = new StringBuilder(vdocURL);
			getClassesURL.append("navigation/classes/generator?")
					.append("_AuthenticationKey=").append(response.getBody().getToken().getKey())
					.append("&customClasses=").append(customClasses)
					.append("&baseClasses=").append(baseClasses);

			HttpGet getClasses = new HttpGet(getClassesURL.toString());
			getClasses.setHeader("Content-Type", "application/xml");

			HttpResponse getClassesResponse = httpClient.execute(getClasses);

			if (getClassesResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new MojoFailureException("Can't generate VDoc interfaces!");
			}

            try (ZipInputStream zipInputStream = new ZipInputStream(getClassesResponse.getEntity().getContent())) {
                this.unzip(zipInputStream, this.outputDirectory);
            }

		} catch (JAXBException e) {
			getLog().error("Can't init the JAXB context : ", e);
			throw new MojoExecutionException("Can't init the JAXB context : " + e.getMessage());
		} catch (IOException e) {
			getLog().error("Can't join the VDoc server : ", e);
			throw new MojoExecutionException("Can't join the VDoc server : " + e.getMessage());
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				getLog().error("Http client can't be closed : ", e);
			}
		}


	}

	protected AuthenticateQuery getAuthenticateToken(CloseableHttpClient httpclient) throws JAXBException, IOException, MojoFailureException {
		JAXBContext jaxbContext = JAXBContext.newInstance(AuthenticateQuery.class);
		Marshaller authenticationMarshaller = jaxbContext.createMarshaller();
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		AuthenticateQuery query = new AuthenticateQuery();
		query.getHeader().setLogin(this.login);
		query.getHeader().setPassword(this.password);

		HttpPost tokenPost = new HttpPost(vdocURL + "navigation/flow?module=portal&cmd=authenticate");
		tokenPost.setHeader("Content-Type", "application/xml");

		// set the body
		try (StringWriter writer = new StringWriter()) {
			authenticationMarshaller.marshal(query, writer);
			tokenPost.setEntity(new StringEntity(writer.getBuffer().toString()));
		}

		HttpResponse tokenResponse = httpclient.execute(tokenPost);

		if (tokenResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			throw new MojoFailureException("Can't authenticate to VDoc serveur!");
		}

		AuthenticateQuery response = (AuthenticateQuery) unmarshaller.unmarshal(tokenResponse.getEntity().getContent());

		getLog().info("token is : " + response.getBody().getToken().getKey());
		return response;
	}

	public void unzip(ZipInputStream stream, File output) throws IOException {
		// create a buffer to improve copy performance later.
		byte[] buffer = new byte[2048];

		// now iterate through each item in the stream. The get next
		// entry call will return a ZipEntry for each file in the
		// stream
		ZipEntry entry;
		while ((entry = stream.getNextEntry()) != null) {
			// Once we get the entry from the stream, the stream is
			// positioned read to read the raw data, and we keep
			// reading until read returns 0 or less.
            File targetFile = new File(this.project.getCompileSourceRoots().iterator().next(), entry.getName());
            targetFile.getParentFile().mkdirs();
            getLog().info("Create file : " + targetFile.getPath());
            try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                int len;
                while ((len = stream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
				}
				outputStream.flush();
			}
		}
	}

	public String getVdocURL() {
		return vdocURL;
	}

	public void setVdocURL(String vdocURL) {
		this.vdocURL = vdocURL;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isBaseClasses() {
		return baseClasses;
	}

	public void setBaseClasses(boolean baseClasses) {
		this.baseClasses = baseClasses;
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public boolean isCustomClasses() {
		return customClasses;
	}

	public void setCustomClasses(boolean customClasses) {
		this.customClasses = customClasses;
	}
}
