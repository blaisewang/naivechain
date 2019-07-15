# naivechain
Naivechain - a blockchain implementation in ~~200~~ 1000 lines of code.

### Quick start
```
git clone https://github.com/blaisewang/naivechain.git
cd naivechain
mvn clean install
java -jar naivechain.jar 3030 4001
java -jar naivechain.jar 3031 4002 ws://localhost:4001
```


### HTTP API

- mine block

  ```
  curl -d "miner=0&broadcast=true" http://localhost:3030/mineBlock
  ```

- query blocks

  ```
  curl http://localhost:3030/blocks
  ```
  
- broadcast blocks

  ```
  curl -d "miner=0" http://localhost:3030/broadcast
  ```

- add peer

  ```
  curl -d "peer=ws://localhost:4001" http://localhost:3030/addPeer
  ```

- query peers

  ```
  curl http://localhost:3030/peers
  ```
  
- add user

  ```
  curl http://localhost:3030/addUser
  ```

- query users

  ```
  curl http://localhost:3030/users
  ```
  
- add transaction

  ```
  curl -d "payer=0&payee=3031.0&amount=2&ignore=false" http://localhost:3030/addTransaction
  ```

- query transactions

  ```
  curl http://localhost:3030/transactions
  ```
