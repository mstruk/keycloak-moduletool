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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.Collection;
import java.util.HashSet;
import java.util.IllegalFormatCodePointException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class Repository {

    private File repoRoot;
    private LinkedHashMap<ModuleId, Module> allModules = new LinkedHashMap<>();
    private long totalSize;

    public Repository(String modulesRoot) throws IOException, IllegalFormatCodePointException {
        repoRoot = new File(modulesRoot);
        if (!repoRoot.isDirectory()) {
            throw new IllegalArgumentException("Specified modulesRoot directory does not exist: " + repoRoot.getAbsoluteFile());
        }

        Stream<Path> pathStream = Files.find(repoRoot.toPath(), Integer.MAX_VALUE, (path, attrs) -> {
            return path.toFile().getName().equals("module.xml");
        });

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);

        long [] total = new long[1];

        pathStream.forEach((path) -> {
            // translate their locations to module identifiers
            Path slot = path.getParent();
            StringBuilder sb = new StringBuilder();
            Path modulepath = repoRoot.toPath().relativize(slot.getParent());

            int count = modulepath.getNameCount();
            for (int i = 0; i < count; i++) {
                if (i > 0)
                    sb.append(".");
                sb.append(modulepath.getName(i));
            }

            ModuleId moduleId = new ModuleId(sb.toString(), slot.getFileName().getName(0).toString());
            Module m = allModules.get(moduleId);
            if (m == null) {
                m = new Module(moduleId);
                allModules.put(moduleId, m);
            }


            // parse module.xml
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();
                Document doc = db.parse(path.toFile());

                Element root = doc.getDocumentElement();
                NodeList deps = root.getElementsByTagName("dependencies");
                if (deps.getLength() > 0) {
                    Element depsEl = (Element) deps.item(0);
                    NodeList modules = depsEl.getElementsByTagName("module");

                    for (int i = 0; i < modules.getLength(); i++) {
                        Element moduleEl = (Element) modules.item(i);
                        ModuleId depId = new ModuleId(moduleEl.getAttribute("name"), moduleEl.getAttribute("slot"));
                        boolean optional = "true".equals(moduleEl.getAttribute("optional"));
                        ;

                        Module dep = allModules.get(depId);
                        if (dep == null) {
                            dep = new Module(depId);
                            allModules.put(depId, dep);
                        }
                        if (optional) {
                            m.addOptionalDependency(dep);
                        } else {
                            m.addDependency(dep);
                        }
                    }
                }

                NodeList res = root.getElementsByTagName("resources");
                if (res.getLength() > 0) {
                    Element resEl = (Element) res.item(0);
                    //NodeList elements = resEl.getElementsByTagName("artifact");
                    //printModules("  Artifacts:");
                    //for (int i = 0; i < elements.getLength(); i++) {
                    //    printModules("    - " + ((Element) elements.item(i)).getAttribute("name"));
                    //}

                    NodeList elements = resEl.getElementsByTagName("resource-root");

                    long rootsSize = 0;
                    for (int i = 0; i < elements.getLength(); i++) {
                        String resname = ((Element) elements.item(i)).getAttribute("path");
                        m.addResourceRoot(resname);
                        File resFile = new File(path.getParent().toFile(), resname);

                        long size = 0;
                        if (resFile.isFile()) {
                            size = resFile.length();
                        } else if (resFile.isDirectory()) {
                            size = getDirSize(resFile);
                        } else {
                            throw new RuntimeException("Resource does not exist: " + resname + " in module " + m.getId());
                        }

                        total[0] += size;
                        rootsSize += size;
                    }

                    m.setSize(rootsSize);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            m.init();
        });

        this.totalSize = total[0];

        LinkedList<Module> failed = new LinkedList<>();

        // check that all modules are inited
        for (Module m: allModules.values()) {
            if (!m.isInited()) {
                failed.add(m);
            }
        }

        if (failed.size() > 0) {
            StringBuilder sb = new StringBuilder("There are missing modules: \n");
            for (Module m: failed) {
                sb.append(m.getId() + " ("
                    + (m.getDependent().size() > 0 ? "referred to by " + toModuleIds(m.getDependent()) + " as required ... " : "")
                    + (m.getOptionallyDependent().size() > 0 ? "referred to by " + toModuleIds(m.getOptionallyDependent()) + " as optional" : "")
                    + ")\n");
            }
            System.out.println("WARNING: " + sb);
        }
    }

    private List<ModuleId> toModuleIds(Collection<Module> modules) {
        LinkedList<ModuleId> ret = new LinkedList<>();
        for (Module m: modules) {
            ret.add(m.getId());
        }
        return ret;
    }


    public long getTotalSize() {
        return totalSize;
    }

    public int getModuleCount() {
        return allModules.size();
    }

    private static long getDirSize(File resFile) throws IOException {
        long [] total = new long[1];
        Files.walkFileTree(resFile.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                total[0] += file.toFile().length();
                return FileVisitResult.CONTINUE;
            }
        });
        return total[0];
    }


    public DeepSizeResult getDeepSize(String moduleNames, boolean skipOptional) {
        LinkedList<Module> modules = new LinkedList<>();

        String [] names = moduleNames.split(",");
        for (String name: names) {
            String [] nameseg = name.split(":");
            ModuleId id = nameseg.length == 1 ? new ModuleId(nameseg[0]) : new ModuleId(nameseg[0], nameseg[1]);
            Module module = allModules.get(id);

            if (module == null) {
                throw new IllegalArgumentException("Modules not found: " + id);
            }
            modules.add(module);
        }

        SimpleVisitor visitor = new SimpleVisitor();

        VisitorContext ctx = new VisitorContext();
        for (Module module: modules) {
            module.visit(visitor, ctx);
        }

        Set<Module> moduleSet = new HashSet<>();
        if (skipOptional) {
            moduleSet = visitor.hard;
        } else {
            moduleSet = ctx.getVisited();
        }

        long total = 0;
        for (Module m : moduleSet) {
            total += m.getSize();
        }

        return new DeepSizeResult(moduleSet, total);
    }

    public void printModules() {
        for (Module m: allModules.values()) {
            print(m.getId().toString() + (m.getSize() > 0 ? "  (" + m.getSize() + ")" : ""));
            print("  Resource-roots:");
            for (String res: m.getResourceRoots()) {
                print("    - " + res);
            }
            print("  Dependencies:");
            for (Module dep: m.getDependencies()) {
                print("    - " + dep.getId());
            }
            for (Module dep: m.getOptionalDependencies()) {
                print("    - " + dep.getId() + " (optional)");
            }
        }
    }

    private static void print(String line) {
        System.out.println(line);
    }

}
