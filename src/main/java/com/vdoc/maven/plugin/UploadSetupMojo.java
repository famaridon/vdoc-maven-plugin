package com.vdoc.maven.plugin;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Upload this setup into a store
 */
@Mojo(name = "upload-setup", threadSafe = true, defaultPhase = LifecyclePhase.DEPLOY)
public class UploadSetupMojo extends AbstractVDocMojo {


	/**
	 * the user name with upload right
	 */
	@Parameter(required = true)
	private String username;
	/**
	 * the user's password
	 */
	@Parameter(required = true)
	private String password;
	/**
	 * the restFull store url
	 */
	@Parameter(required = true)
	private String storeUrlHost;
	/**
	 * the http port
	 */
	@Parameter(required = true, defaultValue = "80")
	private int storeUrlport;
	/**
	 * the vdoc version to build correct store path
	 */
	@Parameter(required = true)
	private String vdocVersion;
	/**
	 * where the setup can be found
	 */
	@Parameter(required = true)
	private String setupName;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		FTPClient ftpClient = new FTPClient();

		try {
			ftpClient.connect(storeUrlHost, storeUrlport);

			// After connection attempt, you should check the reply code to verify
			// success.
			int reply = ftpClient.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				getLog().error("FTP server refused connection.");
				throw new MojoFailureException("FTP server refused connection.");
			}

			ftpClient.login(username, password);

			// build the correct path
			safeChangeDirectory(ftpClient, this.project.getGroupId());
			safeChangeDirectory(ftpClient, this.project.getArtifactId());
			safeChangeDirectory(ftpClient, this.vdocVersion);
			safeChangeDirectory(ftpClient, this.project.getVersion());

			// upload file
			try (InputStream input = new FileInputStream(new File(this.buildDirectory, setupName))) {
				ftpClient.storeFile(setupName, input);
			}

			ftpClient.logout();

		} catch (IOException e) {
			throw new MojoFailureException(e.getMessage());
		}


	}

	protected void safeChangeDirectory(FTPClient ftpClient, String directory) throws IOException, MojoExecutionException {
		if (!ftpClient.changeWorkingDirectory(directory)) {
			if (!ftpClient.makeDirectory(directory)) {
				throw new MojoExecutionException("can't create folder " + directory + " in " + ftpClient.printWorkingDirectory());
			}
			ftpClient.changeWorkingDirectory(directory);
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStoreUrlHost() {
		return storeUrlHost;
	}

	public void setStoreUrlHost(String storeUrlHost) {
		this.storeUrlHost = storeUrlHost;
	}

	public int getStoreUrlport() {
		return storeUrlport;
	}

	public void setStoreUrlport(int storeUrlport) {
		this.storeUrlport = storeUrlport;
	}
}
