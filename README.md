# Marvel Application (Akka-Http)

## Install Latest SBT
http://www.scala-sbt.org/1.0/docs/Setup.html

## Run Application

In command line in folder path type 
```
sbt clean run \
-Dmarvel.url="https://gateway.marvel.com" \
-Dmarvel.privateKey="{PK}" \
-Dmarvel.publicKey="{pubK}" \
-Dmarvel.apiKey="{apiKey}" \
-Dgoogle.apiKey= "{apiKey}" \
-Dgoogle.url="https://translation.googleapis.com"

```
 
## preRequisites
  
JDK: Java(8) preferable

SBT

## Issues

Error handeling is failing due to akka http response transformation issue,
Test are failing due to string 

## GIT

https://github.com/kemmar/marvel
