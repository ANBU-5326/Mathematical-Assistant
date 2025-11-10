Direct downloads and quick install (Windows)

1) Java 17 (Eclipse Temurin)
- Official page (pick Windows x64 MSI/zip): https://adoptium.net/temurin/releases/?version=17
- Example direct release page: https://github.com/adoptium/temurin17-binaries/releases/latest

2) Apache Maven (binary zip)
- Official download page: https://maven.apache.org/download.cgi
- Example direct binary (replace version if needed): https://dlcdn.apache.org/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.zip

Windows quick install steps (PowerShell / CMD)
- Install JDK 17 (run MSI or extract zip). Note the install path, e.g. C:\Program Files\Eclipse Adoptium\jdk-17.0.x
- Install Maven: extract apache-maven-3.9.4 to C:\apache-maven-3.9.4

Set environment variables (run in an elevated CMD or set via System Properties)
# set JAVA_HOME (example)
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-17.0.x"

# add Maven to PATH (append)
setx PATH "%PATH%;C:\apache-maven-3.9.4\bin"

# After setx restart your terminal for changes to take effect

Verify installations
java -version
mvn -v

Build and run the project (from project root c:\CPY_SAVES\Math-web-vs)
# download dependencies and build
mvn -U clean package

# run packaged jar
java -jar target\MathAssistantWeb-1.0.0.jar

# or run directly with Maven
mvn spring-boot:run

If you prefer not to install Maven globally
- Ask me to "add wrapper" and I will add the Maven Wrapper files (mvnw / mvnw.cmd + .mvn/wrapper config) so you can run ./mvnw spring-boot:run on machines without Maven (the wrapper requires the wrapper jar, which I can add).

If something fails
- Copy the exact output of `mvn -U clean package` or `mvn spring-boot:run` and paste here and I will help debug.
