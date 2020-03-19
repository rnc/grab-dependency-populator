[![Build Status (Travis CI)](https://travis-ci.org/rnc/grab-dependency-populator.svg?branch=master)](https://travis-ci.org/rnc/grab-dependency-populator.svg?branch=master)


# Maven extension to add dependencies and repositories specified in Grape `@Grab`/`@GrabResolver` annotations.

## Overview

This extension will activate implicitly once configured. It will scan the source
recursively for `*.groovy` files and locate any `@Grab` annotations (i.e. `@Grab` and `@GrabResolver`). These will be parsed and the
resulting dependencies and repositories added to the Maven project dependencies.

## Installation

It is recommended to install this as a Core Extension (See
[here](https://maven.apache.org/ref/3.6.2/maven-embedder/core-extensions.html)).
Further details on this extension method may be found in
[Using Maven 3 lifecycle extension](https://maven.apache.org/examples/maven-3-lifecycle-extensions.html)
and
[Maven 3.3.1 Release Notes](https://maven.apache.org/docs/3.3.1/release-notes.html)

A typical extension file is:

<pre><code>&lt;extensions xmlns=&quot;http://maven.apache.org/EXTENSIONS/1.0.0&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
            xsi:schemaLocation=&quot;http://maven.apache.org/EXTENSIONS/1.0.0 http://maven.apache.org/xsd/core-extensions-1.0.0.xsd&quot;&gt;
  &lt;extension&gt;
    &lt;groupId&gt;org.goots.maven.extensions&lt;/groupId&gt;
    &lt;artifactId&gt;grab-dependency-populator&lt;/artifactId&gt;
    &lt;version&gt;1.0&lt;/version&gt;
  &lt;/extension&gt;
&lt;/extensions&gt;
</code></pre>

It may also be installed in `<maven-installation>/lib/ext` using the `jar-with-dependencies` version.

## Configuration

The following configuration options are available:

| Config Property | Default | Description |
|-----------------|---------|-------------|
| `grabPopulatorDisable` | false        | Disables the extension |
| `grabPopulatorAddAtEnd` | true        | Add the 'grabbed' dependencies at the end of the list|
| `grabPopulatorErrorOnMismatch` | true        | Throws an error if multiple `@Grab` have the same groupId / artifactId but differing versions |
| `grabPopulatorVerifyDependencies` | true        | Checks that all no grabbed dependency has a different version to native dependencies |

The extension can be configured in two different ways:

#### XML File

An xml file, named as `grabDependencyPopulator.xml` may be placed within `<project>/.mvn`. It may contain:
```
<?xml version="1.0" encoding="utf-8"?>

<configuration>
  <errorOnMismatch>true</errorOnMismatch>
  <verifyDependencies>false</verifyDependencies>
  <atEnd>false</atEnd>
  <include>
    <directory>src</directory>
    <directory>vars</directory>
  </include>
</configuration>
```
The 'include/directory' denotes a list of directories the extension will start searching from. If it is *not* included then the tool will search all directories from the root (excluding `target` build directories).

#### Properties 

The extension will also check system properties (using the naming in the table above) that are available within Maven.
