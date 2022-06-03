# Tenant Provisioning Runbook

---
### Hub

1. Repo: https://dev.azure.com/amtsybex1/Mobile/_git/fusion-tf-hub
2. Create Storage Container in Platform subscription to save tenant terraform state e.g. hub
3. Set Env Vars with Subscription and Tenant Details
   Example: DEV-HUB
    ```
    export ARM_TENANT_ID=a73aacc0-8b97-4e3e-b2b3-c6e59f0b7a9b
    export ARM_SUBSCRIPTION_ID=a95faac2-cab9-4d56-8c6d-fc818f9cf136
    export ARM_CLIENT_ID=4cc27d4c-ad74-4eba-9f59-ab033ec6b899
    export ARM_CLIENT_SECRET=85T7Q~QQiN4o1.uCueeqS7j.Zt4dadkbHrRNW

    export AZURE_CLIENT_ID=4cc27d4c-ad74-4eba-9f59-ab033ec6b899
    export AZURE_CLIENT_SECRET=85T7Q~QQiN4o1.uCueeqS7j.Zt4dadkbHrRNW
    export AZURE_TENANT_ID=a73aacc0-8b97-4e3e-b2b3-c6e59f0b7a9b
    ```
4. AZ login using service principal
    ```
    az login --service-principal -u 4cc27d4c-ad74-4eba-9f59-ab033ec6b899 -p 85T7Q~QQiN4o1.uCueeqS7j.Zt4dadkbHrRNW --tenant a73aacc0-8b97-4e3e-b2b3-c6e59f0b7a9b
    ```
5. Terraform
    ```
    terraform init
    terraform plan
    terraform apply
    ```

### Platform

1. Repo: https://dev.azure.com/amtsybex1/Mobile/_git/fusion-tf-platform
2. Create Storage Container in Platform subscription to save tenant terraform state e.g. plat/platform
3. Set Env Vars with Subscription and Tenant Details
4. AZ login using service principal
    ```
    az login --service-principal -u 71567b6f-47a5-4f8a-81e1-2214cf040923 -p 2Yc7Q~hT6NMQ8w-Xbj.WQrbDGh1ba170izFKM --tenant a73aacc0-8b97-4e3e-b2b3-c6e59f0b7a9b
    ```
5. Terraform
    ```
    terraform init
    terraform plan
    terraform apply
    ```

### Tenant - Shared

1. Repo: https://dev.azure.com/amtsybex1/Mobile/_git/fusion-tf-tenant-shared
2. Create Tenant Storage Container in Platform subscription to save new tenant terraform state file
3. Set Env Vars with Subscription and Tenant Details
4. AZ login using service principal
    ```
    az login --service-principal -u 13b41112-a61a-4e5e-bad4-1069a2fd7809 -p Zh57Q~xN1784urISc-B5OI6LvoTTNep7dBfRl --tenant a73aacc0-8b97-4e3e-b2b3-c6e59f0b7a9b
    ```
5. Terraform
    ```
    terraform init
    terraform plan
    terraform apply
    ```

---

## For Each New Tenant

### Tenant - SQL - DB

1. Repo: https://dev.azure.com/amtsybex1/Mobile/_git/fusion-tf-module-sqldatabase
2. Create Tenant Storage Container in Platform subscription to save new tenant terraform state file
3. Set Env Vars with Subscription and Tenant Details
4. AZ login using service principal
    ```
    az login --service-principal -u 13b41112-a61a-4e5e-bad4-1069a2fd7809 -p Zh57Q~xN1784urISc-B5OI6LvoTTNep7dBfRl --tenant a73aacc0-8b97-4e3e-b2b3-c6e59f0b7a9b
    ```
5. Terraform
    ```
    terraform init
    terraform plan
    terraform apply
    ```

### Tenant

1. Repo: https://dev.azure.com/amtsybex1/Mobile/_git/fusion-tf-tenant-ingress
2. Use Tenant Storage Container in Platform subscription to save tenant terraform state file
3. Set Env Vars with Subscription and Tenant Details
4. AZ login using service principal
    ```
    az login --service-principal -u 13b41112-a61a-4e5e-bad4-1069a2fd7809 -p Zh57Q~xN1784urISc-B5OI6LvoTTNep7dBfRl --tenant a73aacc0-8b97-4e3e-b2b3-c6e59f0b7a9b
    ```
5. Terraform
    ```
    terraform init
    terraform plan -var-file=".vars/dev-internal.tfvars"
    terraform apply -var-file=".vars/dev-internal.tfvars"
    ```

### Tenant - Hub

1. Repo: https://dev.azure.com/amtsybex1/Mobile/_git/fusion-tf-tenant-hub
2. Use Hub Storage Container in Platform subscription to save new tenant terraform state
3. Set Env Vars with Subscription and Tenant Details
4. AZ login using service principal
    ```
    az login --service-principal -u 4cc27d4c-ad74-4eba-9f59-ab033ec6b899 -p 85T7Q~QQiN4o1.uCueeqS7j.Zt4dadkbHrRNW --tenant a73aacc0-8b97-4e3e-b2b3-c6e59f0b7a9b
    ```
5. Terraform
    ```
    terraform init
    terraform plan -var-file=".vars/dev-internal.tfvars"
    terraform apply -var-file=".vars/dev-internal.tfvars"
    ```
