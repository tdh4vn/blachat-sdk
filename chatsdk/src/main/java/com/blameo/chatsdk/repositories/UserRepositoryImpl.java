package com.blameo.chatsdk.repositories;

import android.content.Context;

import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.bodies.UsersBody;
import com.blameo.chatsdk.models.entities.User;
import com.blameo.chatsdk.models.results.GetUsersByIdsResult;
import com.blameo.chatsdk.repositories.local.BlaChatSDKDatabase;
import com.blameo.chatsdk.repositories.local.dao.UserDao;
import com.blameo.chatsdk.repositories.remote.api.APIProvider;
import com.blameo.chatsdk.repositories.remote.api.BlaChatAPI;
import com.blameo.chatsdk.utils.BlaChatTextUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class UserRepositoryImpl implements UserRepository {

    private UserDao userDao;
    private BlaChatAPI blaChatAPI;

    public UserRepositoryImpl(Context context) {
        userDao = BlaChatSDKDatabase.getInstance(context).userDao();
        blaChatAPI = APIProvider.INSTANCE.getBlaChatAPI();
    }

    @Override
    public List<BlaUser> getUsersByIds(List<String> uIds) throws Exception {
        List<User> users = userDao.getUsersByIds(uIds.toArray(new String[0]));
        List<BlaUser> result = new ArrayList<>();
        List<String> missIdUser = new ArrayList<>();
        List<String> idExists = new ArrayList<>();

        for (User user: users) {
            idExists.add(user.getId());
        }

        for (String id: uIds) {
            if (!BlaChatTextUtils.containsInList(id, idExists)) {
                missIdUser.add(id);
            }
        }

        if (!missIdUser.isEmpty()) {
            Response<GetUsersByIdsResult> response = blaChatAPI.getUsersByIds(new UsersBody(
                    missIdUser
            )).execute();

            users.addAll(response.body().getData());
        }


        for (User user: users) {
            result.add(new BlaUser(user));
        }

        return result;
    }

    @Override
    public List<BlaUser> getAllUser() {
        List<User> users = userDao.getAllUsers();
        List<BlaUser> result = new ArrayList<>();
        for (User user: users) {
            result.add(new BlaUser(user));
        }
        return result;
    }

    @Override
    public BlaUser getUserById(String id) {
        User user = userDao.getUserById(id);
        return new BlaUser(user);
    }
}
