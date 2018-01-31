### Hands On: Kafka Cluster Installation Steps

#### Prerequisites: 
* Linux OS
* Java 8 JDK installed 
* Scala 2.11.x or 2.12.x  installed 

#### Java Installation 
```

$ sudo apt-get update
$ sudo apt-get install default-jre
$ sudo apt-get install default-jdk

SET UP JAVA_HOME (.bashrc)

```


#### Kafka Installation 

``` 
 Download the version of Kafka corresponding to the scala version installed in your system.
 

 wget http://www-us.apache.org/dist/kafka/0.11.0.1/kafka_2.12-0.11.0.1.tgz`. 
 
 tar -xvzf kafka_2.12-0.11.0.1.tgz
 
 cd kafka_2.12-0.11.0.1
 
 libs folder - Contains all dependancies Kafka needs to run. This enables Kafka to run as a self contained installation. 
 
 config folder - All the files you will need to configure Kafka. 
 
 bin folder - Contains all the programs to get Kafka up and running in a variety of capacities. 
 
 
```
 

