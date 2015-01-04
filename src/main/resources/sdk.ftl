<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.vdoc.engineering</groupId>
    <artifactId>sdk</artifactId>
    <version>${targetVersion}</version>

    <packaging>pom</packaging>

    <organization>
        <name>Visiativ Software</name>
        <url>http://www.myvdoc.net</url>
    </organization>

    <modules>
        <module>sdk.essential</module>
        <module>sdk.advanced</module>
        <module>apps.assembly.descriptor</module>
    </modules>

    <properties>
        <vdoc.version>${targetVersion}</vdoc.version>
        <project.build.sourceEncoding>cp1252</project.build.sourceEncoding>
        <skipTests>true</skipTests>
        <vdoc-maven-plugin.version>14.1.0-SNAPSHOT</vdoc-maven-plugin.version>
    </properties>

    <dependencies>
        <!-- findbugs -->
        <dependency>
            <groupId>com.google.code.findbugs</groupId>
            <artifactId>jsr305</artifactId>
            <version>3.0.0</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.google.code.findbugs</groupId>
            <artifactId>annotations</artifactId>
            <version>3.0.0</version>
            <scope>provided</scope>
        </dependency>

        <!-- apache commons -->
        <dependency>
            <groupId>commons-codec</groupId>
            <artifactId>commons-codec</artifactId>
            <version>1.3</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>commons-lang</groupId>
            <artifactId>commons-lang</artifactId>
            <version>2.4</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>commons-pool</groupId>
            <artifactId>commons-pool</artifactId>
            <version>1.4</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>commons-configuration</groupId>
            <artifactId>commons-configuration</artifactId>
            <version>1.5</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.2.1</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
            <version>1.1.1</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>commons-collections</groupId>
            <artifactId>commons-collections</artifactId>
            <version>3.2.1</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>1.4</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>commons-discovery</groupId>
            <artifactId>commons-discovery</artifactId>
            <version>0.4</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>commons-httpclient</groupId>
            <artifactId>commons-httpclient</artifactId>
            <version>3.1</version>
            <scope>provided</scope>
        </dependency>
        <!-- jettison -->
        <dependency>
            <groupId>org.codehaus.jettison</groupId>
            <artifactId>jettison</artifactId>
            <version>1.3</version>
            <scope>provided</scope>
        </dependency>

        <!-- JBoss -->
        <dependency>
            <groupId>org.jboss</groupId>
            <artifactId>servlet-api</artifactId>
            <version>5.1.0.GA</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.jboss</groupId>
            <artifactId>jsp-api</artifactId>
            <version>5.1.0.GA</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.jboss</groupId>
            <artifactId>jboss-j2ee</artifactId>
            <version>5.1.0.GA</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.jboss</groupId>
            <artifactId>ejb3-persistence</artifactId>
            <version>5.1.0.GA</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.jboss</groupId>
            <artifactId>jboss-ejb3-ext-api</artifactId>
            <version>5.1.0.GA</version>
            <scope>provided</scope>
        </dependency>
        <!-- JSTL pour JSP 2.0 -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
            <version>1.1.2</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>taglibs</groupId>
            <artifactId>standard</artifactId>
            <version>1.1.2</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>com.sun.xml.bind</groupId>
            <artifactId>jaxb-xjc</artifactId>
            <version>2.2</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <pluginManagement>
            <plugins>
                <!-- change default to JDK 1.7-->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.1</version>
                    <configuration>
                        <source>
                        1.7</source>
                        <target>1.7</target>
                        <fork>true</fork>
                    </configuration>
                </plugin>

                <!-- create a nice jar -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-jar-plugin</artifactId>
                    <version>2.5</version>
                    <configuration>
                        <archive>
                            <index>true</index>
                            <manifest>
                                <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
                                <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>
                                <addClasspath>true</addClasspath>
                            </manifest>
                        </archive>
                    </configuration>
                </plugin>

                <!-- package the vdoc apps -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-dependency-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>copy-dependencies</id>
                            <phase>prepare-package</phase>
                            <goals>
                                <goal>copy-dependencies</goal>
                            </goals>
                            <configuration>
                                <excludeScope>provided</excludeScope>
                                <outputDirectory>${r"${project.build.directory}"}/lib</outputDirectory>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>


            </plugins>
        </pluginManagement>
    </build>

    <profiles>
        <profile>
            <id>vdoc-configuration</id>
            <activation>
                <file>
                    <exists>home.properties</exists>
                </file>
            </activation>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>properties-maven-plugin</artifactId>
                        <version>1.0-alpha-2</version>
                        <executions>
                            <execution>
                                <phase>initialize</phase>
                                <goals>
                                    <goal>read-project-properties</goal>
                                </goals>
                                <configuration>
                                    <files>
                                        <file>home.properties</file>
                                    </files>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>

    <distributionManagement>
        <repository>
            <id>vdoc</id>
            <name>vdoc repository</name>
            <url>${r"${vdoc.deploy.server}"}/nexus/content/repositories/releases/</url>
        </repository>
        <snapshotRepository>
            <id>vdoc.snapshot</id>
            <name>vdoc snapshot repository</name>
            <url>${r"${vdoc.deploy.server}"}/nexus/content/repositories/snapshots/</url>
        </snapshotRepository>
    </distributionManagement>

</project>