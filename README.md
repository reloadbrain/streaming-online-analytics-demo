# Spark Online Analytics Store Demo

This application is a demonstration of using spark structured Streaming to
process orders coming from a kafka topic. Orders can be stored in HDFS and then
queried via Spark SQL ad hoc as data is streaming in live.


### Prerequisite

Environment:
* Kafka
* Zookeeper
* Docker
* OpenShift v3 or greater
* Cassandra


### Building and  Running


```bash

make build

```

### URLS:

- Camel Order WebService:  `http://localhost:8080/orders/view/{id}``
- Kafka URL: `http://localhost:9092`
- SparkMaster: `http://localhost:8081`
- SparkWorker1: `http://localhost:8082`
- Minio S3 Storage Alternative: `http://localhost:9000/minio/docker/`
Note: Check s3storage logs for accesskey/secret (This is autogenerated by the docker image)

### Components:
1) UI :

- NodeJS web UI with form input:
- Dropdown-select -> `[ product1, product2, product3 ]`
- Button "Buy Now"
- WebSockets will send notifications when a customer comes on the site and when they disconnect.

2) MongoDB database:

- Script to create sample Collection with products:

Fields:
```
productName, productPrice, productCategory, productQuantity
```
3) OrderPurchaseMicroService:

- Will send orders for products to kafka broker using camel.
- WebService using Camel JAX-RS from endpoint and using kafka to endpoint.

4) Spark Streaming App:

Stream all incoming messages in kafka and calculate:

- Number of customers on the website.
- Number of orders placed.
- Number of times the customer comes back to the same product page and views.
- Top viewed products.
- Top purchased products.

Will use Parquet dataformat and storing results on filesystem or s3
