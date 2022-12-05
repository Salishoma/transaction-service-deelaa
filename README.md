# transaction-service-deelaa
A transaction service where a user can register as a merchant and perform transactions such as crediting his account or sending of money.
For crediting of account the payment platform used is paystack where the user add money to his account and a code is sent to confirm payment.
Since a user can make request to update his account more than once without first confirming before another request, to ensure the users payment code is not lost, it is stored using the redis server, and once a user completes payment, the code is removed(to prevent multiple confirm with just one payment code).

First, install redis server, then start the server with the command 
**redis-server**.
