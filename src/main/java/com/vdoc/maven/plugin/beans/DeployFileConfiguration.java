package com.vdoc.maven.plugin.beans;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by famaridon on 30/06/2014.
 */
public class DeployFileConfiguration {

	// Required Parameters

	/**
	 * File to be deployed.
	 * <b>User property is</b>: file.
	 */
	protected final File file;

	/**
	 * Server Id to map on the <id> under <server> section of settings.xml In most cases, this parameter will be required for authentication.
	 * Default value is: remote-repository.
	 * User property is: repositoryId.
	 */
	protected final String repositoryId;

	// Optional Parameters

	/**
	 * ArtifactId of the artifact to be deployed. Retrieved from POM file if specified.
	 * User property is: artifactId.
	 */
	protected String artifactId = null;

	/**
	 * Add classifier to the artifact
	 * User property is: classifier.
	 */
	protected String classifier = null;

	/**
	 * A comma separated list of classifiers for each of the extra side artifacts to deploy. If there is a mis-match in the number of entries in files or types, then an error will be raised.
	 * User property is: classifiers.
	 */
	protected String classifiers = null;

	/**
	 * Description passed to a generated POM file (in case of generatePom=true)
	 * User property is: generatePom.description.
	 */
	protected String description = null;

	/**
	 * A comma separated list of files for each of the extra side artifacts to deploy. If there is a mis-match in the number of entries in types or classifiers, then an error will be raised.
	 * User property is: files.
	 */
	protected String files = null;

	/**
	 * Upload a POM for this artifact. Will generate a default POM if none is supplied with the pomFile argument.
	 * Default value is: true.
	 * User property is: generatePom.
	 */
	protected boolean generatePom = true;

	/**
	 * GroupId of the artifact to be deployed. Retrieved from POM file if specified.
	 * User property is: groupId.
	 */
	protected String groupId = null;

	/**
	 * The bundled API docs for the artifact.
	 * User property is: javadoc.
	 */
	protected File javadoc = null;

	/**
	 * Type of the artifact to be deployed. Retrieved from the <packaging> element of the POM file if a POM file specified. Defaults to the file extension if it is not specified via command line or POM.
	 * Maven uses two terms to refer to this datum: the <packaging> element for the entire POM,
	 * and the <type> element in a dependency specification.
	 * User property is: packaging.
	 */
	protected String packaging = null;

	/**
	 * Location of an existing POM file to be deployed alongside the main artifact, given by the ${file} parameter.
	 * User property is: pomFile.
	 */
	protected File pomFile = null;

	/**
	 * Parameter used to control how many times a failed deployment will be retried before giving up and failing. If a value outside the range 1-10 is specified it will be pulled to the nearest value within the range 1-10.
	 * Default value is: 1.
	 * User property is: retryFailedDeploymentCount.
	 */
	protected int retryFailedDeploymentCount = 1;

	/**
	 * The bundled sources for the artifact.
	 * User property is: sources.
	 */
	protected File sources = null;

	/**
	 * A comma separated list of types for each of the extra side artifacts to deploy. If there is a mis-match in the number of entries in files or classifiers, then an error will be raised.
	 * User property is: types.
	 */
	protected String types = null;

	/**
	 * Whether to deploy snapshots with a unique version or not.
	 * Default value is: true.
	 * User property is: uniqueVersion.
	 */
	protected boolean uniqueVersion = true;
	/**
	 * Parameter used to update the metadata to make the artifact as release.
	 * Default value is: false.
	 * User property is: updateReleaseInfo.
	 */
	protected boolean updateReleaseInfo = true;

	/**
	 * URL where the artifact will be deployed.
	 * ie ( file:///C:/m2-repo or scp://host.com/path/to/repo )
	 * User property is: url.
	 */
	protected String url = null;

	/**
	 * Version of the artifact to be deployed. Retrieved from POM file if specified.
	 * User property is: version.
	 */
	protected String version = null;

	public DeployFileConfiguration(File file, String repositoryId) {
		this.file = file;
		this.repositoryId = repositoryId;
	}

	public List<String> toCmd() {
		List<String> strings = new ArrayList<>(2);

		Validate.notNull(this.file, "file is mandatory parameter.");
		this.appendCmdFile(strings, "file", this.file);
		Validate.notEmpty(this.repositoryId, "repositoryId is mandatory parameter.");
		this.appendCmdString(strings, "repositoryId", repositoryId);

		// Optional Parameters
		this.appendCmdString(strings, "artifactId", artifactId);
		this.appendCmdString(strings, "classifier", classifier);
		this.appendCmdString(strings, "classifiers", classifiers);
		this.appendCmdString(strings, "description", description);
		this.appendCmdString(strings, "files", files);
		this.appendCmdString(strings, "generatePom", Boolean.toString(generatePom));
		this.appendCmdString(strings, "groupId", groupId);
		this.appendCmdFile(strings, "javadoc", this.javadoc);
		this.appendCmdString(strings, "packaging", packaging);
		this.appendCmdFile(strings, "pomFile", this.pomFile);
		this.appendCmdString(strings, "retryFailedDeploymentCount", Integer.toString(retryFailedDeploymentCount));
		this.appendCmdFile(strings, "sources", this.sources);
		this.appendCmdString(strings, "types", this.types);
		this.appendCmdString(strings, "uniqueVersion", Boolean.toString(uniqueVersion));
		this.appendCmdString(strings, "updateReleaseInfo", Boolean.toString(updateReleaseInfo));
		this.appendCmdString(strings, "url", url);
		this.appendCmdString(strings, "version", version);

		return strings;
	}

	protected void appendCmdString(List<String> strings, String parameterName, String value) {
		if (StringUtils.isNotEmpty(value)) {
			strings.add("-D" + parameterName + "=" + value);
		}
	}

	protected void appendCmdFile(List<String> strings, String parameterName, File value) {
		if (value != null) {
			strings.add("-D" + parameterName + "=" + value.getAbsolutePath());
		}
	}

	public File getFile() {
		return file;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getClassifiers() {
		return classifiers;
	}

	public void setClassifiers(String classifiers) {
		this.classifiers = classifiers;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	public boolean isGeneratePom() {
		return generatePom;
	}

	public void setGeneratePom(boolean generatePom) {
		this.generatePom = generatePom;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public File getJavadoc() {
		return javadoc;
	}

	public void setJavadoc(File javadoc) {
		this.javadoc = javadoc;
	}

	public String getPackaging() {
		return packaging;
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public File getPomFile() {
		return pomFile;
	}

	public void setPomFile(File pomFile) {
		this.pomFile = pomFile;
	}

	public int getRetryFailedDeploymentCount() {
		return retryFailedDeploymentCount;
	}

	public void setRetryFailedDeploymentCount(int retryFailedDeploymentCount) {
		this.retryFailedDeploymentCount = retryFailedDeploymentCount;
	}

	public File getSources() {
		return sources;
	}

	public void setSources(File sources) {
		this.sources = sources;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public boolean isUniqueVersion() {
		return uniqueVersion;
	}

	public void setUniqueVersion(boolean uniqueVersion) {
		this.uniqueVersion = uniqueVersion;
	}

	public boolean isUpdateReleaseInfo() {
		return updateReleaseInfo;
	}

	public void setUpdateReleaseInfo(boolean updateReleaseInfo) {
		this.updateReleaseInfo = updateReleaseInfo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
