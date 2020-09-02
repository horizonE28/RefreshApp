package com.example.refreshapp;

import java.time.LocalTime;
import java.util.List;

public class FData {
    public float chiller_val;
    public float crisper_val;
    public float freezer_val;
    public float current_val;
    public boolean relay_val;
    public int bot_door;
    public int top_door;
    public String timestamp;

    public float getChiller_val() {
        return chiller_val;
    }

    public void setChiller_val(float chiller_val) {
        this.chiller_val = chiller_val;
    }

    public float getCrisper_val() {
        return crisper_val;
    }

    public void setCrisper_val(float crisper_val) {
        this.crisper_val = crisper_val;
    }

    public float getFreezer_val() {
        return freezer_val;
    }

    public void setFreezer_val(float freezer_val) {
        this.freezer_val = freezer_val;
    }

    public float getCurrent_val() {
        return current_val;
    }

    public void setCurrent_val(float current_val) {
        this.current_val = current_val;
    }

    public boolean isRelay_val() {
        return relay_val;
    }

    public void setRelay_val(boolean relay_val) {
        this.relay_val = relay_val;
    }

    public int getBot_door() {
        return bot_door;
    }

    public void setBot_door(int bot_door) {
        this.bot_door = bot_door;
    }

    public int getTop_door() {
        return top_door;
    }

    public void setTop_door(int top_door) {
        this.top_door = top_door;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public FData(){}

    public FData(float t,float t2, float t3, float c,boolean r, String ts, int bot_door, int top_door){
        this.chiller_val=t;
        this.crisper_val=t2;
        this.freezer_val=t3;
        this.current_val=c;
        this.relay_val=r;
        this.timestamp=ts;
        this.bot_door = bot_door;
        this.top_door = top_door;
    }

}