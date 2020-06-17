package com.blameo.chatsdk.repositories;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.bodies.PostIDsBody;
import com.blameo.chatsdk.models.bodies.UpdateStatusBody;
import com.blameo.chatsdk.models.bodies.UsersBody;
import com.blameo.chatsdk.models.entities.User;
import com.blameo.chatsdk.models.results.GetUsersByIdsResult;
import com.blameo.chatsdk.models.results.UserStatus;
import com.blameo.chatsdk.models.results.UsersStatusResult;
import com.blameo.chatsdk.repositories.local.BlaChatSDKDatabase;
import com.blameo.chatsdk.repositories.local.dao.UserDao;
import com.blameo.chatsdk.repositories.remote.api.APIProvider;
import com.blameo.chatsdk.repositories.remote.api.BlaChatAPI;
import com.blameo.chatsdk.repositories.remote.api.PresenceAPI;
import com.blameo.chatsdk.utils.BlaChatTextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Response;

public class UserRepositoryImpl implements UserRepository {

    private static final String TAG = "USER_REPO";
    private UserDao userDao;
    private BlaChatAPI blaChatAPI;
    private PresenceAPI presenceAPI;
    private HashMap<String, UserStatus> userStatusMap;
    private String myId;

    public UserRepositoryImpl(Context context, String myId) {
        userDao = BlaChatSDKDatabase.getInstance(context).userDao();
        userStatusMap = new HashMap<>();
        this.myId = myId;
        blaChatAPI = APIProvider.INSTANCE.getBlaChatAPI();
        presenceAPI = APIProvider.INSTANCE.getPresenceAPI();
        updateOwnPresence();
    }

    @Override
    public List<BlaUser> getUsersByIds(List<String> uIds) throws Exception {
        List<User> users = userDao.getUsersByIds(uIds.toArray(new String[0]));
        List<BlaUser> result = new ArrayList<>();
        List<String> missIdUser = new ArrayList<>();
        List<String> idExists = new ArrayList<>();

        for (User user : users) {
            idExists.add(user.getId());
        }

        for (String id : uIds) {
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

        userDao.insertMany(users);

        for (User user : users) {
            result.add(new BlaUser(user));
        }

        return result;
    }

    @Override
    public List<BlaUser> getAllUsers() throws Exception {
        List<User> users = userDao.getAllUsers();
        List<BlaUser> result = new ArrayList<>();

        Log.i(TAG, "local user size: " + users.size());
        for (User user : users) {
            result.add(new BlaUser(user));
        }

        if(result.size() ==0)
            result = fetchAllUsers();

        return result;
    }

    @Override
    public List<BlaUser> fetchAllUsers() throws Exception {

        List<BlaUser> result = new ArrayList<>();

        Response<GetUsersByIdsResult> response = blaChatAPI.getAllMembers().execute();
        if(response.isSuccessful() && response.body().getData() != null){
            for (User user : response.body().getData()) {
                result.add(new BlaUser(user));
            }
            userDao.insertMany(response.body().getData());
        }
        return result;
    }

    @Override
    public BlaUser getUserById(String id) {
        User user = userDao.getUserById(id);
        return new BlaUser(user);
    }

    @Override
    public List<BlaUser> getUsersPresence() throws Exception {

        List<User> allUsers = userDao.getAllUsers();
//        for(User u: allUsers){
//            Log.i(TAG, "status: "+u.getLastActiveAt() + " user id: "+ u.getId());
//        }

        List<BlaUser> result = new ArrayList<>();
        HashMap<String, User> usersMap = new HashMap<>();

        StringBuilder ids = new StringBuilder();
        for (User user : allUsers) {
            usersMap.put(user.getId(), user);
            ids.append(user.getId()).append(",");
        }

        PostIDsBody body = new PostIDsBody();
        body.setIds(ids.toString());

        Response<UsersStatusResult> response = presenceAPI.getUsersStatus(body).execute();
        for (UserStatus currentStatus : response.body().getData()) {
            UserStatus previousStatus = userStatusMap.get(currentStatus.getId());

            if (previousStatus == null || previousStatus.getStatus() != currentStatus.getStatus()) {
                userStatusMap.put(currentStatus.getId(), currentStatus);
                BlaUser user = new BlaUser(usersMap.get(currentStatus.getId()));
                user.setOnline(currentStatus.getStatus() == 2);
                result.add(user);
                if(previousStatus != null){
                    user.setLastActiveAt(new Date());
                    userDao.update(user);
                }else{
                    if(user.isOnline()){
                        user.setLastActiveAt(new Date());
                        userDao.update(user);
                    }
                }

            }
        }

        Log.i("updatedd", "response: " + result.size());
        return result;
    }

    @Override
    public List<BlaUser>  getAllUsersStates() {
        try {
            return getUsersPresence();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveUser(User user) {
        userDao.insert(user);
    }

    @Override
    public void updateOwnPresence() {

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                UpdateStatusBody body = new UpdateStatusBody(myId, "2");
                try {
                    Response<UsersStatusResult> response = presenceAPI.updateStatus(body).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                handler.postDelayed(this, 40000);
            }
        });
    }


}
