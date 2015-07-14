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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class Main {

    static String modulesRoot;
    static String moduleList;
    static boolean actionDeepSize;
    static boolean skipOptional;
    static boolean verbose;

    public static void main(String [] args) throws Exception {
        if (!parseArgs(args)) {
            return;
        }

        if (modulesRoot == null) {
            System.out.println("No --modules-root specified");
            return;
        }

        Repository repo = new Repository(modulesRoot, verbose);

        if (actionDeepSize) {
            List<String> names = Arrays.asList(moduleList.split(","));
            List<ModuleId> resolvedModules = resolveModuleList(names, repo);

            HashSet<ModuleId> passedModules = new HashSet<>();
            for (String id: moduleList.split(",")) {
                passedModules.add(new ModuleId(id));
            }

            if (!new HashSet(resolvedModules).equals(passedModules)) {
                System.out.println("Expanded module list: " + String.join(",", asListOfString(resolvedModules)));
            }

            DeepSizeResult result = repo.getDeepSize(resolvedModules, skipOptional);
            System.out.println("Module(s) " + moduleList + " require(s) " + result.getModules().size() + " modules in total.");
            System.out.println("Required modules combined size is " + result.getTotalBytes() + " bytes.");
        } else {
            if (verbose) {
                repo.printModules();
            }
            System.out.println("Repository contains " + repo.getModuleCount() + " modules.");
            System.out.println("Total size of all modules: " + repo.getTotalSize());
            System.out.println();
        }

    }

    static List<String> asListOfString(List<ModuleId> resolvedModules) {
        return resolvedModules.stream().map((id) -> id.toString()).collect(Collectors.toList());
    }

    static List<ModuleId> resolveModuleList(List<String> names, Repository repo) {
        List<ModuleId> resolved = new LinkedList<>();
        for (String name: names) {
            Set<Module> found = repo.find(name);
            if (found.size() == 0) {
                throw new IllegalArgumentException("No module found for: " + name);
            }
            for (Module m: found) {
                resolved.add(m.getId());
            }
        }
        return resolved;
    }

    private static boolean parseArgs(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java org.keycloak.moduletool.Main --modules-root <PATH> [--deep-size <comma separated list of modules>] [--skip-optional] [--verbose]");
            return false;
        }

        int i;
        for (i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--modules-root".equals(arg)) {
                if (i == args.length - 1) {
                    System.out.println("--modules-root takes additional argument: <path to directory>");
                    return false;
                }

                modulesRoot = args[++i];

                if (!Files.isDirectory(Paths.get(modulesRoot))) {
                    System.out.println("No such directory: " + modulesRoot);
                    return false;
                }
            } else if ("--deep-size".equals(arg)) {
                if (i == args.length - 1) {
                    System.out.println("--deep-size takes additional argument: <comma separated list of module names>");
                    return false;
                }
                actionDeepSize = true;
                moduleList = args[++i];
            } else if ("--skip-optional".equals(arg)) {
                skipOptional = true;
            } else if ("--verbose".equals(arg)) {
                verbose = true;
            }
        }
        return true;
    }

}
