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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

public class GitConfigRepo {

  protected String gitRepositoryUrl;
  protected String branchToClone;
  protected String localGitDirectory;
  protected String configFilePath;
  protected Repository gitRepository;

  public GitConfigRepo(String gitRepositoryUrl) {
    this(gitRepositoryUrl, "master", System.getProperty("user.home") + "/camunda-config/", "application.properties");
  }

  public GitConfigRepo(String gitRepositoryUrl, String branchToClone, String localGitDirectory,
                       String configFilePath) {
    this.gitRepositoryUrl = gitRepositoryUrl;
    this.branchToClone = branchToClone;
    this.localGitDirectory = localGitDirectory;
    this.configFilePath = configFilePath;
  }

  public void cloneRepository() {
    Path gitRepoPath = Paths.get(localGitDirectory);
    if (Files.exists(gitRepoPath)) {
      openGitRepository(localGitDirectory);
      syncWithRemote();
    } else {
      try (Git git = Git.cloneRepository()
          .setURI(gitRepositoryUrl)
          .setDirectory(new File(localGitDirectory))
          .setBranch(branchToClone)
          .call()) {
        this.gitRepository = git.getRepository();
      } catch (GitAPIException e) {
        // TODO: add logging
        e.printStackTrace();
      }
    }
  }

  public void syncWithRemote() {
    try (Git git = new Git(gitRepository)) {
      git.pull().call();
    } catch (CanceledException e) {
      e.printStackTrace();
    } catch (Exception e) {
      // TODO: add logging
      e.printStackTrace();
    }
  }

  public void openGitRepository(String path) {
    try (Git git = Git.open(new File(path))) {
      this.gitRepository = git.getRepository();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getLocalGitDirectory() {
    return localGitDirectory;
  }

  public String getConfigFilePath() {
    return configFilePath;
  }
}