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

import org.jboss.modules.DependencySpec;
import org.jboss.modules.LocalModuleLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleDependencySpec;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class ModulesTest {

    @Test
    public void testModules() throws ModuleLoadException {
        // situations we test

        // - we have mutual dependencies a depends on b, and b depends on a
        //   Test that such a situation doesn't cause infinite recursion
        // - we want for full display to print out proper structure
        // - we want for total size calculation to properly calculate all sizes ... nothing counted twice, nothing skipped
        // - we want for --deep-size to properly calculate deep size for specific module
        // - we want for --skip-optional to properly skip the whole tree of optional

        String [] moduleIds = {
            "test.module1",
            "test.module2:v2",
            "test.module3",
            "test.tools.module4",
            "test.tools.module4:v2"
        };

        String [][] deps = {
            {"test.module2:v2", "test.module3:main"},
            {"test.module1:main", "test.tools.module4:main"},
            {"test.tools.module4:v2", "test.tools.module5:main"},
            {},
            {}
        };


        String modulesRoot = "src/test/resources/modules";
        final File repoRoot = new File(modulesRoot);
        ModuleLoader loader = new LocalModuleLoader(new File[] {repoRoot});
        int i;
        for (i = 0; i < moduleIds.length; i++) {
            String id = moduleIds[i];
            String [] nameSlot = id.split(":");
            String name = nameSlot[0];
            String slot = nameSlot.length > 1 ? nameSlot[1] : null;
            Module module;
            try {
                module = loader.loadModule(ModuleIdentifier.create(name, slot));
            } catch (Exception e) {
                throw new RuntimeException("Failed to load module: " + moduleIds[i], e);
            }

            HashSet<String> dependencies = new HashSet<>();
            dependencies.addAll(Arrays.asList(deps[i]));

            HashSet<String> dspecSet = new HashSet<>();
            DependencySpec[] dspecs = module.getDependencies();
            for (DependencySpec dspec: dspecs) {
                if (dspec instanceof ModuleDependencySpec) {
                    dspecSet.add(((ModuleDependencySpec) dspec).getIdentifier().toString());
                }
            }

            Assert.assertEquals(dependencies, dspecSet);
        }
    }
}
