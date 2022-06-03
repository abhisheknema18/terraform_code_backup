original_image="k8s.gcr.io/csi-secrets-store/driver-crds"
target_acr="acrplatpenshared.azurecr.io"
tag="v1.0.1"

docker pull $original_image:$tag

docker tag $original_image:$tag $target_acr/$original_image:$tag 

docker push $target_acr/$original_image:$tag 