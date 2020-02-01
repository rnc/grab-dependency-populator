[![Build Status (Travis CI)](https://travis-ci.org/rnc/grab-dependency-populator.svg?branch=master)](https://travis-ci.org/rnc/grab-dependency-populator.svg?branch=master)


# Maven extension to add dependencies specified in Grape `@Grab` annotations.

## Overview

This extension will activate implicitly once configured. It will scan the source
recursively for `*.groovy` files and locate any `@Grab` annotations. These will be parsed and the
resulting dependency added to the Maven project dependencies.

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
    &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
  &lt;/extension&gt;
&lt;/extensions&gt;
</code></pre>

It may also be installed in `<maven-installation>/lib/ext` using the `jar-with-dependencies` version.

## Configuration

There is no configuration required for this plugin. It can be disabled
by setting `grab.extension.disable` to true.


## Future

* Handle GrabResolvers as well and add the repositories to the Maven project
* Configurable error if multiple Grab's have differing versions (but same group:artifact)
