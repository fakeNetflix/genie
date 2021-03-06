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
import com.netflix.genie.agent.execution.statemachine.Events;
import lombok.extern.slf4j.Slf4j;

/**
 * Action performed when in state SHUTDOWN.
 *
 * @author mprimi
 * @since 4.0.0
 */
@Slf4j
class ShutdownAction extends BaseStateAction implements StateAction.Shutdown {

    ShutdownAction(
        final ExecutionContext executionContext
    ) {
        super(executionContext);
    }

    @Override
    protected void executePreActionValidation() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Events executeStateAction(final ExecutionContext executionContext) {
        UserConsole.getLogger().info("Shutting down...");

        return Events.SHUTDOWN_COMPLETE;
    }

    @Override
    protected void executePostActionValidation() {
    }
}
