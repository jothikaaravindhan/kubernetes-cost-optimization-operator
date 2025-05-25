# Installation Guide
This provides the pre-requisites and the actual installation details for this operator.
This project is developed in the Mac environment and the instructions are recorded based on the Mac environment.

## Pre Requisites
This provides the list of component required for this project development and deployment.

### Kubernetes
Docker Desktop installation provides a Kubernetes installation. This can be enabled using the setting option in the docker desktop GUI.

* Version: 1.32.2
* Flavour: Docker Desktop

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





