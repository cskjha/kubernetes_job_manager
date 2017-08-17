# Introduction to Kubernetes Job Management Service
Kubernetes Job Management Service (KJMS) is a service that runs on top of Kubernetes to provide an application level Job Management. It provides a lambda like language agnostic code execution platform.

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites
- Kubernetes cluster (TBD Minikube should also work)
- Java Development Kit 1.8.x
- Postgres database 9.x
- Maven 3.3.x
- Gradle 2.4.x

### Installing

#### Build
Run the following commands to build the service.
```
git clone git clone 10.112.81.169:/opt/git/opensource.git
cd opensource
gradle wrapper
./gradlew build
```
#### Prepare Postgres database
Run the following command to prepare the database.
It will create a new schema named lambda under the default database and create the required tables.
```
psql -Upostgres < ./create-schema.sql
```

#### Prepare Kubernetes

TBD

#### Start service

Run normally:
```
./gradlew bootRun
```

Run in debug mode (attach your IDE to jdwp debug port 5005)
```
./gradlew bootRun --debug-jvm
```

## Running the tests

TBD

## Deployment

TBD

## Contributing

TBD

## Versioning

TBD

## Authors

Amit Kumar
Atul Jadhav
Jay Juch 
Chandrashekhar Jha 
Yash Bhatnagar 
Vishal Gupta

## License

TBD

