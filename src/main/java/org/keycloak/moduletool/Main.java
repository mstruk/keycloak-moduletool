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

import java.io.File;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class Main {

    static String modulesRoot;
    static String moduleList;
    static boolean actionDeepSize;
    static boolean printModules;
    static boolean skipOptional;

    public static void main(String [] args) throws Exception {
        if (!parseArgs(args)) {
            return;
        }

        if (modulesRoot == null) {
            System.out.println("No --modules-root specified");
            return;
        }

        Repository repo = new Repository(modulesRoot);
        if (printModules) {
            repo.printModules();
        }

        System.out.println("Repository contains " + repo.getModuleCount() + " modules.");
        System.out.println("Total size of all modules: " + repo.getTotalSize());
        System.out.println();

        if (actionDeepSize) {
            DeepSizeResult result = repo.getDeepSize(moduleList, skipOptional);
            System.out.println("Module(s) " + moduleList + " require(s) " + result.getModules().size() + " modules in total.");
            System.out.println("Required modules combined size is " + result.getTotalBytes() + " bytes.");
        }

    }

    private static boolean parseArgs(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java org.keycloak.moduletool.Main --modules-root <PATH> [--deep-size <comma separated list of modules>] [--skip-optional] [--print-modules]");
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

                if (!new File(modulesRoot).isDirectory()) {
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
            } else if ("--print-modules".equals(arg)) {
                printModules = true;
            }
        }
        return true;
    }

}
