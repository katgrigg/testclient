/*
 *
 *       Copyright 2015 Jonathan Shook
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.metawiring.load.core;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.metawiring.load.config.TestClientConfig;
import com.metawiring.load.generator.GeneratorInstanceSource;
import com.metawiring.load.generator.GeneratorSourceImpl;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

public class ExecutionContext {

    private TestClientConfig config;
    private Cluster cluster;
    private Session session;
    private MetricRegistry metrics = new MetricRegistry();
    private List<ScheduledReporter> reporters = new ArrayList<ScheduledReporter>();
    private GeneratorInstanceSource generatorInstanceSource = new GeneratorSourceImpl();
    private long startedAt = System.currentTimeMillis();
    private long endedAt = startedAt;

    public ExecutionContext(TestClientConfig config) {
        this.config = config;
    }

    public TestClientConfig getConfig() {
        return config;
    }

    public void startup() {

        Cluster.Builder builder = Cluster.builder()
                .addContactPoint(config.host)
                .withPort(config.port);

        if (config.user != null && !config.user.isEmpty()) {
            builder.withCredentials(config.user, config.password);
        }

        cluster = builder.build();

        session = cluster.newSession();
        System.out.println("cluster-metadata-allhosts:\n" + session.getCluster().getMetadata().getAllHosts());

    }

    public MetricRegistry getMetrics() {
        return metrics;
    }

    public Session getSession() {
        return session;
    }

    public GeneratorInstanceSource getGeneratorInstanceSource() {
        return generatorInstanceSource;
    }

    public void shutdown() {

        cluster.close();
        for (ScheduledReporter reporter : reporters) {
            reporter.report();
            reporter.stop();
        }
    }

    public String getSummary() {
        return getClass().getSimpleName() + " activities:" + config.activities;
    }

    public Interval getInterval() {
        return new Interval(startedAt, System.currentTimeMillis());
    }
}
