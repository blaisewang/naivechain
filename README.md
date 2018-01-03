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

- query blocks

  ```
  curl http://localhost:8080/blocks
  ```

- mine block

  ```
  curl http://localhost:8080/mineBlock?data=some_data
  ```

- add node

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
  curl http://localhost:{node}/transfer?user={user_address}&node={target_node}&address={target_address}
  ```
