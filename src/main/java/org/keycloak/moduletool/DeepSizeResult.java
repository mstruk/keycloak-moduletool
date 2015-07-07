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

import java.util.Set;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class DeepSizeResult {
    private long totalBytes;
    private Set<Module> modules;

    DeepSizeResult(Set<Module> modules, long totalBytes) {
        this.modules = modules;
        this.totalBytes = totalBytes;
    }
    public long getTotalBytes() {
        return totalBytes;
    }

    public Set<Module> getModules() {
        return modules;
    }
}
