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
package com.netflix.genie.web.properties;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Properties related to launching Agent processes locally.
 *
 * @author tgianos
 * @since 4.0.0
 */
@ConfigurationProperties(prefix = LocalAgentLauncherProperties.PROPERTY_PREFIX)
@Getter
@Setter
@Validated
public class LocalAgentLauncherProperties {

    /**
     * Prefix for all properties related to the local agent launcher.
     */
    public static final String PROPERTY_PREFIX = "genie.agent.launcher.local";

    /**
     * The command that should be run to execute the Genie agent. Required.
     */
    @NotEmpty(message = "The agent executable cannot be empty or it will be impossible to launch an agent process")
    private List<@NotBlank String> executable = Lists.newArrayList("java", "-jar", "/tmp/genie-agent.jar");

    /**
     * Defaults to 10 GB (10,240 MB).
     */
    @Min(value = 1, message = "The minimum value is 1MB but the value should likely be much higher")
    private int maxJobMemory = 10_240;

    /**
     * Default to 30 GB (30,720 MB).
     */
    @Min(value = 1L, message = "The minimum value is 1MB but the value should likely be set much higher")
    private long maxTotalJobMemory = 30_720L;
}
