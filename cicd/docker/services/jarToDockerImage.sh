!/bin/bash

#first -> jar name , second -> watcher/lambda , third -> target image name
# Eg: ./jarToDockerImage.sh watcher-services.jar watcher-services watcher-svc:1

sed -i -e "s/<JAR_NAME>/$1/g" Dockerfile
sed -i -e "s/<SERVICE_TYPE>/$2/g" Dockerfile
pwd | xargs docker build -t $3

docker login -u XXX -p XXXX bora-docker-local.jfrog.io

docker tag $3  bora-docker-local.jfrog.io/$3

docker push bora-docker-local.jfrog.io/$3
