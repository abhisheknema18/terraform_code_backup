## Application Deployment to AKS Runbook

#### Setup

1. Connect to the right AKS cluster

   `az aks get-credentials -n aks-web-d01-uksouth -g rg-web-d01-uksouth`

2. Make sure cluster foundation chart has been deployed to AKS cluster.

    https://dev.azure.com/amtsybex1/Mobile/_git/fusion-cluster-foundation-helm-chart
3. Create secret to access ACR from AKS cluster in all namespaces which will need access to ACR
    ```
    kubectl -n fieldsmart create secret docker-registry acr-secret \
    --docker-server=acrplatnonprodshared.azurecr.io \
    --docker-username=acrplatnonprodshared \
    --docker-password=r8T19MQcnoVij/mlhUSg7h7B6202Te4p
    ```
4. Provision Storage
    1. K8s secret to access File Share from AKS - Create manually
    ```
    kubectl -n fieldsmart create secret generic azure-fs-secret 
        --from-literal=azurestorageaccountname=stfieldsmart
        --from-literal=azurestorageaccountkey=key
    ```

    2. Create PersistentVolumeClaim in appropriate namespace - copy the snippet below into a file called pvc.yaml and execute the command:   kubectl apply -f pvc.yaml -n fieldsmart
    ```
    apiVersion: v1
    kind: PersistentVolumeClaim
    metadata:
      name: azurefile
    spec:
      accessModes:
        - ReadWriteMany
      storageClassName: ""
      resources:
        requests:
          storage: 1Gi
    ```
    3. Create PersistentVolume in appropriate namespace - copy the snippet below into a file called pv.yaml and execute the command:   kubectl apply -f pv.yaml -n fieldsmart
    ```
    apiVersion: v1
    kind: PersistentVolume
    metadata:
      name: azurefile
    spec:
      capacity:
        storage: 1Gi
      accessModes:
        - ReadWriteMany
      azureFile:
        secretName: azure-fs-secret
        shareName: fieldsmart
        readOnly: false
      mountOptions:
        - dir_mode=0777
        - file_mode=0777
        - uid=1001
        - gid=1001
        - mfsymlinks
        - nobrl
    ```
5. Create TLS secret using the crt and key files
    ```
    kubectl create secret tls aks-ingress-tls \
    --namespace fieldsmart \
    --key aks-ingress-tls.key \
    --cert aks-ingress-tls.crt
    ```

#### Deploy Application
1. Pull template helm chart

   https://dev.azure.com/amtsybex1/Mobile/_git/fusion-helm-template

   `export HELM_EXPERIMENTAL_OCI=1`

   `helm chart export acrplatnonprodshared.azurecr.io/helm/fieldsmart:v1 --destination ./fieldsmartTemplate`
2. Pull Values files from fusion-devops repo/chart

   Clone https://dev.azure.com/amtsybex1/Mobile/_git/fusion-devops
   OR
   `helm chart export <chart-name> --destination ./configs`
3. Filter values files based on tenant, environment and application
4. helm upgrade/install
     ```
     helm upgrade --install webservice . --namespace fieldsmart \
         -f ./../fusion-devops/configs/internal/values-common.yaml \
         -f ./../fusion-devops/configs/internal/webservice/values-common.yaml \
         -f ./../fusion-devops/configs/internal/webservice/values-dev.yaml \
         --set image.tag=0.1.55-beta-develop.126
     ```

Following flags can be used while upgrade/install
```
--atomic 
if set, upgrade process rolls back changes made in case of failed upgrade. The --wait flag will be set automatically if --atomic is used
--wait                         
if set, will wait until all Pods, PVCs, Services, and minimum number of Pods of a Deployment, StatefulSet, or ReplicaSet are in a ready state before marking the release as successful. 
It will wait for as long as --timeout
```


#### Applications

##### Mobile Webservice

    `helm upgrade --install webservice . --namespace fieldsmart -f ./../fusion-devops/configs/internal/values-common.yaml -f ./../fusion-devops/configs/internal/webservice/values-common.yaml -f ./../fusion-devops/configs/internal/webservice/values-dev.yaml --set image.tag=0.1.55-beta-develop.126`

##### Integration Service
    `helm upgrade --install integrationservice . --namespace fieldsmart -f ./../fusion-devops/configs/internal/values-common.yaml -f ./../fusion-devops/configs/internal/integration\ service/values-common.yaml -f ./../fusion-devops/configs/internal/integrationservice/values-dev.yaml --set image.tag=0.1.55-beta-develop.126`

##### XML Loader
    `helm upgrade --install xmlloader . --namespace fieldsmart -f ./../fusion-devops/configs/internal/values-common.yaml -f ./../fusion-devops/configs/internal/xmlloader/values-common.yaml -f ./../fusion-devops/configs/internal/xmlloader/values-dev.yaml --set image.tag=0.1.55-beta-develop.126`

##### Extract Adapter
    `helm upgrade --install extractadapter . --namespace fieldsmart -f ./../fusion-devops/configs/internal/values-common.yaml -f ./../fusion-devops/configs/internal/extractadapter/values-common.yaml -f ./../fusion-devops/configs/internal/extractadapter/values-dev.yaml --set image.tag=0.1.55-beta-develop.126`