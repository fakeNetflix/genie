/*
 *
 *  Copyright 2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.common.internal.services.impl

import com.netflix.genie.common.internal.dto.DirectoryManifest
import com.netflix.genie.common.internal.exceptions.checked.JobArchiveException
import com.netflix.genie.common.internal.services.JobArchiveService
import com.netflix.genie.common.internal.services.JobArchiver
import com.netflix.genie.common.util.GenieObjectMapper
import org.apache.commons.lang3.StringUtils
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Specifications for {@link JobArchiveServiceImpl}.
 *
 * @author tgianos
 */
class JobArchiveServiceImplSpec extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    def "When archiveDirectory is invoked a valid manifest is written into the expected directory"() {
        def archiver = new JobArchiver() {
            @Override
            boolean archiveDirectory(final Path directory, final URI target) throws JobArchiveException {
                return false
            }
        }
        DirectoryManifest.Factory directoryManifestFactory = Mock(DirectoryManifest.Factory)
        def service = new JobArchiveServiceImpl([archiver], directoryManifestFactory)
        def jobDirectory = this.temporaryFolder.newFolder().toPath()
        Files.write(jobDirectory.resolve("someFile"), UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8))
        Files.createDirectory(jobDirectory.resolve("subDir"))
        def target = this.temporaryFolder.newFolder().toURI()
        def manifestDirectoryPath = StringUtils.isBlank(JobArchiveService.MANIFEST_DIRECTORY)
            ? jobDirectory
            : jobDirectory.resolve(JobArchiveService.MANIFEST_DIRECTORY)
        def manifestPath = manifestDirectoryPath.resolve(JobArchiveService.MANIFEST_NAME)
        def originalManifest = new DirectoryManifest.Factory().getDirectoryManifest(jobDirectory, true)

        when:
        service.archiveDirectory(jobDirectory, target)

        then:
        1 * directoryManifestFactory.getDirectoryManifest(jobDirectory, true) >> originalManifest
        Files.exists(manifestPath)

        when:
        def manifest = GenieObjectMapper.getMapper().readValue(manifestPath.toFile(), DirectoryManifest)

        then:
        manifest.getNumDirectories() == 2
        manifest.getNumFiles() == 1
        manifest == originalManifest
    }
}
