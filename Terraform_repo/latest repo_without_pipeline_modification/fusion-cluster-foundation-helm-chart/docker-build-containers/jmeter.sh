original_image="jmeteralpine"
target_acr="acrplatpenshared.azurecr.io"
tag="regTests"

docker build -t $original_image:$tag . --file './dockerfile/jmeterdockerfile'

docker tag $original_image:$tag $target_acr/$original_image:$tag

docker push $target_acr/$original_image:$tag
