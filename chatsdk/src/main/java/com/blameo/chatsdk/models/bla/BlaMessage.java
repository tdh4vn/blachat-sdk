package com.blameo.chatsdk.models.bla;

import com.blameo.chatsdk.models.pojos.Message;
import com.blameo.chatsdk.models.pojos.User;
import com.blameo.chatsdk.utils.GsonUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class BlaMessage extends Message {
    private ArrayList<BlaUser> seenBy;
    private ArrayList<BlaUser> receivedBy;

    public ArrayList<BlaUser> getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(ArrayList<BlaUser> seenBy) {
        this.seenBy = seenBy;
    }

    public ArrayList<BlaUser> getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(ArrayList<BlaUser> receivedBy) {
        this.receivedBy = receivedBy;
    }

    public HashMap<String, Object> getCustomData() {
        return GsonUtil.jsonToMap(customData);
    }

    public void setCustomData(HashMap<String, Object> customData) {
        this.customData = GsonUtil.mapToJSON(customData);
    }
}
