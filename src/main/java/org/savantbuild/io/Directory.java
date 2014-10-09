/*
 * Copyright (c) 2014, Inversoft Inc., All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.savantbuild.io;

import java.nio.file.attribute.FileTime;

/**
 * A directory that might be empty or might contain other directories or files.
 *
 * @author Brian Pontarelli
 */
public class Directory implements Comparable<Directory> {
  public String groupName;

  public FileTime lastModifiedTime;

  public Integer mode;

  public String name;

  public String userName;

  public Directory(String name, Integer mode, String userName, String groupName, FileTime lastModifiedTime) {
    this.name = name;
    this.groupName = groupName;
    this.mode = mode;
    this.userName = userName;
    this.lastModifiedTime = lastModifiedTime;
  }

  @Override
  public int compareTo(Directory o) {
    return name.compareTo(o.name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Directory directory = (Directory) o;
    return name.equals(directory.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /**
   * Sets the groupName.
   *
   * @param groupName The groupName
   * @return This.
   */
  public Directory withGroupName(String groupName) {
    this.groupName = groupName;
    return this;
  }

  /**
   * Sets the lastModifiedTime.
   *
   * @param lastModifiedTime The lastModifiedTime
   * @return This.
   */
  public Directory withLastModifiedTime(FileTime lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
    return this;
  }

  /**
   * Sets the mode.
   *
   * @param mode The mode
   * @return This.
   */
  public Directory withMode(Integer mode) {
    this.mode = mode;
    return this;
  }

  /**
   * Sets the name.
   *
   * @param name The name.
   * @return This.
   */
  public Directory withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the userName.
   *
   * @param userName The userName
   * @return This.
   */
  public Directory withUserName(String userName) {
    this.userName = userName;
    return this;
  }
}
