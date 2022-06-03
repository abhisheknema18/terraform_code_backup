original_image="k8s.gcr.io/ingress-nginx/kube-webhook-certgen"
target_acr="acrplatpenshared.azurecr.io"
tag="v1.1.1"

docker pull $original_image:$tag

docker tag $original_image:$tag $target_acr/$original_image:$tag 

docker push $target_acr/$original_image:$tag 