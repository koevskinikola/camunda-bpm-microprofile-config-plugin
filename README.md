# MicroProfile Config integration plugin for the Camunda Platform

> Note: This plugin is a prototype to showcase an integration with the MicroProfile Config specification.
> It provides an uber jar, where the `smallrye-config-core`, `smallrye-config-source-yaml`, and `jgit` dependencies
> are included. The plugin currently works with the Camunda Platform Tomcat distro.

This plugin adds support for [MicroProfile Config](https://www.eclipse.org/community/eclipse_newsletter/2017/september/article3.php) 
for the Camunda Platform, i.e., it loads a `ProcessEngineConfiguration` instance with properties provided by 
a `MicroProfile ConfigSource`.

This plugin uses the [SmallRye Config](https://smallrye.io/docs/smallrye-config/index.html) implementation of the 
MicroProfile Config specification.

## How to use it

Depending on your project setup you can define a configuration file on your classpath, in your file system, or
on an external system (accessible through a URL, e.g.: a [Git config repo](https://github.com/koevskinikola/camunda-bpm-microprofile-config-plugin)).

> Note: if using a Git config repo, it should be public.

The properties should be defined under the `camunda.config` prefix. 

There is also a special `cambpm_conf_path` property that defines a configuration file URL. It should only be defined
as a system property, or environment variable.

The plugin should be added in your `bpm-platform.xml` (yes, you still need to do this, it's a process engine plugin):

```xml
<bpm-platform>
  ...
  <process-engine name="...">
    ...
    <plugins>
      ...
      <!-- plugin enabling microprofile config support -->
      <plugin>
        <class>org.camunda.config.CamundaMicroprofileConfigPlugin</class>
        <properties>
          <!-- you don't have to set this property, if you define the `cambpm_conf_path`
               as a system property, or environment variable -->
          <property name="configFilePath">https://github.com/koevskinikola/cambpm-test-config.git</property>
        </properties>
      </plugin>
    </plugins>
  </process-engine>
</bpm-platform>
```

The plugin provides support for the following `ConfigSources`: 

1. `GitConfigSource|PropertiesConfigSource|YamlConfigSource` for a provided file URL.
1. `YamlConfigSource` for `classpath:/application.yaml`
1. `PropertiesConfigSource` for `classpath:/application.properties`
1. `PropertiesConfigSource` for `META-INF/microprofile-config.properties`
1. `SysPropConfigSource` - system property config source.
1. `EnvConfigSource` - environment variable config source.

You can have multiple configuration files. However, the properties defined in config sources with a higher priority
will override the ones in lower priority config sources. The list above is ordered by descending priority.


