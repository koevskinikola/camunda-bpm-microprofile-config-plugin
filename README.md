# MicroProfile Config integration plugin for the Camunda Platform

This plugin adds support for [MicroProfile Config](https://www.eclipse.org/community/eclipse_newsletter/2017/september/article3.php) 
for the Camunda Platform, i.e. it loads a `ProcessEngineConfiguration` instance with properties provided by 
a `MicroProfile ConfigSource`.

This plugin uses the [SmallRye Config](https://smallrye.io/docs/smallrye-config/index.html) implementation of the 
MicroProfile Config specification.

It provides support for the following `ConfigSources`. 

* `PropertiesConfigSource|YamlConfigSource` for a provided file URL.
* `YamlConfigSource` for `application.yaml`
* `PropertiesConfigSource` for `application.properties`
* `PropertiesConfigSource` for `META-INF/microprofile-config.properties`
* `SysPropConfigSource`
* `EnvConfigSource`


