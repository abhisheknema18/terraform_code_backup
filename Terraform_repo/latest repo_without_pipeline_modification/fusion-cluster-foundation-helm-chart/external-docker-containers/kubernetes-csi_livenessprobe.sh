original_image="mcr.microsoft.com/oss/kubernetes-csi/livenessprobe"
target_acr="acrplatpenshared.azurecr.io"
tag="v2.5.0"

docker pull $original_image:$tag

docker tag $original_image:$tag $target_acr/$original_image:$tag 

docker push $target_acr/$original_image:$tag 