original_image="k8s.gcr.io/sig-storage/csi-node-driver-registrar"
target_acr="acrplatpenshared.azurecr.io"
tag="v2.4.0"

docker pull $original_image:$tag

docker tag $original_image:$tag $target_acr/$original_image:$tag 

docker push $target_acr/$original_image:$tag 