# naivechain
Naivechain - a blockchain implementation in 200 lines of code.

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
  curl http://localhost:3030/mineBlock?user=0
  ```

- query blocks

  ```
  curl http://localhost:3030/blocks
  ```

- add peer

  ```
  curl http://localhost:3030/addPeer?node=ws://localhost:4001
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
  
- transfer money

  ```
  curl http://localhost:3030/transfer?user=0&node=3031&address=0
  ```
