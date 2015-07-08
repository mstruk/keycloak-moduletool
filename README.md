# keycloak-moduletool
Tool to explore JBoss Modules repository module dependencies

Basic use is to calculate the effective size in terms of bytes on a disk of a tree of dependencies for a list of modules.
The tool allows excluding optional modules from size calculation.

See [DeepSizeTest.java](https://github.com/mstruk/keycloak-moduletool/blob/master/src/test/java/org/keycloak/moduletool/DeepSizeTest.java) for how to use it programmatically.

# Command line examples

## Display usage message:

    $ java -cp target/keycloak-moduletool-1.0.0-SNAPSHOT.jar org.keycloak.moduletool.Main

    Usage: java org.keycloak.moduletool.Main --modules-root <PATH> [--deep-size <comma separated list of modules>] [--skip-optional] [--print-modules]

## Display basic repository info

    $ java -cp target/keycloak-moduletool-1.0.0-SNAPSHOT.jar org.keycloak.moduletool.Main --modules-root $JBOSS_HOME/modules/system/layers/base
    
    WARNING: There are missing modules: 
    org.jboss.ws.native.jbossws-native-core (referred to by [javax.activation.api] as optional)
    org.springframework.spring (referred to by [org.jboss.ws.jaxws-client, org.jboss.ws.cxf.jbossws-cxf-server, org.apache.cxf, org.apache.cxf.impl, org.jboss.as.webservices.server.integration] as optional)
    com.google.protobuf (referred to by [org.infinispan.client.hotrod] as optional)
    org.jboss.genericjms.provider (referred to by [org.jboss.genericjms] as optional)
    org.keycloak.keycloak-wildfly-adapter (referred to by [org.keycloak.keycloak-wf9-server-subsystem] as optional)

    Repository contains 422 modules.
    Total size of all modules: 137036306

## Calculate the size of all the dependencies required by a module - org.jboss.logging for example

    $ java -cp target/keycloak-moduletool-1.0.0-SNAPSHOT.jar org.keycloak.moduletool.Main --modules-root $JBOSS_HOME/modules/system/layers/base --deep-size org.jboss.logging

    WARNING: There are missing modules: 
    org.jboss.ws.native.jbossws-native-core (referred to by [javax.activation.api] as optional)
    org.springframework.spring (referred to by [org.jboss.ws.jaxws-client, org.jboss.ws.cxf.jbossws-cxf-server, org.apache.cxf, org.apache.cxf.impl, org.jboss.as.webservices.server.integration] as optional)
    com.google.protobuf (referred to by [org.infinispan.client.hotrod] as optional)
    org.jboss.genericjms.provider (referred to by [org.jboss.genericjms] as optional)
    
    Repository contains 422 modules.
    Total size of all modules: 137036306

    org.jboss.logging
      org.jboss.logmanager
        javax.api
          javax.sql.api
            javax.api
        org.jboss.modules
    Module(s) org.jboss.logging require(s) 5 modules in total.
    Required modules combined size is 585609 bytes.

