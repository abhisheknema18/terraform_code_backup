# Introduction 
Template Helm Chart to create Kubernetes resources like Deployment, Service, Ingress, ConfigMap, HPA etc.

# Getting Started
-   Validate Template Helm Chart Using Values.
    
    Please run the below sample commands by providing the values yaml file in the appropriate location.
    For reference of values.yaml files refer the following repo - https://dev.azure.com/amtsybex1/Mobile/_git/fusion-devops 
    
    The dot (.) in the below command indicates the current directory. You can provide the relative path based on the location of your helm chart.
    
    ```
      helm template . -f ./../fusion-devops/configs/internal/portal/values-dev.yaml
    ```

    ```
      helm lint . -f ./../fusion-devops/configs/internal/portal/values-dev.yaml
    ```

    ```
      helm upgrade --install webservices . --dry-run -f ./../fusion-devops/configs/internal/portal/values-dev.yaml
    ```
      
-   Install / Upgrade Using Helm Chart
    
    Please note the precedence of the values.yaml files. The values to the right override the previous values.
    
    ` 
    Tenant (common.yaml) -> Application (common.yaml) -> Environment (dev.yaml) 
    `
    ```
    helm upgrade --install webservices . -f ./../fusion-devops/configs/internal/values-common.yaml -f ./../fusion-devops/configs/internal/webservice/values-common.yaml -f ./../fusion-devops/configs/internal/webservice/values-dev.yaml
    ```


# Package Application
Use the below application to package helm chart

```
helm package .
```
