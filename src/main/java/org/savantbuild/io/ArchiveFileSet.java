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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * A FileSet for archives. This allows the files in the FileSet to optionally contain a prefix.
 *
 * @author Brian Pontarelli
 */
public class ArchiveFileSet extends FileSet {
  public String dirGroupName;

  public Integer dirMode;

  public String dirUserName;

  public String groupName;

  public Integer mode;

  public String prefix;

  public String userName;

  /**
   * Constructs a new ArchiveFileSet.
   *
   * @param directory The directory of the FileSet.
   */
  public ArchiveFileSet(Path directory) {
    super(directory);
  }

  /**
   * Constructs a new ArchiveFileSet. The directory is required but the prefix is optional. Leaving the prefix blank
   * will cause all of the files in the FileSet to contain relative paths based on the FileSet's directory. Using the
   * prefix will cause the files in the FileSet to be relative to the prefix plus the directory.
   *
   * @param directory The directory of the FileSet.
   * @param prefix    The prefix used to calculate the relative paths in the FileInfo objects.
   */
  public ArchiveFileSet(Path directory, String prefix) {
    super(directory);
    this.prefix = prefix;
  }

  /**
   * Constructs a new ArchiveFileSet. The directory is required but the prefix is optional. Leaving the prefix blank
   * will cause all of the files in the FileSet to contain relative paths based on the FileSet's directory. Using the
   * prefix will cause the files in the FileSet to be relative to the prefix plus the directory.
   *
   * @param directory       The directory of the FileSet.
   * @param prefix          (Optional) The prefix used to calculate the relative paths in the FileInfo objects.
   * @param mode            (Optional) The POSIX file mode.
   * @param userName        (Optional) The user name for the fileset.
   * @param groupName       (Optional) The group name for the fileset.
   * @param dirUserName     (Optional) The user name that the directories created by this ArchiveFileSet will use.
   * @param dirGroupName    (Optional) The group name that the directories created by this ArchiveFileSet will use.
   * @param dirMode         (Optional) The mode that the directories created by this ArchiveFileSet will use.
   * @param includePatterns (Optional) A list of regular expression Pattern objects that list the files to include.
   * @param excludePatterns (Optional) A list of regular expression Pattern objects that list the files to exclude.
   */
  public ArchiveFileSet(Path directory, String prefix, Integer mode, String userName, String groupName,
                        String dirUserName, String dirGroupName, Integer dirMode, Collection<Pattern> includePatterns,
                        Collection<Pattern> excludePatterns) {
    super(directory, includePatterns, excludePatterns);
    this.prefix = prefix;
    this.mode = mode;
    this.userName = userName;
    this.groupName = groupName;
    this.dirGroupName = dirGroupName;
    this.dirUserName = dirUserName;
    this.dirMode = dirMode;
  }

  /**
   * Overrides the parent method, but uses the {@link #dirGroupName}, {@link #dirUserName} and {@link #dirMode}
   * variables to set the mode, userName and groupName inside the returned Directory objects.
   *
   * @return The set of directories.
   * @throws IOException If the build fails.
   */
  @Override
  public Set<Directory> toDirectories() throws IOException {
    List<FileInfo> infos = super.toFileInfos();
    Set<Directory> directories = new TreeSet<>();
    for (FileInfo info : infos) {
      Path relativeDir = info.relative.getParent();
      Path originDir = info.origin.getParent();
      while (relativeDir != null) {
        Integer mode = dirMode != null ? dirMode : FileTools.toHexMode(Files.getPosixFilePermissions(originDir));
        String userName = dirUserName != null ? dirUserName : Files.getOwner(originDir).getName();
        String groupName = dirGroupName != null ? dirGroupName : Files.readAttributes(originDir, PosixFileAttributes.class).group().getName();
        FileTime lastModifiedTime = Files.getLastModifiedTime(originDir);
        directories.add(new Directory(relativeDir.toString(), mode, userName, groupName, lastModifiedTime));

        relativeDir = relativeDir.getParent();
        originDir = originDir.getParent();
      }
    }

    return directories;
  }

  @Override
  public List<FileInfo> toFileInfos() throws IOException {
    List<FileInfo> infos = super.toFileInfos();
    if (prefix != null) {
      infos.forEach((info) -> info.relative = Paths.get(prefix, info.relative.toString()));
    }
    if (mode != null) {
      infos.forEach((info) -> info.permissions = FileTools.toPosixPermissions(FileTools.toMode(mode)));
    }
    if (userName != null) {
      infos.forEach((info) -> info.userName = userName);
    }
    if (groupName != null) {
      infos.forEach((info) -> info.groupName = groupName);
    }

    return infos;
  }

  /**
   * Sets the dirGroupName.
   *
   * @param dirGroupName The dirGroupName.
   * @return This.
   */
  public ArchiveFileSet withDiGroupName(String dirGroupName) {
    this.dirGroupName = dirGroupName;
    return this;
  }

  /**
   * Sets the dirMode.
   *
   * @param dirMode The dirMode.
   * @return This.
   */
  public ArchiveFileSet withDirMode(Integer dirMode) {
    this.dirMode = dirMode;
    return this;
  }

  /**
   * Sets the dirUserName.
   *
   * @param dirUserName The dirUserName.
   * @return This.
   */
  public ArchiveFileSet withDirUserName(String dirUserName) {
    this.dirUserName = dirUserName;
    return this;
  }

  /**
   * Sets the groupName.
   *
   * @param groupName The groupName.
   * @return This.
   */
  public ArchiveFileSet withGroupName(String groupName) {
    this.groupName = groupName;
    return this;
  }

  /**
   * Sets the mode.
   *
   * @param mode The mode.
   * @return This.
   */
  public ArchiveFileSet withMode(Integer mode) {
    this.mode = mode;
    return this;
  }

  /**
   * Sets the prefix.
   *
   * @param prefix The prefix.
   * @return This.
   */
  public ArchiveFileSet withPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  /**
   * Sets the userName.
   *
   * @param userName The userName.
   * @return This.
   */
  public ArchiveFileSet withUserName(String userName) {
    this.userName = userName;
    return this;
  }
}
