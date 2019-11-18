# How to run
first run discovery service, then you can run Gateway. There is also option to run it using docker.
## Main goal
It should be used to communicate with other microservices:
- clients
- order
- warehouse
## Tests
This project contains two types of tests:
- unit tests
- container tests
Both of them you can find in src/test/java. To run container tests 
you need to select integration-test profile and run maven test. It's required to
have all microservices package.
## Endpoints in ProductController
### GET /products
Get all products from warehouse microservice
### POST /products
Add product to warehouse microservice
### GET /product/{id}
Get product from warehouse microservice with id from path
### POST /buyProduct
Endpoint to buy products. 
First send request to warehouse microservice to take one product if it is on stock.
Then it add client to client microservice and add his id to order, which is sending to order microservice. 
After that state is updated nad transaction object is send by queue to save it in Clients microservice.
## Endpoints in OrderController
### GET orderState/order/{id}
Get actual state of order
### POST /updateOrderState
update state of order, available states:
-SUBMITTED
-FULLFILLED
-CANCEL
-PAID
## Endpoints in TransactionController
### GET /transaction/{id}
Get all client transactions
### GET /transaction/{id}/{dateFrom}/{dateTo}
Get all client transactions between dateFrom and dateTo

