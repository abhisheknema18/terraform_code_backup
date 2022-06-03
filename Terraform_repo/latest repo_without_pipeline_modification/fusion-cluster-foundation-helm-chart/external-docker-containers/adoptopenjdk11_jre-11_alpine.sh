original_image="adoptopenjdk/openjdk11"
target_acr="acrplatpenshared.azurecr.io"
tag="jre-11.0.11_9-alpine"

docker pull $original_image:$tag

docker tag $original_image:$tag $target_acr/$original_image:$tag 

docker push $target_acr/$original_image:$tag 