# testrex-maven-plugin

This plugin can be used to automatically send surefire reports to the Testrex server.

This project can be build and installed to local repository like this:

```
mvn clean install
```

After that you can use this plugin in you projects.

## Example pom
You have to provide your project's id to make this plugin work. The url is not required. If no url is specified http://localhost:8080 is used as a default value.
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

You should also set the surefire plugin to ignore test fails, so the testrex plugin is run even when any of the tests fails.

```
<project>
  <build>
    <plugins>
      ...
      <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-surefire-plugin</artifactId>
         <configuration>
           <testFailureIgnore>true</testFailureIgnore>
         </configuration>
      </plugin>
      ...
    </plugins>
  </build>
</project>
```