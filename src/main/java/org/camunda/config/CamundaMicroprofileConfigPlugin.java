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

import io.smallrye.config.ConfigValue;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import org.camunda.bpm.container.impl.metadata.PropertyHelper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;

public class CamundaMicroprofileConfigPlugin implements ProcessEnginePlugin {

  protected String configFilePath;

  public CamundaMicroprofileConfigPlugin() {
    this(null);
  }

  public CamundaMicroprofileConfigPlugin(String configFilePath) {
    this.configFilePath = configFilePath;
  }

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

    if (configFilePath == null) {
      ConfigValue configValue = new SmallRyeConfigBuilder()
          .addDefaultSources()
          .build()
          .getConfigValue("cambpm_conf_path");

      this.configFilePath = configValue.getValue();
    }

    try {
      SmallRyeConfig smallRyeConfig = SmallRyeConfigurator.provideSmallRyeConfig(configFilePath);
      CamundaConfig microProfileConfig = smallRyeConfig
          .getConfigMapping(CamundaConfig.class, "camunda");

      PropertyHelper.applyProperties(processEngineConfiguration, microProfileConfig.config);

    } catch (IOException e) {
      // TODO: add logging
      e.printStackTrace();
    }
  }

  @Override
  public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    // no op
  }

  @Override
  public void postProcessEngineBuild(ProcessEngine processEngine) {
    // no op
  }

  public String getConfigFilePath() {
    return configFilePath;
  }

  public void setConfigFilePath(String configFilePath) {
    this.configFilePath = configFilePath;
  }

}