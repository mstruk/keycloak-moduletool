# keycloak-moduletool
Tool to explore JBoss Modules repository module dependencies

Basic use is to calculate the effective size in terms of bytes on a disk of a tree of dependencies for a list of modules.
The tool allows excluding optional modules from size calculation.

See [DeepSizeTest.java](https://github.com/mstruk/keycloak-moduletool/blob/master/src/test/java/org/keycloak/moduletool/DeepSizeTest.java) for how to use it programmatically.

# Command line examples

## Display usage message:

    $ java -cp target/moduletool.jar org.keycloak.moduletool.Main

    Usage: java org.keycloak.moduletool.Main --modules-root <PATH> [--deep-size <comma separated list of modules>] [--skip-optional] [--verbose]

## Display basic repository info

    $ java -cp target/moduletool.jar org.keycloak.moduletool.Main --modules-root $JBOSS_HOME/modules/system/layers/base
    
    WARNING: There are missing modules: 
    org.jboss.ws.native.jbossws-native-core (referred to by [javax.activation.api] as optional)
    org.springframework.spring (referred to by [org.jboss.ws.jaxws-client, org.jboss.ws.cxf.jbossws-cxf-server, org.apache.cxf, org.apache.cxf.impl, org.jboss.as.webservices.server.integration] as optional)
    com.google.protobuf (referred to by [org.infinispan.client.hotrod] as optional)
    org.jboss.genericjms.provider (referred to by [org.jboss.genericjms] as optional)
    org.keycloak.keycloak-wildfly-adapter (referred to by [org.keycloak.keycloak-wf9-server-subsystem] as optional)

    Repository contains 422 modules.
    Total size of all modules: 137036306

## Calculate the size of all the dependencies required by a module - org.jboss.logging for example

    $ java -cp target/moduletool.jar org.keycloak.moduletool.Main --modules-root $JBOSS_HOME/modules/system/layers/base --deep-size org.jboss.logging --verbose

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

## Calculate the size of all required dependencies - skipping optional

    $ java -cp target/moduletool.jar org.keycloak.moduletool.Main --modules-root $JBOSS_HOME/modules/system/layers/base --deep-size 'org.keycloak.*,org.jboss.as.connector,org.jboss.as.jaxrs' --skip-optional

    WARNING: There are missing modules: 
    - org.jboss.ws.native.jbossws-native-core (referred to by [javax.activation.api] as optional)
    - org.springframework.spring (referred to by [org.apache.cxf.impl, org.jboss.ws.cxf.jbossws-cxf-server, org.jboss.ws.jaxws-client, org.jboss.as.webservices.server.integration, org.apache.cxf] as optional)
    - com.google.protobuf (referred to by [org.infinispan.client.hotrod] as optional)
    - org.jboss.genericjms.provider (referred to by [org.jboss.genericjms] as optional)
    - org.keycloak.keycloak-wildfly-adapter (referred to by [org.keycloak.keycloak-wf9-server-subsystem] as optional)

    Expanded module list: org.keycloak.keycloak-saml-core,org.keycloak.keycloak-forms-common-freemarker,org.keycloak.keycloak-export-import-dir,org.keycloak.keycloak-connections-file,org.keycloak.keycloak-core-jaxrs,org.keycloak.keycloak-connections-http-client,org.keycloak.keycloak-wildfly-extensions,org.keycloak.keycloak-events-email,org.keycloak.keycloak-export-import-zip,org.keycloak.keycloak-account-api,org.keycloak.keycloak-export-import-api,org.keycloak.keycloak-model-mongo,org.keycloak.keycloak-server,org.keycloak.keycloak-login-freemarker,org.keycloak.keycloak-social-linkedin,org.keycloak.keycloak-services,org.keycloak.keycloak-model-sessions-infinispan,org.keycloak.keycloak-saml-protocol,org.keycloak.keycloak-connections-jpa,org.keycloak.keycloak-model-file,org.keycloak.keycloak-email-freemarker,org.keycloak.keycloak-connections-mongo-update,org.keycloak.keycloak-model-sessions-mem,org.keycloak.keycloak-timer-api,org.keycloak.keycloak-export-import-single-file,org.keycloak.keycloak-social-twitter,org.keycloak.keycloak-events-api,org.keycloak.keycloak-js-adapter,org.keycloak.keycloak-model-sessions-mongo,org.keycloak.keycloak-core,org.keycloak.keycloak-broker-core,org.keycloak.keycloak-events-jpa,org.keycloak.keycloak-events-mongo,org.keycloak.keycloak-social-google,org.keycloak.keycloak-email-api,org.keycloak.keycloak-model-jpa,org.keycloak.keycloak-forms-common-themes,org.keycloak.keycloak-connections-mongo,org.keycloak.keycloak-server-subsystem,org.keycloak.keycloak-social-facebook,org.keycloak.keycloak-login-api,org.keycloak.keycloak-timer-basic,org.keycloak.keycloak-invalidation-cache-infinispan,org.keycloak.keycloak-connections-infinispan,org.keycloak.keycloak-model-sessions-jpa,org.keycloak.keycloak-social-stackoverflow,org.keycloak.keycloak-social-core,org.keycloak.keycloak-kerberos-federation,org.keycloak.keycloak-invalidation-cache-model,org.keycloak.keycloak-model-api,org.keycloak.keycloak-connections-jpa-liquibase,org.keycloak.keycloak-wf9-server-subsystem,org.keycloak.keycloak-account-freemarker,org.keycloak.keycloak-ldap-federation,org.keycloak.keycloak-social-github,org.keycloak.keycloak-events-jboss-logging,org.keycloak.keycloak-broker-saml,org.keycloak.keycloak-broker-oidc,org.jboss.as.connector,org.jboss.as.jaxrs
    Module(s) org.keycloak.*,org.jboss.as.connector,org.jboss.as.jaxrs require(s) 116 modules in total.
    Required modules combined size is 44883300 bytes.

