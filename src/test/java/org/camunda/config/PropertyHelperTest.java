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

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.container.impl.metadata.PropertyHelper;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.junit.jupiter.api.Test;

public class PropertyHelperTest {

  @Test
  public void shouldLoadConfigurationPropertiesFromMapWithPropertyHelper() {
    // given
    Map<String, String> properties = new HashMap<>();
    properties.put("jobExecutorActivate", "true");
    properties.put("defaultNumberOfRetries", "4");
    properties.put("processEngineName", "custom");
    StandaloneInMemProcessEngineConfiguration config = new StandaloneInMemProcessEngineConfiguration();

    // when
    PropertyHelper.applyProperties(config, properties);

    // then
    assertThat(config.getDefaultNumberOfRetries()).isEqualTo(4);
    assertThat(config.isJobExecutorActivate()).isTrue();
    assertThat(config.getProcessEngineName()).isEqualTo("custom");
  }

}