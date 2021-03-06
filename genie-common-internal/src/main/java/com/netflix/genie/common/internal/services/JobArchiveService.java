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
package com.netflix.genie.common.internal.services;

import com.netflix.genie.common.internal.exceptions.checked.JobArchiveException;

import java.net.URI;
import java.nio.file.Path;

/**
 * A service which is responsible for taking the files related to running a Genie job and backing them up to a different
 * location.
 *
 * @author standon
 * @author tgianos
 * @since 4.0.0
 */
public interface JobArchiveService {

    /**
     * The subdirectory within the job directory where the manifest will be placed.
     */
    String MANIFEST_DIRECTORY = "genie";

    /**
     * The name of job manifest file generated by the system.
     */
    String MANIFEST_NAME = "manifest.json";

    /**
     * Backup the contents of the given directory to the target location. This will recursively backup ALL the files
     * and sub-directories within the given directory to the target.
     *
     * @param directory {@link Path} to the directory to archive
     * @param targetURI target {@link URI} for the root archive location
     * @throws JobArchiveException if archival fails
     */
    void archiveDirectory(Path directory, URI targetURI) throws JobArchiveException;
}
