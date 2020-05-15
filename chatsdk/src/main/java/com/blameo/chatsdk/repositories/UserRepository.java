package com.blameo.chatsdk.repositories;

import com.blameo.chatsdk.models.bla.BlaUser;

import java.util.List;

public interface UserRepository {
    List<BlaUser> getUsersByIds(List<String> uIds) throws Exception ;

    List<BlaUser> getAllUser();

    BlaUser getUserById(String id);

    List<BlaUser> getUsersPresence() throws Exception;

    void updateOwnPresence() throws Exception;
}
