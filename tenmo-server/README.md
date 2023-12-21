# TEnmo Server

Congratulations—you've landed a job with TEnmo, whose product is an online payment service for transferring "TE bucks" between friends. However, they don't have a product yet. You've been tasked with finalizing the server side of the application, which is a RESTful API server.

You need to add controllers, models, and DAOs to implement the following features:

## Use cases

1. **[DONE]** As a user of the system, I need to be able to register myself with a username and password. _The ability to register has been provided in your starter code._
2. **[DONE]** As a user of the system, I need to be able to log in using my registered username and password. _The ability to log in has been provided in your starter code._
   1. Logging in returns an Authentication Token. I need to include this token with all my subsequent interactions with the system outside of registering and logging in.
3. As a user, when I register, a new account is created for me.
   1. The new account has an initial balance of $1000.
4. As an authenticated user of the system, I need to be able to retrieve my account balance.
5. As an authenticated user of the system, I need to be able to *send* a transfer of a specific amount of TE Bucks to a registered user.
   1. I must not be allowed to send money to myself.
   2. A transfer includes the User IDs of the "from" and "to" users and the amount of TE Bucks.
   3. The receiver's account balance is increased by the amount of the transfer.
   4. The sender's account balance is decreased by the amount of the transfer.
   5. I can't send more TE Bucks than I have in my account.
   6. I can't send a zero or negative amount.
   7. A "Sending" Transfer has an initial status of *Approved*.
6. As an authenticated user of the system, I need to be able to retrieve a list of transfers I have sent or received.
7. As an authenticated user of the system, I need to be able to retrieve the details of any transfer based upon the transfer ID.
8. As an authenticated user of the system, I need to be able to *request* a transfer of a specific amount of TE Bucks from another registered user.
   1. I must not be allowed to request money from myself.
   2. I can't request a zero or negative amount.
   3. A transfer includes the User IDs of the from and to users and the amount of TE Bucks.
   4. A Request Transfer has an initial status of *Pending*.
   5. No account balance changes until the request is approved.
   6. The transfer request should appear in both users' list of transfers.
9.  As an authenticated user of the system, I need to be able to retrieve a list of pending requests waiting for my approval.
10. As an authenticated user of the system, I need to be able to either approve or reject a pending request.
   8. I can't "approve" a given Request Transfer for more TE Bucks than I have in my account.
   9. The Request Transfer status is *Approved* if I approve, or *Rejected* if I reject the request.
   10. If the transfer is approved, the requester's account balance is increased by the amount of the request.
   11. If the transfer is approved, the requestee's account balance is decreased by the amount of the request.
   12. If the transfer is rejected, no account balance changes.


Validate all of the API's endpoints using Postman


## Datasource

The TEnmo Server depends on a database. You'll find the details about it in the `tenmo-database` folder.

A Datasource is already configured in `/src/resources/application.properties`. 

```
# datasource connection properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tenmo
spring.datasource.name=tenmo
spring.datasource.username=postgres
spring.datasource.password=postgres1
```

## Testing

### DAO integration tests

`com.techelevator.dao.BaseDaoTests` is available to use as a base class for any DAO integration test. It initializes a Datasource for testing and manages rollback of database changes between tests.

`com.techelevator.dao.JdbUserDaoTests` is available as an example for writing your own DAO integration tests.

Remember that when testing, you're not using the real database. The schema and data for the test database are in `/src/test/resources/test-data.sql`. The schema in this file *must* match the schema defined in `tenmo-database/tenmo.sql`.

