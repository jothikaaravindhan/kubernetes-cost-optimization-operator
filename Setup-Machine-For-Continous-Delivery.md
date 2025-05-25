#  Setup Machine For Continuous Delivery
This project is for the kubernetes operator development. This operator requires a Kubernetes cluster to run. 
You can use any Kubernetes cluster, such as Minikube, Kind, or a cloud provider's managed Kubernetes service.

For continuous delivery, Azure Virtual Machine is used for CD pipeline. The following steps will help you set up the machine for continuous delivery.

## Setup Azure Virtual Machine
This machine should be setup with ssh based access and the same should br provided in the github environment secrets.
1. Create a new Azure Virtual Machine with the following specifications:
   - **OS**: Ubuntu 20.04 LTS
   - **Size**: Standard B2s (2 vCPUs, 4 GB RAM)
   - **SSH Key**: Generate a new SSH key pair or use an existing one.
   - **Network**: Ensure the VM has internet access and public IP address.
2. SSH into the VM and install microk8s based on the instructions from the [MicroK8s documentation](https://ubuntu.com/tutorials/install-a-local-kubernetes-with-microk8s#1-overview).
3. Install Helm on the VM:
   ```bash
   sudo snap install helm --classic
   ```
4. Install metrics-server on the VM:
   ```bash
   kubectl apply -f scripts/k8s/metrics-server.yaml
   ```

With these steps, your Azure Virtual Machine will be set up for continuous delivery of the Kubernetes operator.

## Setup GitHub Actions with Azure VM
1. In your GitHub repository, navigate to **Settings** > **Secrets and variables** > **Actions**.
2. Create the required environment and each environment should have the following secrets(Each environment can have a different values):
     - **AZURE_VM_NAME**: The name of your Azure VM (e.g., `costoperator`).
     - **AZURE_VM_HOST**: The public IP address of your Azure VM 
     - **AZURE_VM_USERNAME**: The username for SSH access to your Azure VM (e.g., `jojo`).
     - **AZURE_VM_SSH_PRIVATE_KEY**: The private SSH key for accessing your Azure VM. Make sure to copy the entire key, including the `-----BEGIN OPENSSH PRIVATE`
     - **HELM_RELEASE_NAME**: The name of the Helm release for the cost operator (e.g., `cost-operator`).

## Usage of the Azure VM in GitHub Actions
The Azure VM will be used in the GitHub Actions workflow to deploy the Kubernetes operator. 
Due to the limited usage hours of the free tier, it is recommended to use the VM only for the deployment process and 
shut it down after the deployment is complete.

> [!NOTE]  
> Azure Student Subscription doesn't provide necessary permissions to create service principal (Which gives client id and client secret).
> 
> With necessary permissions, you can use the service principal to authenticate with Azure and deploy the Kubernetes operator using Helm.
>
> Also this permission allows the workflow to switch off and on the VM. Which is not possible with the student subscription.


