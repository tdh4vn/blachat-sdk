package com.blameo.chatsdk.handlers;




import com.blameo.chatsdk.blachat.BlaPresenceListener;
import com.blameo.chatsdk.models.bla.BlaPresenceType;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.bodies.UpdateStatusBody;
import com.blameo.chatsdk.models.results.UsersStatusResult;
import com.blameo.chatsdk.repositories.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;

public class PresenceHandlerImpl implements PresenceHandler {

    private ScheduledExecutorService updatePresenceSchedule;

    private ScheduledExecutorService putMyPresenceSchedule;

    private Vector<BlaPresenceListener> blaPresenceListeners;

    private UserRepository userRepository;

    public PresenceHandlerImpl(UserRepository userRepository) {
        blaPresenceListeners = new Vector<>();
        updatePresenceSchedule = Executors.newSingleThreadScheduledExecutor();
        putMyPresenceSchedule = Executors.newSingleThreadScheduledExecutor();
        this.userRepository = userRepository;
    }

    @Override
    public void addListener(BlaPresenceListener blaPresenceListener) {
        blaPresenceListeners.add(blaPresenceListener);
    }

    @Override
    public void removeListener(BlaPresenceListener blaPresenceListener) {
        blaPresenceListeners.remove(blaPresenceListener);
    }

    @Override
    public void startHandler() {
        updatePresenceSchedule.scheduleAtFixedRate(() -> {
            try {
                List<BlaUser> users = userRepository.getAllUsersStates();
                if (users != null && users.size() > 0) {
                    for (BlaPresenceListener blaPresenceListener: blaPresenceListeners) {
                        blaPresenceListener.onUpdate(users);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1, 1, TimeUnit.MINUTES);


        putMyPresenceSchedule.scheduleAtFixedRate(() -> {
            try {
                userRepository.updateOwnPresence();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 30, 30, TimeUnit.SECONDS);

    }

}
