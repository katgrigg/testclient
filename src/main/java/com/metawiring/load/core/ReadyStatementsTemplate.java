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

import com.datastax.driver.core.Session;
import com.metawiring.load.config.StatementDef;
import com.metawiring.load.config.TestClientConfig;
import com.metawiring.load.config.YamlActivityDef;
import com.metawiring.load.generator.ScopedCachingGeneratorSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience class for handling multiple ReadyStatement bindings
 */
public class ReadyStatementsTemplate {
    private final Session session;
    private final ScopedCachingGeneratorSource generatorSource;
    private List<ReadyStatementTemplate> readyStatementTemplates = new ArrayList<>();
    private TestClientConfig testClientConfig;

    public ReadyStatementsTemplate(Session session, ScopedCachingGeneratorSource generatorSource, TestClientConfig testClientConfig) {
        this.session = session;
        this.generatorSource = generatorSource;
        this.testClientConfig = testClientConfig;
    }


    public ReadyStatementsTemplate addStatements(YamlActivityDef yamlActivityDef, String statementSection) {

        switch (statementSection.toLowerCase()) {
            case "ddl":
                for (StatementDef statementDef : yamlActivityDef.getDdl()) {
                    readyStatementTemplates.add(new ReadyStatementTemplate(statementDef, generatorSource, testClientConfig));
                }
                break;
            case "dml":
                for (StatementDef statementDef : yamlActivityDef.getDml()) {
                    readyStatementTemplates.add(new ReadyStatementTemplate(statementDef, generatorSource, testClientConfig));
                }
                break;
            default:
                throw new RuntimeException("Currently supported statement sections are ddl and dml, not " + statementSection.toLowerCase());
        }

        return this;
    }

    public void addReadyStatementTemplate(ReadyStatementTemplate readyStatementTemplate) {
        readyStatementTemplates.add(readyStatementTemplate);
    }

    public void prepareAll() {
        for (ReadyStatementTemplate readyStatementTemplate : readyStatementTemplates) {
            readyStatementTemplate.prepare(session);
        }
    }

    public ReadyStatements bindAllGenerators(long startCycle) {
        List<ReadyStatement> readyStatementList = new ArrayList<ReadyStatement>();
        for (ReadyStatementTemplate readyStatementTemplate : readyStatementTemplates) {
            readyStatementList.add(readyStatementTemplate.bindGenerators(startCycle));
        }
        ReadyStatements readyStatements = new ReadyStatements(readyStatementList);
        return readyStatements;
    }
}
