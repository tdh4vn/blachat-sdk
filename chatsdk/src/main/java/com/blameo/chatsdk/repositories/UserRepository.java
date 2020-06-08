package com.blameo.chatsdk.repositories;

import com.blameo.chatsdk.blachat.BlaPresenceListener;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.bla.BlaUserPresence;
import com.blameo.chatsdk.models.entities.User;

import java.io.IOException;
import java.util.List;

public interface UserRepository {
    List<BlaUser> getUsersByIds(List<String> uIds) throws Exception ;

    List<BlaUser> getAllUsers() throws IOException;

    BlaUser getUserById(String id);

    List<BlaUserPresence> getUsersPresence() throws Exception;

    void updateOwnPresence() throws Exception;

    List<BlaUserPresence> getAllUsersStates();

    void saveUser(User user);
}
