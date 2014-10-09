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
package org.savantbuild.io.zip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Enumeration;
import java.util.Set;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.savantbuild.io.FileTools;

/**
 * Collection of ZIP file tools.
 *
 * @author Brian Pontarelli
 */
public class ZipTools {
  /**
   * Unzips a ZIP file to a directory.
   *
   * @param file The ZIP file to unzip.
   * @param to   The directory to unzip to.
   * @throws IOException If the unzip fails.
   */
  public static void unzip(Path file, Path to) throws IOException {
    if (Files.notExists(to)) {
      Files.createDirectories(to);
    }

    ZipFile zipFile = new ZipFile(file.toFile());
    Enumeration<ZipEntry> entries = zipFile.getEntries();
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      Path entryPath = to.resolve(entry.getName());
      if (!entry.isDirectory()) {
        if (Files.notExists(entryPath.getParent())) {
          Files.createDirectories(entryPath.getParent());
        }

        if (Files.isRegularFile(entryPath)) {
          if (Files.size(entryPath) == entry.getSize()) {
            continue;
          } else {
            Files.delete(entryPath);
          }
        }

        Files.createFile(entryPath);

        try (OutputStream os = Files.newOutputStream(entryPath)) {
          InputStream is = zipFile.getInputStream(entry);
          byte[] ba = new byte[1024];
          int read;
          while ((read = is.read(ba)) != -1) {
            if (read > 0) {
              os.write(ba, 0, read);
            }
          }
        }

        int unixMode = entry.getUnixMode();
        if (unixMode != 0) {
          Set<PosixFilePermission> permissions = FileTools.toPosixPermissions(unixMode);
          Files.setPosixFilePermissions(entryPath, permissions);
        }
      }
    }

    zipFile.close();
  }
}
