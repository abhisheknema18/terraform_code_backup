original_image="maven"
target_acr="acrplatpenshared.azurecr.io"
tag="3.8.4-openjdk-11"

docker pull $original_image:$tag

docker tag $original_image:$tag $target_acr/$original_image:$tag

docker push $target_acr/$original_image:$tag