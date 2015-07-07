/*
 * Copyright 2015 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.keycloak.moduletool;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class DeepSizeTest {

    private static final String modulesRoot = "src/test/resources/modules";

    private static final String [] resources = {
        "test/module1/main/module1.jar",
        "test/module2/v2/module2.jar",
        "test/module3/main/root.war/WEB-INF/web.xml",
        "test/module3/main/module.xml",
        "test/tools/module4/main/module4m.jar",
        "test/tools/module4/v2/module4v2.jar"
    };

    private static final String [] allModules = {
        "test.module1",
        "test.module2:v2",
        "test.module3",
        "test.tools.module4",
        "test.tools.module4:v2",
        "test.tools.module5"
    };

    @Test
    public void testTotalSize() throws IOException {
        long total = 0;
        for (String res: resources) {
            total += new File(modulesRoot, res).length();
        }

        Repository repo = new Repository(modulesRoot);
        Assert.assertEquals(total, repo.getTotalSize());
    }

    @Test
    public void testDeepSize() throws IOException {
        Repository repo = new Repository(modulesRoot);

        DeepSizeResult result = repo.getDeepSize("test.module1", false);
        Assert.assertEquals(repo.getTotalSize(), result.getTotalBytes());

        HashSet<String> moduleIds = new HashSet<>();
        for (Module m: result.getModules()) {
            moduleIds.add(m.getId().toString());
        }
        HashSet<String> expectedModuleIds = new HashSet<>(Arrays.asList(allModules));
        Assert.assertEquals(expectedModuleIds, moduleIds);
    }

    @Test
    public void testDeepSizeSkipOptional() throws IOException {
        Repository repo = new Repository(modulesRoot);
        DeepSizeResult result = repo.getDeepSize("test.module1", true);

        String [] ress = {
            "test/module1/main/module1.jar",
            "test/module2/v2/module2.jar",
            "test/tools/module4/main/module4m.jar"
        };
        long size = 0;
        for (String res: ress) {
            size += new File(modulesRoot, res).length();
        }
        Assert.assertEquals(size, result.getTotalBytes());


        HashSet<String> moduleIds = new HashSet<>();
        for (Module m: result.getModules()) {
            moduleIds.add(m.getId().toString());
        }
        HashSet<String> expectedModuleIds = new HashSet<>(Arrays.asList(new String [] {
            "test.module1",
            "test.module2:v2",
            "test.tools.module4"
        }));
        Assert.assertEquals(expectedModuleIds, moduleIds);
    }
}
