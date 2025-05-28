# Kubernetes Cost Optimization Operator

This project implements a Kubernetes Operator that monitors resource usage (CPU/Memory) of selected pods and notifies users via email when pre-configured resource usage is detected.

---

## Features

- Monitors CPU and Memory usage
- Sends email alerts when thresholds are breached
- Custom Resource Definitions (CRDs) for dynamic rule configuration
- Deployable via Helm chart

---

## Prerequisites

Before proceeding, ensure the following are installed:

- **Java 21**
- **Gradle**
- **Docker**
- **Kubernetes Cluster** 
- **kubectl CLI**
- **Helm**
- **CRD Generator** (`crd-gen` from Fabric8)

Detailed installation instructions are available in the [Developer Guide](Developer-Guide.md).

---

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/jothikaaravindhan/kubernetes-cost-optimization-operator.git
cd kubernetes-cost-optimization-operator
```
### 2. Build the Project

```bash 
cd app
gradle clean build
```
### 3. Generate CRDs

```bash
crd-gen app/build/libs/app-0.2.0-SNAPSHOT-plain.jar -o app/crds
```
### 4. Deploy the CRD

```bash
kubectl apply -f app/crds/costoptimizationrules.org.jothika.costoperator-v1.yml
```
### 5.  Install Helm Chart
Update image name and email settings in helm/values.yaml, then:

```bash
helm install cost-optimization-operator charts/cost-optimization-operator
```
### 6. Deploy Custom Resource (CR)
```bash
kubectl apply -f app/crds/nginx-cpu-greater-than-point-one-cr.yaml -n cost-optimization
```
Make sure to update the namespace, threshold, and resource type inside the CR YAML.

### 7. Monitor the Operator
Use kubectl or Lens to watch the pod:
```bash
kubectl get pods -n cost-optimization
kubectl logs -f <POD_TO_MONITOR> -n cost-optimization
```

### 8. Check Email Notifications
Check the email configured in the CR for alerts.
```bash
kubectl get events -n cost-optimization
```

### 9. Uninstall the Operator
```bash
helm uninstall cost-optimization-operator 
kubectl delete ns cost-optimization-operator 
```




