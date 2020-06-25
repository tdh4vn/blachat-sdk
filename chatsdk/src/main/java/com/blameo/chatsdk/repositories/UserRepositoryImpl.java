package com.blameo.chatsdk.repositories;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.blameo.chatsdk.models.bla.BlaPresenceType;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Response;

public class UserRepositoryImpl implements UserRepository {

    private static final String TAG = "USER_REPO";

    private UserDao userDao;

    private BlaChatAPI blaChatAPI;

    private PresenceAPI presenceAPI;

    private HashMap<String, UserStatus> userStatusMap;

    private String myId;

    private final HashMap<String, BlaUser> usersMap = new HashMap<>();

    private static UserRepository shareInstance = null;

    public static UserRepository getInstance(Context context, String myId) {
        if (shareInstance == null) {
            shareInstance = new UserRepositoryImpl(context, myId);
        }
        return shareInstance;
    }

    public static UserRepository getInstance() {
        if (shareInstance != null) {
            return shareInstance;
        }

        throw new RuntimeException();
    }

    private UserRepositoryImpl(Context context, String myId) {
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

        synchronized (usersMap) {
            for (User user : users) {
                BlaUser blaUser = new BlaUser(user);
                result.add(blaUser);
                usersMap.put(blaUser.getId(), blaUser);
            }
        }

        return result;
    }

    @Override
    public List<BlaUser> fetchAllUsers() throws Exception {
        return null;
    }

    @Override
    public String getMyId() {
        return this.myId;
    }

    @Override
    public void updateFCMToken(String imei, String fcmToken) throws IOException {
        blaChatAPI.updateFCMToken(imei, fcmToken).execute();
    }

    @Override
    public void deleteFCMToken(String imei) throws IOException {
        blaChatAPI.deleteFCMToken(imei).execute();
    }

    @Override
    public BlaUser getUserById(String id) {
        try {
            User user = usersMap.get(id);
            if (user != null) return new BlaUser(user);
            user = userDao.getUserById(id);
            if (user == null) {
                List<User> users = syncUserByIds(Collections.singletonList(id));
                if (users != null && users.size() > 0) {
                    user = users.get(0);
                    usersMap.put(user.getId(), new BlaUser(user));
                } else {
                    return null;
                }
            }
            usersMap.put(user.getId(), new BlaUser(user));
            return new BlaUser(user);
        } catch (Exception e) {
            return null;
        }
    }

    private List<User> syncUserByIds(List<String> uIds) throws IOException {
        Response<GetUsersByIdsResult> res = blaChatAPI.getUsersByIds(new UsersBody(uIds)).execute();
        if (res.isSuccessful() && res.body() != null) {

            userDao.insertMany(res.body().getData());

            for (User u: res.body().getData()) {
                usersMap.put(u.getId(), new BlaUser(u));
            }

            return res.body().getData();
        }
        return null;
    }

    @Override
    public List<BlaUser> getUsersPresence() throws Exception {

        List<User> allUsers = userDao.getAllUsers();

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
        if (response.body() != null && response.body().getData() != null) {
            for (UserStatus currentStatus : response.body().getData()) {
                UserStatus previousStatus = userStatusMap.get(currentStatus.getId());

                if (previousStatus == null || previousStatus.getStatus() != currentStatus.getStatus()) {
                    userStatusMap.put(currentStatus.getId(), currentStatus);
                    if (usersMap.get(currentStatus.getId()) != null) {
                        BlaUser user = new BlaUser(Objects.requireNonNull(usersMap.get(currentStatus.getId())));
                        user.setOnline(currentStatus.getStatus() == BlaPresenceType.ONLINE.getValue());
                        result.add(user);
                        if(previousStatus != null){
                            user.setLastActiveAt(new Date());
                            userDao.update(user);
                        } else {
                            if(user.isOnline()){
                                user.setLastActiveAt(new Date());
                                userDao.update(user);
                            }
                        }
                    }
                }
            }
        }
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
        usersMap.put(user.getId(), new BlaUser(user));
    }

    @Override
    public void updateOwnPresence() {
        UpdateStatusBody body = new UpdateStatusBody(myId, BlaPresenceType.ONLINE.toString());
        try {
            presenceAPI.updateStatus(body).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
