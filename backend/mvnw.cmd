@REM Maven Wrapper script for Windows
@REM
@setlocal

set ERROR_CODE=0
if not "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir
set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR%"=="" set MAVEN_PROJECTBASEDIR=%CD%
goto endDetectBaseDir
:endDetectBaseDir

set MAVEN_CMD_LINE_ARGS=%*

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"

if exist %WRAPPER_JAR% (
    goto execute
)

echo Downloading Maven Wrapper...
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar' -UseBasicParsing}"
if %ERRORLEVEL% neq 0 goto error

:execute
if "%JAVA_HOME%"=="" (set "JAVA_EXE=java") else (set "JAVA_EXE=%JAVA_HOME%\bin\java")
"%JAVA_EXE%" -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR:~0,-1%" -classpath %WRAPPER_JAR% org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CMD_LINE_ARGS%
if %ERRORLEVEL% neq 0 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
exit /B %ERROR_CODE%
