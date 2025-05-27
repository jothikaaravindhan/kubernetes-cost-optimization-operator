# Developer Guide
This provides the installations required for the development of the kubernetes cost optimization operator.

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

#### Helm
Brew tool in Mac environment used for this installation.

* Version: 3.17.2

> brew install Helm


### Kubernetes Metrics Server

This installation enables Kubernetes to record the memory and cpu usage of the pods/nodes.

Verify that the metrics server is installed already using the command below. This will indicate if the metrics server is not installed.

>kubectl top node

Download the metrics-server yaml from https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.7.2/components.yaml and save it as [metrics-server.yaml](scripts/k8s/metrics-server.yaml).

Modify this file to enable insecure-tls as described here https://github.com/kubernetes-sigs/metrics-server

Create metrics server using the following command. The modified yaml is available in the scripts/k8s directory.

>kubectl apply -f scripts/k8s/metrics-server.yaml

Once installed verify the metrics server installation as show below,

>kubectl top node


### Apache Benchmark (ab)

Apache Benchmark is a tool to test the performance of the web server. This is used to generate load on the NGINX web server to demonstrate the operator's functionality.

Install Apache Benchmark using the following command. This is installed by default in the Mac environment.
For other environments, you can install it using the package manager of your choice.
> apt-get install apache2-utils

Sample command to run the Apache Benchmark tool to generate load on the NGINX web server.
> ab -n 1000 -c 10 http://localhost:80/
This command will send 1000 requests to the NGINX web server with a concurrency of 10 requests at a time.