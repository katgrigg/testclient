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

package com.metawiring.load.activities;

import com.metawiring.load.config.TestClientConfig;
import com.metawiring.load.config.YamlActivityDef;
import com.metawiring.load.core.ExecutionContext;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

@Test
public class YamlConfigurableActivityTest {

    @Test
    public void testYamlConfigurableActivityLoadsOnInit() {
        ExecutionContext c = mock(ExecutionContext.class);
        TestClientConfig conf = TestClientConfig.builder().build();
        when(c.getConfig()).thenReturn(conf);

        YamlConfigurableActivity activity = new YamlConfigurableActivity();
        activity.init("write-telemetry",c);
        YamlActivityDef ad = activity.getYamlActivityDef();
        assertNotNull(ad);

    }
}