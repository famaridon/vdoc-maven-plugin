<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>${targetGroupId}</groupId>
    <artifactId>vdoc.sdk.full</artifactId>
    <version>${targetVersion}</version>

    <dependencies>
        <#list dependencies as dependency >
        <dependency>
            <groupId>${dependency.groupId}</groupId>
            <artifactId>${dependency.artifactId}</artifactId>
            <version>${dependency.version}</version>
        </dependency>
        </#list>
    </dependencies>

</project>