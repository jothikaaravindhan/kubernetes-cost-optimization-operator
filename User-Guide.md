# Kubernetes Cost Optimization Operator

This project implements a Kubernetes Operator that monitors resource usage (CPU/Memory) of selected pods and notifies users via email when inefficient resource usage is detected.

---

## Features

- Monitors CPU and Memory usage
- Sends email alerts when thresholds are breached
- Custom Resource Definitions (CRDs) for dynamic rule configuration
- Deployable via Helm chart
- Built using Java Operator SDK

---

## Prerequisites

Before proceeding, ensure the following are installed:

- **Java (17 or 21)**
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
crd-gen app/build/libs/app-0.1.0-SNAPSHOT.jar -o app/crds
```
### 4. Deploy the Operator

```bash
kubectl apply -f app/crds/CostOptimizationRule
kubectl apply -f app/deploy/operator.yaml
```
### 5.  Install Helm Chart
Update image name and email settings in helm/values.yaml, then:

```bash
helm install cost-optimization-operator charts/cost-optimization-operator
```
### 6. Deploy Custom Resource (CR)
```bash
kubectl apply -f app/crds/my-first-cr.yaml
 
```
Make sure to update the namespace, threshold, and resource type inside the CR YAML.

### 7. Monitor the Operator
Use kubectl or Lens to watch the pod:
```bash
kubectl get pods 
kubectl logs -f <nginx-release-54bb959bd7-p7c69> -n cost-optimization
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




