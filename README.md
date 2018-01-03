# naivechain
Naivechain - a blockchain implementation in 200 lines of code.

### Quick start
```
git clone https://github.com/blaisewang/naivechain.git
cd naivechain
mvn clean install
java -jar naivechain.jar 8080 7001
java -jar naivechain.jar 8081 7002 ws://localhost:7001
```


### HTTP API

- mine block

  ```
  curl http://localhost:8080/mineBlock?data=some_data
  ```

- query blocks

  ```
  curl http://localhost:8080/blocks
  ```

- add peer

  ```
  curl http://localhost:8080/addPeer?node=ws://localhost:7001
  ```

- query peers

  ```
  curl http://localhost:8080/peers
  ```
  
- add user

  ```
  curl http://localhost:8080/addUser
  ```

- query users

  ```
  curl http://localhost:8080/users
  ```
  
- transfer money

  ```
  curl http://localhost:8080/transfer?user=1&node=8081&address=1
  ```
