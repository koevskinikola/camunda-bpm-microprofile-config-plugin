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
package org.camunda.config.gitsource;

import java.util.Set;

import org.camunda.config.SmallRyeConfigurator;
import org.eclipse.microprofile.config.spi.ConfigSource;

public class GitConfigSource implements ConfigSource {

//  protected int ordinal;
  protected GitConfigRepo gitConfigRepo;
  protected String configFilePath;
  protected String configType;
  protected ConfigSource delegateConfigSource;

  public GitConfigSource(String gitRepoUrl, int ordinal) {
//    this.ordinal = ordinal;
    this.gitConfigRepo = new GitConfigRepo(gitRepoUrl);
    this.gitConfigRepo.cloneRepository();
    this.configFilePath = gitConfigRepo.getLocalGitDirectory() + gitConfigRepo.getConfigFilePath();
    String configContent = SmallRyeConfigurator.readFileFromFilesystem(this.configFilePath);
    this.configType = SmallRyeConfigurator.determineFileType(this.configFilePath);
    this.delegateConfigSource = SmallRyeConfigurator.provideConfigSource(configContent, configType);
  }

    @Override
  public Set<String> getPropertyNames() {
    gitConfigRepo.syncWithRemote();
    return delegateConfigSource.getPropertyNames();
  }

  @Override
  public String getValue(String propertyName) {
    gitConfigRepo.syncWithRemote();
    return delegateConfigSource.getValue(propertyName);
  }

  @Override
  public String getName() {
     gitConfigRepo.syncWithRemote();
    return delegateConfigSource.getName();
  }

  @Override
  public int getOrdinal() {
    return this.delegateConfigSource.getOrdinal();
  }
}