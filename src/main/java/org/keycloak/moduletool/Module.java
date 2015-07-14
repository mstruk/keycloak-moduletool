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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class Module {

    private ModuleId id;
    private long size;
    private boolean inited;

    private List<Module> dependencies = new LinkedList<>();
    private List<Module> optionalDependencies = new LinkedList<>();
    private Set<Module> dependents = new HashSet<>();
    private Set<Module> optionalDependents = new HashSet<>();
    private List<String> resourceRoots = new LinkedList<>();

    Module(ModuleId moduleId) {
        this.id = moduleId;
    }

    public ModuleId getId() {
        return id;
    }

    void addDependency(Module module) {
        dependencies.add(module);
        module.dependents.add(this);
    }

    void addOptionalDependency(Module module) {
        optionalDependencies.add(module);
        module.optionalDependents.add(this);
    }

    void addResourceRoot(String resRoot) {
        resourceRoots.add(resRoot);
    }

    void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    void visit(ModuleVisitor visitor, VisitorContext ctx) {
        visitor.accept(this, ctx);
        if (ctx.visited(this)) {
            return;
        }
        visitDependent(visitor, ctx);
    }

    void visitDependent(ModuleVisitor visitor, VisitorContext ctx) {
        ctx.enter();
        for (Module m : dependencies) {
            // if we are in optional tree
            // we consider all additional children as optional
            if (ctx.isOptional()) {
                visitor.acceptOptional(m, ctx);
            } else {
                visitor.accept(m, ctx);
            }
            if (!ctx.visited(m)) {
                m.visitDependent(visitor, ctx);
            }
        }
        for (Module m : optionalDependencies) {
            visitor.acceptOptional(m, ctx);
            if (!ctx.visited(m)) {
                ctx.incrOptional();
                m.visitDependent(visitor, ctx);
                ctx.decrOptional();
            }
        }
        ctx.exit();
    }

    void init() {
        this.inited = true;
    }

    public List<Module> getDependencies() {
        return dependencies;
    }

    public List<Module> getOptionalDependencies() {
        return optionalDependencies;
    }

    public List<String> getResourceRoots() {
        return resourceRoots;
    }

    public boolean isInited() {
        return inited;
    }

    public Set<Module> getDependent() {
        return dependents;
    }

    public Set<Module> getOptionallyDependent() {
        return optionalDependents;
    }
}