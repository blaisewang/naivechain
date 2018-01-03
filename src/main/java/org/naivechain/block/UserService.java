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

    public void registerUser(int node) {
        userList.add(new User(node, userList.size()));
    }


    public boolean isValIdUser(User user) {
        return userList.contains(user);
    }

    public List<User> getUserList() {
        return userList;
    }
}
