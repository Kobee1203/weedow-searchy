# Make a new release

This page describes the different steps to make a new release.

* Check if all commits for the next release are present on the `dev` branch
* Checkout the `dev` branch
* Update the parent `pom.xml` file with the next version suffixed with `-SNAPSHOT`\
  _Example when the next version is `1.1.0`:_
  ```xml
  <project xmlns="..." xmlns:xsi="..." xsi:schemaLocation="...">
    ...
    <groupId>com.weedow</groupId>
    <artifactId>weedow-searchy-parent</artifactId>
    <version>1.1.0-SNAPSHOT</version>
    ...
  </project>
  ```
* Update the other `pom.xml` files with the same _SNAPSHOT_ version\
  _Example in the `pom.xml` file of the `core` module:_
  ```xml
  <project xmlns="..." xmlns:xsi="..." xsi:schemaLocation="...">
    ...
    <parent>
      <groupId>com.weedow</groupId>
      <artifactId>weedow-searchy-parent</artifactId>
      <version>1.1.0-SNAPSHOT</version>
    </parent>

    <artifactId>weedow-searchy-core</artifactId>
    <name>weedow-searchy-core</name>
    <description>Searchy Core Library</description>
    ...
  </project>
  ```
* Update the `README.md` file with the next version. Replace everywhere the last release version with the next version.\
  _Example in the `Installation` section, when the next version is `1.1.0`:_
  ```markdown
  * If you have a [Maven](https://maven.apache.org/) project, you can add the following dependency in your `pom.xml` file:
  <dependency>
      <groupId>com.weedow</groupId>
      <artifactId>weedow-searchy-jpa</artifactId>
      <version>1.1.0</version>
  </dependency>
  * If you have a [Gradle](https://gradle.org/) project, you can add the following dependency in your `build.gradle` file:
  implementation "com.weedow:weedow-searchy-jpa:1.1.0"
  ```
  **Do the same thing everywhere in the `README.md` file**
* Commit and push the changes
* Create a Pull Request from `dev` into `master`\
  _Github Workflow `release.yml` checks if there is a git push in the `master` branch in order to release a new version._\
  _Currently, the `master` branch is only used for release creation._\
  _**DO NOT COMMIT AND PUSH DIRECTLY IN THE `MASTER` BRANCH, OTHERWISE A NEW RELEASE WILL BE CREATED!**_
* Wait for the analysis to be completed, and Merge the PR
* Wait for the `Release` Github Action is completed
* Check if the new git tag related to the new release has been created
* Check if the _Github Releases_ page contains the new release
* Check if the `master` branch contains the following commits:
  ```
  [maven-release-plugin] prepare release 1.0.0
  [maven-release-plugin] prepare for next development iteration
  ```
* Create a Pull Request from `master` into `dev`\
  _The release creation is finished. The main branch `dev` has to be updated with the last commits containing the next `SNAPSHOT` version_
* Wait for the analysis to be completed, and Merge the PR