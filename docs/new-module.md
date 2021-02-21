# Add a new module

This page describes how to add a new module in the project.

## Implementation module ('jpa', 'mongodb', ...)

* Create a new folder `mymodule` at the same level as the `jpa` folder
* Go to https://start.spring.io/
  * create a new Maven Project with the required dependencies
  * Generate and download the zip
  * The zip contains a root folder:
    * Open this root folder
    * Copy/Paste the following folders and files into the created folder `mymodule`:
      ```
      mymodule (root folder)
      |_ src
      |_ HELP.md
      |_ pom.xml
      ```
  * The result is:
    ```
    weedow-searchy (project root folder)
    |_ ...
    :
    |_ jpa
    :
    |_ mymodule
       |_ src
       |_ HELP.md
       |_ pom.xml
    ```
* Replace the `pom.xml` file content of the new module by the following:
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <parent>
          <groupId>com.weedow</groupId>
          <artifactId>weedow-searchy-parent</artifactId>
          <version>0.0.2-SNAPSHOT</version> <!-- Specify the current version from the pom parent -->
      </parent>
  
      <artifactId>weedow-searchy-mymodule</artifactId>
      <name>weedow-searchy-mymodule</name>
      <description>This module provides...</description>
  
      <dependencies>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-configuration-processor</artifactId>
              <optional>true</optional>
          </dependency>
  
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter</artifactId>
          </dependency>
  
          <dependency>
              <groupId>com.weedow</groupId>
              <artifactId>weedow-searchy-core</artifactId>
              <version>${project.version}</version>
          </dependency>
  
          <!-- Other dependencies -->
          ...
  
          <!-- Testing Dependencies -->
  
          <dependency>
              <groupId>com.nhaarman.mockitokotlin2</groupId>
              <artifactId>mockito-kotlin</artifactId>
              <scope>test</scope>
          </dependency>
      </dependencies>
  
      <build>
          <plugins>
              <plugin>
                  <groupId>org.jetbrains.kotlin</groupId>
                  <artifactId>kotlin-maven-plugin</artifactId>
              </plugin>
          </plugins>
      </build>
  
      <profiles>
          <profile>
              <id>release</id>
              <build>
                  <plugins>
                      <plugin>
                          <groupId>org.jetbrains.dokka</groupId>
                          <artifactId>dokka-maven-plugin</artifactId>
                      </plugin>
  
                      <!-- Add this plugin if the distribution package (distribution module) contains this modulebe and its dependencies -->
                      <plugin>
                          <groupId>org.apache.maven.plugins</groupId>
                          <artifactId>maven-dependency-plugin</artifactId>
                          <executions>
                              <execution>
                                  <phase>package</phase>
                                  <goals>
                                      <goal>copy-dependencies</goal>
                                  </goals>
                                  <configuration>
                                      <outputDirectory>${project.build.directory}/dependencies</outputDirectory>
                                      <includeScope>runtime</includeScope>
                                  </configuration>
                              </execution>
                          </executions>
                      </plugin>
                  </plugins>
              </build>
          </profile>
      </profiles>
  </project>
  ```
* Update the parent `pom.xml` file
  * Add the new module in the `<modules>` tag:
    ```
    <modules>
      ...
      <module>mymodule</module>
      ...
    </modules>
    ```
* Code coverage
  * If the module source code should be covered:
    * Update the `pom.xml` file of the report module: Add the module as dependency:
      ```xml
      <dependency>
        <groupId>com.weedow</groupId>
        <artifactId>weedow-searchy-mymodule</artifactId>
        <version>${project.version}</version>
      </dependency>
      ```
  * If the module source code should not covered:
    * Update the parent `pom.xml` file: Add the package of the new module to be excluded from the code coverage:
    ```xml
    <properties>
      <sonar.exclusions>
        ...
        **/com/weedow/searchy/mymodule/**/*
      </sonar.exclusions>
      <sonar.coverage.exclusions>
        ...
        **/com/weedow/searchy/mymodule/**/*
      </sonar.coverage.exclusions>
    </properties>
    ```
  * Add the packages (jars) of the new module in the distribution package
    * Update the `pom.xml` file of the distribution module
      * Add the new module as dependency
        ```xml
        <dependency>
          <groupId>com.weedow</groupId>
          <artifactId>weedow-searchy-mymodule</artifactId>
          <version>${project.version}</version>
        </dependency>
        ```
    * Update the `assembly.xml` file:
      ```xml
      <fileSet>
        <outputDirectory>mymodule</outputDirectory>
        <directory>${project.basedir}/../mymodule/target</directory>
        <includes>
            <include>*-mymodule-*.jar</include>
            <include>./dependencies/*.jar</include>
        </includes>
      </fileSet>
      ```