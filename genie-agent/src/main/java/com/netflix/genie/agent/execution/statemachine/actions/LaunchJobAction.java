/*
 *
 *  Copyright 2018 Netflix, Inc.
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
package com.netflix.genie.agent.execution.statemachine.actions;

import com.netflix.genie.agent.cli.UserConsole;
import com.netflix.genie.agent.execution.ExecutionContext;
import com.netflix.genie.agent.execution.exceptions.ChangeJobStatusException;
import com.netflix.genie.agent.execution.exceptions.JobLaunchException;
import com.netflix.genie.agent.execution.process.JobProcessManager;
import com.netflix.genie.agent.execution.services.AgentJobService;
import com.netflix.genie.agent.execution.statemachine.Events;
import com.netflix.genie.common.dto.JobStatus;
import com.netflix.genie.common.dto.JobStatusMessages;
import com.netflix.genie.common.internal.dto.v4.JobSpecification;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;

/**
 * Action performed when in state LAUNCH_JOB.
 *
 * @author mprimi
 * @since 4.0.0
 */
@Slf4j
class LaunchJobAction extends BaseStateAction implements StateAction.LaunchJob {

    private final JobProcessManager jobProcessManager;
    private final AgentJobService agentJobService;

    LaunchJobAction(
        final ExecutionContext executionContext,
        final JobProcessManager jobProcessManager,
        final AgentJobService agentJobService
    ) {
        super(executionContext);
        this.jobProcessManager = jobProcessManager;
        this.agentJobService = agentJobService;
    }

    @Override
    protected void executePreActionValidation() {
        assertClaimedJobIdPresent();
        assertCurrentJobStatusEqual(JobStatus.INIT);
        assertJobSpecificationPresent();
        assertJobDirectoryPresent();
        assertJobEnvironmentPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Events executeStateAction(final ExecutionContext executionContext) {
        UserConsole.getLogger().info("Launching job...");

        final JobSpecification jobSpec = executionContext.getJobSpecification().get();
        final File jobDirectory = executionContext.getJobDirectory().get();
        final Map<String, String> jobEnvironment = executionContext.getJobEnvironment().get();
        final boolean interactive = jobSpec.isInteractive();

        try {
            this.jobProcessManager.launchProcess(
                jobDirectory,
                jobEnvironment,
                jobSpec.getExecutableArgs(),
                jobSpec.getJobArgs(),
                interactive,
                jobSpec.getTimeout().orElse(null)
            );
        } catch (final JobLaunchException e) {
            throw new RuntimeException("Failed to launch job", e);
        }

        try {
            this.agentJobService.changeJobStatus(
                executionContext.getClaimedJobId().get(),
                JobStatus.INIT,
                JobStatus.RUNNING,
                JobStatusMessages.JOB_RUNNING
            );
            executionContext.setCurrentJobStatus(JobStatus.RUNNING);
        } catch (final ChangeJobStatusException e) {
            throw new RuntimeException("Failed to update job status", e);
        }

        return Events.LAUNCH_JOB_COMPLETE;
    }

    @Override
    protected void executePostActionValidation() {
        assertCurrentJobStatusEqual(JobStatus.RUNNING);
    }
}
