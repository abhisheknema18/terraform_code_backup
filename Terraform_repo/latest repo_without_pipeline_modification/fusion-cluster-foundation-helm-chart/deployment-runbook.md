## Cluster Foundation Deployment to AKS Runbook

#### Setup & Push Chart

csi-secrets-store-provider-azure (v1.0.1)
https://github.com/Azure/secrets-store-csi-driver-provider-azure/tree/v1.0.1/charts/csi-secrets-store-provider-azure

Manual Changes:
1. Reponame update
2. syncSecret enabled: true
3. imagePullSecrets:
 - name: acr-secret

secrets-store-csi-driver (v1.0.1)
https://github.com/kubernetes-sigs/secrets-store-csi-driver/tree/v1.1.0/charts/secrets-store-csi-driver

Manual Changes:
1. Reponame update
2. syncSecret enabled: true
3. imagePullSecrets:
 - name: acr-secret

ingress-nginx (4.0.17)
https://github.com/kubernetes/ingress-nginx/tree/helm-chart-4.0.17/charts/ingress-nginx

Manual Changes:
1. Reponame update
2. Comment Digest
3. enableHttp: false
4. imagePullSecrets:
 - name: acr-secret
5.  kind: DaemonSet 

reloader (v0.0.105)
https://github.com/stakater/Reloader/tree/v0.0.105/deployments/kubernetes/chart/reloader

Manual Changes:
1. Reponame update
2. imagePullSecrets:
 - name: acr-secret


After charts upgrade, Go to the file location where we kept main Chart.yaml and make sure required containers should be added in our repo before we proceed further with cluster-foundation helm chart push and run below command

-------------------<1>-----------------
helm package .

-------------------<2>-----------------
az acr helm push -n acrplatpenshared fusion-cluster-foundation-<version of fusion-cluster-foundation helm chart>.tgz


#### Setup & Deployment

1. Connect to the right AKS cluster

   `az aks get-credentials -n aks-web-dev-uksouth -g rg-web-dev-uksouth`
   
2. Create secret to access ACR from AKS cluster in all namespaces which will need access to ACR. e.g. kube-system

    ```
    kubectl -n kube-system create secret docker-registry acr-secret \
    --docker-server=fusion.azurecr.io \
    --docker-username=username \
    --docker-password=password
    ```
   Note: Make sure helm chart uses the right secret name to fetch docker images from ACR
3. Register the AKS-AzureKeyVaultSecretsProvider preview feature

    ```
    az feature register --namespace "Microsoft.ContainerService" --name "AKS-AzureKeyVaultSecretsProvider"

    az feature list -o table --query "[?contains(name, 'Microsoft.ContainerService/AKS-AzureKeyVaultSecretsProvider')].{Name:name,State:properties.state}"

    az provider register --namespace Microsoft.ContainerService
    ```

4. Assign Contributor Role to AKS cluster managed identity in clusters VNet 
   
5. Create TLS secret using the crt and key files

    ```
    kubectl create secret tls aks-ingress-tls \
    --namespace kube-system \
    --key domain.key \
    --cert certificate.crt
    ```
6. Deploy the cluster foundation template and set the appropriate Public IP, DNS and the resource group containing the Public IP resource.
   ``` 
   helm upgrade --install cluster-foundation . --namespace kube-system \
         --set ingress-nginx.controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group"=MC_rg-web-dev-uksouth_aks-web-dev-uksouth_uksouth \
         --set ingress-nginx.controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal"=true \
         --set ingress-nginx.controller.extraArgs.default-ssl-certificate="kube-system/aks-ingress-tls" \
         --set secrets-store-csi-driver.enableSecretRotation="true" \
         --wait
   ```
