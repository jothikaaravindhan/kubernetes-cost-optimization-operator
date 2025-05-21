#  Install via GitHub Release (Pre-packaged Helm Chart)
You can skip cloning the repo and directly use the Helm chart from the GitHub release:

1. Download the release chart
Visit the [Releases](https://github.com/jothikaaravindhan/kubernetes-cost-optimization-operator/releases/tag/v0.1.0) section and download the latest
```bash
 cost-optimization-operator-v0.1.0.tgz
```
2. Install the Helm chart
```bash
helm install cost-optimization-operator cost-optimization-operator-v0.1.0.tgz
```
3. Deploy Custom Resource (CR)
```bash
kubectl apply -f app/crds/my-first-cr.yaml
```
Make sure to update the namespace, threshold, and resource type inside the CR YAML.

Rest of the steps are the same as above in [User Guide](User-Guide.md)
