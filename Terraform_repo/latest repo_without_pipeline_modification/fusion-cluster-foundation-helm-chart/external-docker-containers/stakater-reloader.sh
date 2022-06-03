original_image="stakater/reloader"
target_acr="acrplatpenshared.azurecr.io"
tag="v0.0.105"

docker pull $original_image:$tag

docker tag $original_image:$tag $target_acr/$original_image:$tag 

docker push $target_acr/$original_image:$tag 