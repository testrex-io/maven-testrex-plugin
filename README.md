# testrex-maven-plugin

This plugin can be used to automatically send surefire reports to the Testrex server.

This project can be build and installed to local repository like this:

```
mvn clean install
```

After that you can use this plugin in you projects.


## Integration tests
Before running the integration tests, you should have installed this plugin in you local repository.
To run the build with integrations tests you must specify `run-its` profile:

```
mvn clean install -P run-its
```

# Configuring the plugin

You have to provide your project's id to make this plugin work. The url is not required. If no url is specified
`http://localhost:8080` is used as a default value.

If your testrex server is unsecured, you must set the `authentication` to `false`.

### Pom example for unsecured testrex
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
          <authentication>false</authentication>
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

If your testrex server is secured with OAuth2, you must provide username and password. 
You should also set the `oAuthTokenUrl`. Default value is `http://localhost:8080/auth/realms/dev/protocol/openid-connect/token`.
You can also specify this plugin's `client_id` which is set in your Authorization server. Default value is `testrex-maven-plugin`.

### Pom example of secured testrex
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
          <username>username</username>
          <password>password</password>
          <url>http://localhost:8180</url>
          <projectId>1</projectId>
          
          <!-- optional -->
          <oAuthTokenUrl>http://localhost:8080/auth/realms/dev/protocol/openid-connect/token</oAuthTokenUrl>
          <authClientId>testrex-maven-plugin</authClientId>
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