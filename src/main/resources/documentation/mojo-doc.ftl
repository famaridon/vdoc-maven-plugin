<#-- @ftlvariable name="" type="org.apache.maven.plugin.descriptor.MojoDescriptor" -->
---
layout: doc
title: ${goal}
description: |
${description}
permalink: /${goal}/
category: doc
weight: 2
---

<h2>Full name</h2>
<p>${pluginDescriptor.groupId}:${pluginDescriptor.artifactId}:{{vdoc_maven_plugin_version}}:${goal}</p>

<h2>Attributes</h2>
<ul>
<#if projectRequired>
    <li>Requires a Maven project to be executed.</li>
</#if>
<#if dependencyResolutionRequired?? >
    <li>Requires dependency resolution of artifacts in scope: ${dependencyResolutionRequired}.</li>
</#if>
<#if threadSafe>
    <li>The goal is thread-safe and supports parallel builds.</li>
</#if>
<#if executeGoal?? >
    <li>Binds by default to the lifecycle phase: ${executeGoal}.</li>
</#if>

</ul>

<h3>Parameters</h3>
<dl class="dl-horizontal parameters">
<#list parameters as parameter>
    <dt class="<#if parameter.required>required</#if>">${parameter.name}</dt>
    <dd>${parameter.description}
        <ul>
            <li><span class="type">${parameter.type}</span></li>
            <#if parameter.defaultValue?? >
                <li><span class="default-value">${parameter.defaultValue}</span></li></#if>
        </ul>
    </dd>
</#list>
</dl>

<h2 id="pom-example">Example</h2>

<pre data-src="{{ '/example/${goal}/pom.xml' | prepend: site.baseurl }}" d></pre>