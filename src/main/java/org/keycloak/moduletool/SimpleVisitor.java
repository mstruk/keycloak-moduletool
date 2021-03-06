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

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
class SimpleVisitor implements ModuleVisitor {

    HashSet<Module> optional = new HashSet<>();
    HashSet<Module> hard = new HashSet<>();
    boolean verbose = false;

    SimpleVisitor(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void accept(Module module, VisitorContext ctx) {
        if (verbose) {
            System.out.println(ctx.tab() + module.getId());
        }
        hard.add(module);
    }

    @Override
    public void acceptOptional(Module module, VisitorContext ctx) {
        if (verbose) {
            System.out.println(ctx.tab() + module.getId() + " (optional)");
        }
        optional.add(module);
    }
}