/**
 *
 */
package com.vdoc.maven.plugin.jaxb.beans.generator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author famaridon
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ScopeElement {
	@XmlAttribute(name = "protocol-uri")
	protected String protocolUri;

	/**
	 * Gets the {@link com.vdoc.maven.plugin.jaxb.beans.generator.ScopeElement#protocolUri}.
	 *
	 * @return {@link com.vdoc.maven.plugin.jaxb.beans.generator.ScopeElement#protocolUri}
	 */
	public String getProtocolUri() {
		return this.protocolUri;
	}

	/**
	 * Sets the {@link com.vdoc.maven.plugin.jaxb.beans.generator.ScopeElement#protocolUri}.
	 *
	 * @param protocolUri the {@link com.vdoc.maven.plugin.jaxb.beans.generator.ScopeElement#protocolUri} to set
	 */
	public void setProtocolUri(String protocolUri) {
		this.protocolUri = protocolUri;
	}

}
