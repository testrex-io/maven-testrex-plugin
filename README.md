# testrex-maven-plugin

This plugin can be used to automatically send surefire reports to the Testrex server.

This project can be build and installed to local repository like this:

```
mvn clean install
```

After that you can use this plugin in you projects.

## Example pom

```xml
 <build>
  ...
    <plugins>
      ...
      <plugin>
        <groupId>io.testrex</groupId>
        <artifactId>testrex-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
          <url>http://localhost:8080</url>
          <projectId>1</projectId>
        </configuration>
        <executions>
          <execution>
            <id>send</id>
            <phase>test</phase>
            <goals>
              <goal>
                send
              </goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      ...
    </plugins>
  ...
</build>
```