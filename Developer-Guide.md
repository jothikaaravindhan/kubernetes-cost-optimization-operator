# Developer Guide
This provides the installations required for the development of the kubernetes cost optimization operator.

All pre-requisite components mentioned in [Installation Guide](Installation-Guide.md) should be installed based on the instructions.

### Java

SDKMan tool used to install java. Java operator SDK is developed in java version 17. Version 21 should be backward compatible with 17. If there is any blocking issues arise, the version can be rolled back to 17.

* Version: 21

This will list the available versions of java.
>sdk list java

Choose and install the required version as shown below,
>sdk install java 21.0.6-zulu

Verify that the java version is 21
>java --version

### Gradle

Brew tool in Mac environment used for this installation.

* Version: 8.14

> brew install gradle


### CRD Generation
CRD generation can be done using crd-gen tool as described in the [here](https://github.com/fabric8io/kubernetes-client/blob/main/crd-generator/cli/README.md)
Follow the instructions in the above link to install the crd-gen tool. With this tool, the CRD generation can be done using the command below.

> ./crd-gen <JAR_FILE> -o <OUTPUT_DIR>

Example:

> ./crd-gen app/build/libs/app-0.1.0-SNAPSHOT.jar -o app/crds

This will generate the CRD yaml files in the output directory specified. The generated CRD files can be used for the operator deployment.