<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <artifactId>sdk</artifactId>
        <groupId>com.vdoc.engineering</groupId>
        <version>${targetVersion}</version>
    </parent>


    <artifactId>sdk.advanced</artifactId>
    <packaging>pom</packaging>

    <dependencies>
	<#list dependencies as dependency >
        <dependency>
            <groupId>${dependency.groupId}</groupId>
            <artifactId>${dependency.artifactId}</artifactId>
            <version>${r"${vdoc.version}"}</version>
            <scope>provided</scope>
        </dependency>
	</#list>
    </dependencies>

    <build>
        <plugins>
            <!-- active default parent plugins -->
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

    <profiles>

        <profile>
            <!-- enable the setup build -->
            <id>packager</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-dependency-plugin</artifactId>
                    </plugin>
                    <plugin>
                        <groupId>com.vdoc.maven</groupId>
                        <artifactId>vdoc-maven-plugin</artifactId>
                        <version>${r"${vdoc-maven-plugin.version}"}</version>
                        <executions>
                            <execution>
                                <phase>package</phase>
                                <goals>
                                    <goal>create-setup</goal>
                                </goals>
                                <configuration>
                                    <setupName>${r"${project.artifactId}-${project.version} for VDoc${project.parent.version}"}</setupName>
                                    <packagingType>APPS</packagingType>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>

        <profile>
            <!-- enable the hard deploy (require 2 next) -->
            <id>deployer</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.vdoc.maven</groupId>
                        <artifactId>vdoc-maven-plugin</artifactId>
                        <version>${r"${vdoc-maven-plugin.version}"}</version>
                        <executions>
                            <execution>
                                <phase>package</phase>
                                <goals>
                                    <goal>hard-deploy</goal>
                                </goals>
                                <configuration>
                                    <includeTest>true</includeTest>
                                    <vdocHome>${r"${VDOC_HOME}"}</vdocHome>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>

    </profiles>

</project>