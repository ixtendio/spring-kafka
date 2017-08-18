# Spring & Kafka

This project shows how to use Kafka and Kafka Streams using Spring Boot and Spring-Kafka.

## Prerequisites
### Runtime
Install **Java 8** as described at [https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)

Install **VirtualBox** & **VirtualBox Extension Pack** as described at [https://www.virtualbox.org/wiki/Downloads](https://www.virtualbox.org/wiki/Downloads) 

Install **Vagrant** as described at [https://www.vagrantup.com/docs/installation/](https://www.vagrantup.com/docs/installation/)

Install **Ansible** as described at [http://docs.ansible.com/ansible/latest/intro_installation.html](http://docs.ansible.com/ansible/latest/intro_installation.html)

### Compilation
Install **Maven** as described at [https://maven.apache.org/install.html](https://maven.apache.org/install.html)

## Kafka Cluster Installation

**Important!** For a successful installation, you must have a good internet connection. 

From terminal, go to the project folder and run:

```bash
vagrant up
```

If this command is executed for the very first time, it will take a few minutes (depending on your internet connection) to complete. That's because it will download a CentOS 7 image from the Vagrant public repository and after that it will create and provision three virtual machines in your VirtualBox. The following programs will be installed on each VM:
1. Java 8
2. Zookeeper Server
3. Kafka Server using TLS communication

**Recommended!** To make sure that the VM provisioning has been successfully completed and that all servers are up and running, you can execute the **provisioning** again by executing this command: 

```bash
vagrant provision
```

At this point, if everything worked fine, on your local VirtualBox you must have three CentOS 7 virtual machines (kafka_kafka1, kafka_kafka2, kafka_kafka3). Each virtual machine must have an instance of the Zookeeper server and an instance of the Kafka server installed and started.

To check if the Kafka server is up and running, first you must login to one VM:

```bash
vagrant ssh kafka1
```
 
then type:

```bash
service kafka status
```

If the server is up and running, you must see this message:
> Active: active (running) since ...

### Troubleshooting

If the Kafka server is not in the active (running) state, you can execute the **provisioning** again. If, after the re-provisioning, the server is still down, then you must investigate the logs to see what could be the cause of the failure:

```bash
tail -n500 /opt/kafka/logs/server.log
``` 

## Kafka Topics Initialization

After the Kafka server is up an running, execute the following command in one of the VMs:

```bash
~/kafka-topics-init.sh
```

This command will create four topics:
1. **songs**
2. **movies** 
3. **movies-thriller** 
4. **users-opinions**

Each topic has three partitions and a replication factor of three.
    
## Program Execution

First, make sure that you are located under the project folder. Here, compile the project by running:

```bash
mvn clean package
```    

After that, launch the program by executing: 

```bash
./local_run.sh
```

You can find the output of the program in the folder **.run/out** located under the project folder. Below you can find a short description of each log file:
1. **movies_batch_consumer.log** - contains the movies events read by *MovieBatchConsumer* using the batch mode
2. **movies_consumer.log** - contains the movies events read by *MovieConsumer*
3. **movies_thriller_consumer.log** - contains the thriller movies with their rating and tags processed by *MovieStreamProcessor*
4. **songs_batch_consumer.log** - contains the songs events read by *SongBatchConsumer* using the batch mode
5. **songs_consumer.log** - contains the songs events read by *SongConsumer*

The Kafka events are produced by:
1. **MovieProducer**
2. **SongProducer**
3. **UserOpinionProducer**