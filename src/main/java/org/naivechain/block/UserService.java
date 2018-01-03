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
        for (User aUser : userList) {
            if (aUser.getNode() == user.getNode() && aUser.getAddress() == user.getAddress()) {
                return true;
            }
        }
        return false;
    }

    public List<User> getUserList() {
        return userList;
    }
}
