
set earFolder=%VDOC_HOME%\jboss\server\all\deploy\vdoc.ear
set targetVersion=14.0.4
set targetGroupId=com.vdoc.sdk
set repositoryId=vdoc
set repositoryUrl=http://192.168.3.123:9090/nexus/content/repositories/releases/
set mavenHome=D:\apache-maven-3.2.1


@echo earFolder : %earFolder%
@echo targetVersion : %targetVersion%
@echo targetGroupId : %targetGroupId%
@echo repositoryId : %repositoryId%
@echo repositoryUrl : %repositoryUrl%
@echo mavenHome : %mavenHome%

mvn.bat -X com.vdoc.maven:vdoc-maven-plugin:1.4-SNAPSHOT:deploy-vdoc -DearFolder=%earFolder% -DtargetVersion=%targetVersion% -DtargetGroupId=%targetGroupId% -DrepositoryId=%repositoryId% -DrepositoryUrl=%repositoryUrl% -DmavenHome=%mavenHome% 
