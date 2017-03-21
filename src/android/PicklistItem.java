package com.commontime.plugin;

import org.json.JSONException;
import org.json.JSONObject;

public class PicklistItem
{
    private int id;
    private String partNo;
    private String partDescription;
    private String partGty;
    private String title;

    public PicklistItem(int id, String partNo, String partDescription, String partGty, String title) {
        this.id = id;
        this.partNo = partNo;
        this.partDescription = partDescription;
        this.partGty = partGty;
        this.title = title;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("partNo", partNo);
        jo.put("partDescription", partDescription);
        jo.put("partGty", partGty);
        jo.put("title", title);
        return jo;
    }
}