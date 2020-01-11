/*
 * Copyright (C) 2019 Johan Dykstrom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.dykstrom.jppf.mandel.task;

import org.jppf.JPPFException;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFConnectionPool;
import org.jppf.client.JPPFJob;
import org.jppf.node.protocol.Task;
import org.jppf.utils.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.dykstrom.jppf.mandel.model.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class creates JPPF jobs and tasks, and submits them to a JPPF client.
 *
 * @author Johan Dykstrom
 */
public class TaskSpawner implements AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(TaskSpawner.class);

    private final AtomicInteger jobId = new AtomicInteger(0);

    private final JPPFClient jppfClient;

    public TaskSpawner() {
        logger.info("Creating JPPF client...");
        jppfClient = new JPPFClient();
        logger.info("Creating JPPF client... done");
    }

    public List<Line> spawnTasks(List<Parameters> parametersList, int numberOfJobs) throws JPPFException {
        return executeMultipleConcurrentJobs(parametersList, numberOfJobs);
    }

    private List<Line> executeMultipleConcurrentJobs(List<Parameters> parametersList, int numberOfJobs) throws JPPFException {
        // ensure that the client connection pool has as many connections as the number of jobs to execute
        ensureNumberOfConnections(jppfClient, numberOfJobs);

        // this list will hold all the jobs submitted for execution, so we can later collect and process their results
        final List<JPPFJob> jobs = new ArrayList<>(numberOfJobs);

        int tasksPerJob = parametersList.size() / numberOfJobs;

        // create and submit all the jobs
        for (int jobNumber = 0; jobNumber < numberOfJobs; jobNumber++) {
            jobs.add(createAndSubmitJob(parametersList, jobNumber * tasksPerJob, (jobNumber + 1) * tasksPerJob));
        }

        List<Line> lines = new ArrayList<>();

        // wait until the jobs are finished and process their results.
        for (final JPPFJob job : jobs) {
            // wait if necessary for the job to complete and collect its results
            final List<Task<?>> results = job.awaitResults();

            // process the job results
            lines.addAll(processExecutionResults(results));
        }

        return lines;
    }

    private JPPFJob createAndSubmitJob(List<Parameters> parametersList, int fromIndex, int toIndex) throws JPPFException {
        // create a job with a distinct name
        String jobName = "job-" + jobId.getAndIncrement() + "-" + fromIndex + "-" + (toIndex - 1);
        logger.debug("Creating job: {}", jobName);
        final JPPFJob job = createJob(jobName, parametersList.subList(fromIndex, toIndex));

        // submit the job for execution, without blocking the current thread
        jppfClient.submitAsync(job);

        return job;
    }

    /**
     * Ensure that the JPPF client has the desired number of connections.
     *
     * @param jppfClient          the JPPF client which submits the jobs.
     * @param numberOfConnections the desired number of connections.
     */
    private void ensureNumberOfConnections(final JPPFClient jppfClient, final int numberOfConnections) {
        // wait until the client has at least one connection pool with at least one available connection
        final JPPFConnectionPool pool = jppfClient.awaitActiveConnectionPool();

        // if the pool doesn't have the expected number of connections, change its size
        if (pool.getConnections().size() != numberOfConnections) {
            // set the pool size to the desired number of connections
            pool.setSize(numberOfConnections);
        }

        // wait until all desired connections are available (ACTIVE status)
        pool.awaitActiveConnections(Operator.AT_LEAST, numberOfConnections);
    }


    /**
     * Process the execution results of each submitted task.
     *
     * @param results the tasks results after execution on the grid.
     */
    @SuppressWarnings("unchecked")
    private List<Line> processExecutionResults(final List<Task<?>> results) throws JPPFException {
        List<Line> lines = new ArrayList<>();
        // process the results
        for (final Task<?> task : results) {
            if (task.getThrowable() != null) {
                throw new JPPFException("Task " + task.getId() + " threw exception: " + task.getThrowable().getMessage(), task.getThrowable());
            } else {
                lines.addAll((List<Line>) task.getResult());
            }
        }
        return lines;
    }

    /**
     * Create a JPPF job that can be submitted for execution.
     *
     * @param jobName an arbitrary, human-readable name given to the job.
     * @param parametersList List of task parameters.
     * @return an instance of the {@link org.jppf.client.JPPFJob JPPFJob} class.
     * @throws JPPFException if an error occurs while creating the job or adding tasks.
     */
    private JPPFJob createJob(final String jobName, List<Parameters> parametersList) throws JPPFException {
        final JPPFJob job = new JPPFJob();
        job.setName(jobName);

        for (int i = 0; i < parametersList.size(); i++) {
            String taskId = jobName + "-task-" + i;
            Task<?> task = job.add(new LineTask(taskId, parametersList.get(i)));
            task.setId(taskId);
        }
        return job;
    }

    @Override
    public void close() {
        logger.info("Closing JPPF client...");
        jppfClient.close();
        logger.info("Closing JPPF client... done");
    }
}
