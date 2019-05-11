#Course Project

##Overall idea on how to run the project


##Contents of the repository


##How to install and run the simulator



##How to download the docker image

Docker commands to build image and container

1. The following generates a docker image locally
sbt docker: publishLocal
   
2. The following publishes a docker image to the hub (Repository adarsh23)
sbt docker: publish
   
3. Creates a container when run within the project root directory
docker-compose up
   
How to run using global docker image
a. Install docker
b. docker pull adarsh23/regionalcloudsim:latest
c. docker run adarsh23/regionalcloudsim

The above command will execute the app


##Diagram of the network topology


##Authors
* Adarsh Hegde
* Amrish Jhaveri
* Chinmay Gangal
* Karan Kadakia
