# Account Transfer Service

This service is designed to manage the transfer of funds between accounts.
The service includes comprehensive transaction handling, getting current currency exchange rate and
currency exchange operations.

## Running the Service

To run the service, you will need at least Java version 17 and maven.

1. Navigate to the project directory::
```bash
cd /path-to/account-transfer-service
```

2. Build and run the project using the command (with running tests):
```bash
mvn clean install && java -jar target/account-transfer-0.0.1-SNAPSHOT.jar
```

without running tests:
```bash
mvn clean install -Dmaven.test.skip=true && java -jar target/account-transfer-0.0.1-SNAPSHOT.jar
```

3. After successful startup, the application will be available with default port 8080 at: 
   http://localhost:8080/account-transfer-service/
   If you want to use another port, you can set it with run project command:
```bash
mvn clean install && java -jar -Dserver.port=your_port_value target/account-transfer-0.0.1-SNAPSHOT.jar
```

4. The service uses the embedded H2 database and after the application is successfully launched, H2 console will be 
   available with default port 8080 at: http://localhost:8080/account-transfer-service/h2-console
   with configuration to connect:
```text
jdbcUrl: jdbc:h2:mem:account_transfer_db
username: sa
password:
```

and with pre-filled test data:
```text
ID      BALANCE     CURRENCY    OWNER_ID  
1       1500.50     USD         1001
2       2500.75     EUR         1002
3       3000.00     USD         1003
4       4500.25     JPY         1004
5       5000.50     GBP         1005
```


## Available Endpoints

After the application is successfully launched, full Swagger documentation will be available at:
http://localhost:8080/account-transfer-service/swagger-ui/index.html

### 1. Retrieving the current exchange rate between the currencies specified in the parameters: 'fromCurrency' and 'toCurrency'

GET /account-transfer-service/exchange-rate

example request: curl -X GET http://localhost:8080/account-transfer-service/exchange-rate?fromCurrency=USD&toCurrency=EUR

example response:
HTTP/1.1 200 OK
```json
{
   "fromCurrency": "USD",
   "toCurrency": "EUR",
   "rate": 0.9265,
   "dateTime": "2024-06-10T16:19:42.3761066+03:00"
}
```

### 2.  Transfers funds from one account to another, returning details of the transaction including exchange rates and updated balances.

GET /account-transfer-service/transfer

example request: curl -X POST http://localhost:8080/account-transfer-service/transfer
request body:
```json
{
   "accountOwnerId": 1001,
   "targetAccountId": 1005,
   "amount": 152.01
}
```

example response:
HTTP/1.1 200 OK
```json
{
   "transactionId": 1,
   "accountOwnerId": 1001,
   "targetAccountId": 1005,
   "amount": 152.01,
   "dateTime": "2024-06-10T17:00:27.4635512+03:00",
   "status": "SUCCESS",
   "residualBalance": 1348.49,
   "baseCurrency": "USD",
   "targetCurrency": "GBP",
   "exchangeRate": 0.95
}
```

