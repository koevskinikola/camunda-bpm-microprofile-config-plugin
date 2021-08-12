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

import io.smallrye.config.ConfigValue;
import io.smallrye.config.SmallRyeConfigBuilder;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SystemConfigPropertyTest {

  protected ProcessEngine processEngine;

  @AfterEach
  protected void cleanUp() {
    if (processEngine != null) {
      processEngine.close();
    }
  }

  @BeforeEach
  protected void setUp() {
    System.setProperty("cambpm_conf_path", "application.yaml");
  }

  @AfterEach
  protected void tearDown() {
    System.clearProperty("cambpm_conf_path");
  }

  @Test
  public void shouldReadSystemProperty() {
    // given
    ProcessEngineConfigurationImpl configuration
        = ConfigTestHelper.setupProcessEngineConfiguration(null);

    // when
    processEngine = configuration.buildProcessEngine();

    // then
    ConfigTestHelper.assertValues(processEngine, true, 10, "customEngineYaml");
  }
}