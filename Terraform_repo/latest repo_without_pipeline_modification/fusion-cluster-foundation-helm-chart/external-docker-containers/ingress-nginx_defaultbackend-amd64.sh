original_image="k8s.gcr.io/defaultbackend-amd64"
target_acr="acrplatpenshared.azurecr.io"
tag="1.5"

docker pull $original_image:$tag

docker tag $original_image:$tag $target_acr/$original_image:$tag 

docker push $target_acr/$original_image:$tag 