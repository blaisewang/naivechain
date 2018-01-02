package org.naivechain.block;

import java.util.ArrayList;
import java.util.List;

/**
 * naivechain
 * Created by blaisewang on 02/01/2018.
 */
public class UserService {
    private List<User> userList;

    UserService() {
        this.userList = new ArrayList<>();
    }

    public void addFirstUser(String peer) {
        userList.add(new User(peer, 0));
    }


    public void registerUser(String peer) {
        userList.add(new User(peer, userList.size()));
    }

    public List<User> getUserList() {
        return userList;
    }
}
