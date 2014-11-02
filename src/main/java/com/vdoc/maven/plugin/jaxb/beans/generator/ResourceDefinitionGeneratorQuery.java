/**
 *
 */
package com.vdoc.maven.plugin.jaxb.beans.generator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author famaridon
 */
@XmlRootElement(name = "get")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceDefinitionGeneratorQuery {

	public static final String EXPORT_PACKAGE = "exportPackage";
	public static final String USED_SUB_PACKAGE_BY_TYPE = "usedSubPackageByCatalogType";

	@XmlElement
	protected Header header = new Header();

	/**
	 * Gets the {@link com.vdoc.maven.plugin.jaxb.beans.generator.ResourceDefinitionGeneratorQuery#header}.
	 *
	 * @return {@link com.vdoc.maven.plugin.jaxb.beans.generator.ResourceDefinitionGeneratorQuery#header}
	 */
	public Header getHeader() {
		return this.header;
	}

	/**
	 * Sets the {@link com.vdoc.maven.plugin.jaxb.beans.generator.ResourceDefinitionGeneratorQuery#header}.
	 *
	 * @param header the {@link com.vdoc.maven.plugin.jaxb.beans.generator.ResourceDefinitionGeneratorQuery#header} to set
	 */
	public void setHeader(Header header) {
		this.header = header;
	}

}
