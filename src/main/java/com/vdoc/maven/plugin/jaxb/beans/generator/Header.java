/**
 *
 */
package com.vdoc.maven.plugin.jaxb.beans.generator;

import com.vdoc.maven.plugin.jaxb.beans.adapter.MapAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author famaridon
 */
@XmlType(name = "header")
@XmlAccessorType(XmlAccessType.FIELD)
public class Header {

	@XmlJavaTypeAdapter(MapAdapter.class)
	@XmlElement
	protected Map<String, String> configuration = new HashMap<String, String>();

	@XmlElement
	protected ScopeElement project = new ScopeElement();

	/**
	 * Gets the {@link com.vdoc.maven.plugin.jaxb.beans.generator.Header#project}.
	 *
	 * @return {@link com.vdoc.maven.plugin.jaxb.beans.generator.Header#project}
	 */
	public ScopeElement getProject() {
		return this.project;
	}

	/**
	 * Sets the {@link com.vdoc.maven.plugin.jaxb.beans.generator.Header#project}.
	 *
	 * @param project the {@link com.vdoc.maven.plugin.jaxb.beans.generator.Header#project} to set
	 */
	public void setProject(ScopeElement project) {
		this.project = project;
	}

	/**
	 * Gets the {@link com.vdoc.maven.plugin.jaxb.beans.generator.Header#configuration}.
	 *
	 * @return {@link com.vdoc.maven.plugin.jaxb.beans.generator.Header#configuration}
	 */
	public Map<String, String> getConfiguration() {
		return this.configuration;
	}

	/**
	 * Sets the {@link com.vdoc.maven.plugin.jaxb.beans.generator.Header#configuration}.
	 *
	 * @param configuration the {@link com.vdoc.maven.plugin.jaxb.beans.generator.Header#configuration} to set
	 */
	public void setConfiguration(Map<String, String> configuration) {
		this.configuration = configuration;
	}

}
