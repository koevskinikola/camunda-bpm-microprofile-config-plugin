/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.config;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import io.smallrye.config.source.yaml.YamlConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSource;

public class SmallRyeConfigurator {

  protected static final String DEFAULT_PROPS_FILE = "/application.properties";
  protected static final String DEFAULT_YAML_FILE = "/application.yaml";
  protected static final String YAML = "yaml";
  protected static final String PROPS = "properties";

  public static SmallRyeConfig provideSmallRyeConfig(String url) throws IOException {
    List<ConfigSource> configSources = new ArrayList<>();
    if (url != null) {
      ConfigSource customSource = SmallRyeConfigurator.provideConfigSource(url);
      if (customSource != null) {
        configSources.add(customSource);
      }
    }

    URL propsUrl = SmallRyeConfigurator.class.getResource(DEFAULT_PROPS_FILE);
    URL yamlUrl = SmallRyeConfigurator.class.getResource(DEFAULT_YAML_FILE);

    configSources.add(new PropertiesConfigSource(propsUrl, 500));
    configSources.add(new YamlConfigSource(yamlUrl, 600));

    SmallRyeConfig smallRyeConfig = new SmallRyeConfigBuilder()
        .addDefaultSources()
        .withSources(configSources)
        .withMapping(CamundaConfig.class, "camunda")
        .build();

    return smallRyeConfig;
  }

  public static ConfigSource provideConfigSource(String url) {
    String configType = determingFileType(url);
    try {
      URL configFile = SmallRyeConfigurator.class.getResource(url);
      if (YAML.equals(configType)) {
        return new YamlConfigSource(configFile, 700);
      } else {
        return new PropertiesConfigSource(configFile, 700);
      }
    } catch (IOException e) {
      // TODO: add logging for missing file
      e.printStackTrace();
    }

    return null;
  }

  protected static String determingFileType(String url) {
    String[] urlSegments = url.split("\\.");
    String fileType = urlSegments[urlSegments.length-1];
    if (fileType.equals("yaml") ||fileType.equals("yml")) {
      return YAML;
    } else if (fileType.equals("properties")) {
      return PROPS;
    } else {
      throw new RuntimeException("Please provide a correct configuration file");
    }
  }

}