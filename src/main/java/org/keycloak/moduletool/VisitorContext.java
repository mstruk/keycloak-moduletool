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

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
class VisitorContext {

    final static String SPACES = "                                                                                     ";

    private HashSet<Module> moduleSet = new HashSet<>();
    private boolean optional;
    private int depth = 0;

    void setOptional(boolean optional) {
        this.optional = optional;
    }

    boolean isOptional() {
        return this.optional;
    }

    boolean visited(Module module) {
        return !moduleSet.add(module);
    }

    Set<Module> getVisited() {
        return moduleSet;
    }

    void enter() {
        depth++;
    }

    void exit() {
        depth--;
    }

    int getDepth() {
        return depth;
    }

    String tab() {
        return SPACES.substring(0, depth * 2);
    }
}