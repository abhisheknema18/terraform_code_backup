original_image="mcr.microsoft.com/oss/azure/secrets-store/provider-azure"
target_acr="acrplatpenshared.azurecr.io"
tag="v1.0.1"

docker pull $original_image:$tag

docker tag $original_image:$tag $target_acr/$original_image:$tag 

docker push $target_acr/$original_image:$tag 