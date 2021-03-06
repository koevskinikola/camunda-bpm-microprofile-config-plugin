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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MicroprofileConfigTest {

  protected ProcessEngine processEngine;

  @AfterEach
  protected void cleanUp() {
    if (processEngine != null) {
      processEngine.close();
    }
  }

  @Test
  public void shouldLoadCustomPropertiesConfigFileFromClasspath() {
    // given
    ProcessEngineConfigurationImpl configuration
        = ConfigTestHelper.setupProcessEngineConfiguration("application.properties");

    // when
    processEngine = configuration.buildProcessEngine();

    // then
    ConfigTestHelper.assertValues(processEngine, true, 9, "customEngine");
  }

  @Test
  public void shouldLoadCustomYamlConfigFileFromClasspath() {
    // given
    ProcessEngineConfigurationImpl configuration
        = ConfigTestHelper.setupProcessEngineConfiguration("application.yaml");

    // when
    processEngine = configuration.buildProcessEngine();

    // then
    ConfigTestHelper.assertValues(processEngine, true, 10, "customEngineYaml");
  }

  @Test
  public void shouldLoadYamlConfigFileFromClasspath() {
    // given
    ProcessEngineConfigurationImpl configuration
        = ConfigTestHelper.setupProcessEngineConfiguration(null);

    // when
    processEngine = configuration.buildProcessEngine();

    // then
    ConfigTestHelper.assertValues(processEngine, true, 10, "customEngineYaml");
  }

  @Test
  public void shouldLoadPropertiesConfigFileFromFilesystem(@TempDir Path tempDir) throws IOException {
    // given
    String fileName = "application.properties";
    createTmpFile(tempDir.toString() + "/", fileName);
    ProcessEngineConfigurationImpl configuration
        = ConfigTestHelper.setupProcessEngineConfiguration(tempDir.toString() + "/" + fileName);

    // when
    processEngine = configuration.buildProcessEngine();

    // then
    ConfigTestHelper.assertValues(processEngine, true, 9, "customEngine");
  }

  @Test
  public void shouldLoadPropertiesConfigFileFromUrl(@TempDir Path tempDir) throws IOException {
    // given
    String fileName = "application.properties";
    createTmpFile(tempDir.toString() + "/", fileName);
    ProcessEngineConfigurationImpl configuration
        = ConfigTestHelper.setupProcessEngineConfiguration("file://" + tempDir.toString() + "/" + fileName);

    // when
    processEngine = configuration.buildProcessEngine();

    // then
    ConfigTestHelper.assertValues(processEngine, true, 9, "customEngine");
  }

  @Test
  public void shouldLoadPropertiesConfigFileFromGitRepo() {
    // given
    String gitUrl = "https://github.com/koevskinikola/cambpm-test-config.git";
    ProcessEngineConfigurationImpl configuration
        = ConfigTestHelper.setupProcessEngineConfiguration(gitUrl);

    // when
    processEngine = configuration.buildProcessEngine();

    // then
    ConfigTestHelper.assertValues(processEngine, true, 9, "customEngine");
  }

  protected void createTmpFile(String filePath, String fileName) throws IOException {
    try (FileWriter configFile = new FileWriter(filePath + fileName)){
      configFile.write(SmallRyeConfigurator.readFileFromClasspath(fileName));
    }
  }

}