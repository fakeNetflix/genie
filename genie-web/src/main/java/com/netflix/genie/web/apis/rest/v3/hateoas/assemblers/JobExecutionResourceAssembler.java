/*
 *
 *  Copyright 2015 Netflix, Inc.
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
package com.netflix.genie.web.apis.rest.v3.hateoas.assemblers;

import com.netflix.genie.common.dto.JobExecution;
import com.netflix.genie.common.exceptions.GenieException;
import com.netflix.genie.web.apis.rest.v3.controllers.JobRestController;
import com.netflix.genie.web.apis.rest.v3.hateoas.resources.JobExecutionResource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

/**
 * Assembles Job Request resources out of JobRequest DTOs.
 *
 * @author tgianos
 * @since 3.0.0
 */
public class JobExecutionResourceAssembler implements ResourceAssembler<JobExecution, JobExecutionResource> {

    private static final String JOB_LINK = "job";
    private static final String REQUEST_LINK = "request";
    private static final String OUTPUT_LINK = "output";
    private static final String STATUS_LINK = "status";
    private static final String METADATA_LINK = "metadata";

    /**
     * {@inheritDoc}
     */
    @Override
    public JobExecutionResource toResource(final JobExecution jobExecution) {
        final String id = jobExecution.getId().orElseThrow(IllegalArgumentException::new);
        final JobExecutionResource jobExecutionResource = new JobExecutionResource(jobExecution);

        try {
            jobExecutionResource.add(
                ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder
                        .methodOn(JobRestController.class)
                        .getJobExecution(id)
                ).withSelfRel()
            );

            jobExecutionResource.add(
                ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder
                        .methodOn(JobRestController.class)
                        .getJob(id)
                ).withRel(JOB_LINK)
            );

            jobExecutionResource.add(
                ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder
                        .methodOn(JobRestController.class)
                        .getJobRequest(id)
                ).withRel(REQUEST_LINK)
            );

            // TODO: https://github.com/spring-projects/spring-hateoas/issues/186 should be fixed in .20 currently .19
//            jobExecutionResource.add(
//                ControllerLinkBuilder.linkTo(
//                    JobRestController.class,
//                    JobRestController.class.getMethod(
//                        "getJobOutput",
//                        String.class,
//                        String.class,
//                        HttpServletRequest.class,
//                        HttpServletResponse.class
//                    ),
//                    id,
//                    null,
//                    null,
//                    null
//                ).withRel("output")
//            );

            jobExecutionResource.add(
                ControllerLinkBuilder
                    .linkTo(JobRestController.class)
                    .slash(id)
                    .slash(OUTPUT_LINK)
                    .withRel(OUTPUT_LINK)
            );

            jobExecutionResource.add(
                ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder
                        .methodOn(JobRestController.class)
                        .getJobStatus(id)
                ).withRel(STATUS_LINK)
            );

            jobExecutionResource.add(
                ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder
                        .methodOn(JobRestController.class)
                        .getJobMetadata(id)
                ).withRel(METADATA_LINK)
            );
        } catch (final GenieException ge) {
            // If we can't convert it we might as well force a server exception
            throw new RuntimeException(ge);
        }

        return jobExecutionResource;
    }
}
