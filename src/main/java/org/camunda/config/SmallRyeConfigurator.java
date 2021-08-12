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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import io.smallrye.config.common.utils.ConfigSourceUtil;
import io.smallrye.config.source.yaml.YamlConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSource;

public class SmallRyeConfigurator {

  protected static final String DEFAULT_PROPS_FILE = "/application.properties";
  protected static final String DEFAULT_YAML_FILE = "/application.yaml";
  public static final String YAML = "yaml";
  public static final String PROPS = "properties";
  public static final String GIT = "git";

  public static SmallRyeConfig provideSmallRyeConfig(String url) throws IOException {
    List<ConfigSource> configSources = new ArrayList<>();
    if (url != null) {
      String configType = determineFileType(url);
      String configSourceContent;
      if (Paths.get(url).isAbsolute()) {
        configSourceContent = readFileFromFilesystem(url);
      } else if (isUrlValid(url)) {
        configSourceContent = readFileFromUrl(url);
      } else {
        configSourceContent = readFileFromClasspath(url);
      }
      ConfigSource customSource = SmallRyeConfigurator.provideConfigSource(configSourceContent, configType);
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

  public static ConfigSource provideConfigSource(String configContent, String configType) {
    try {
      if (YAML.equals(configType)) {
        return new YamlConfigSource("externalConfig", configContent, 700);
      } else {
        Properties properties = new Properties();
        properties.load(new StringReader(configContent));
        return new PropertiesConfigSource(ConfigSourceUtil.propertiesToMap(properties), "externalConfig", 700);
      }
    } catch (IOException e) {
      // TODO: add logging for missing file
      e.printStackTrace();
    }

    return null;
  }

  public static String determineFileType(String url) {
    String[] urlSegments = url.split("\\.");
    String fileType = urlSegments[urlSegments.length-1];
    if (fileType.equals("yaml") ||fileType.equals("yml")) {
      return YAML;
    } else if (fileType.equals("properties")) {
      return PROPS;
    } else if (fileType.equals("git")) {
      return GIT;
    } else {
      throw new RuntimeException("Please provide a correct configuration file");
    }
  }

  protected static String readFileFromClasspath(String classPath) {
    ClassLoader classLoader = SmallRyeConfigurator.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(classPath);
    String data = readFromInputStream(inputStream);
    return data;
  }

  protected static String readFileFromFilesystem(String path) {
    String data = "";
    byte[] encoded;
    try {
      encoded = Files.readAllBytes(Paths.get(path));
      data = new String(encoded, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return data;
  }

  protected static String readFileFromUrl(String url) {
    URL configFile = null;
    String data = "";
    try {
      configFile = new URL(url);
      URLConnection urlConnection = configFile.openConnection();
      InputStream inputStream = urlConnection.getInputStream();
      data = readFromInputStream(inputStream);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return data;
  }

  protected static String readFromInputStream(InputStream inputStream) {
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br
             = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = br.readLine()) != null) {
        resultStringBuilder.append(line).append("\n");
      }
    } catch (IOException e) {
      // TODO: add logging
      e.printStackTrace();
    }
    return resultStringBuilder.toString();
  }

  protected static boolean isUrlValid(String url) {
    try {
      new URL(url);
    } catch (MalformedURLException e) {
      return false;
    }

    return true;
  }

}