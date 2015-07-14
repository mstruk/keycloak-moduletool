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

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class ModuleId {
    private String id;
    private String slot = "main";

    public ModuleId(String name) {
        String [] idSlot = name.split(":");
        this.id = idSlot[0];
        if (idSlot.length > 1) {
            this.slot = idSlot[1];
        }
    }

    public ModuleId(String name, String slot) {
        this.id = name;
        if (slot != null && !"".equals(slot)) {
            this.slot = slot;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ModuleId moduleId = (ModuleId) o;

        if (!id.equals(moduleId.id)) {
            return false;
        }
        return !(slot != null ? !slot.equals(moduleId.slot): moduleId.slot != null);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (slot != null ? slot.hashCode(): 0);
        return result;
    }

    @Override
    public String toString() {
        if ("main".equals(slot)) {
            return id;
        } else {
            return id + ":" + slot;
        }
    }
}