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

import java.util.Collections;
import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;

public class ConfigTestHelper {

  protected static ProcessEngineConfigurationImpl setupProcessEngineConfiguration(String configUrl) {
    StandaloneInMemProcessEngineConfiguration configuration
        = new StandaloneInMemProcessEngineConfiguration();

    List<ProcessEnginePlugin> plugins = configuration.getProcessEnginePlugins();
    plugins = (plugins == null) ? Collections.emptyList() : plugins;

    plugins.add(new CamundaMicroprofileConfigPlugin(configUrl));
    configuration.setProcessEnginePlugins(plugins);

    return configuration;
  }

  protected static void assertValues(ProcessEngine processEngine,
                              boolean jobExecutorActive,
                              int defaultRetryNumber,
                              String engineName) {

    ProcessEngineConfigurationImpl configuration
        = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
    assertThat(configuration.isJobExecutorActivate()).isEqualTo(jobExecutorActive);
    assertThat(configuration.getDefaultNumberOfRetries()).isEqualTo(defaultRetryNumber);
    assertThat(configuration.getProcessEngineName()).isEqualTo(engineName);
  }
}