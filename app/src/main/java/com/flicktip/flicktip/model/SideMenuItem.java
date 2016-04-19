package com.flicktip.flicktip.model;

/**
 * Created by Cinthia on 16/02/2016.
 */
public class SideMenuItem {

    private String title;
    private int icon;
    private String count = "0";

    private boolean isCounterVisible = false;

    public SideMenuItem(){
    }

    public SideMenuItem(String title, int icon) {
        this.icon = icon;
        this.title = title;
    }

    public SideMenuItem(String title, int icon, String count, boolean isCounterVisible) {
        this.title = title;
        this.icon = icon;
        this.count = count;
        this.isCounterVisible = isCounterVisible;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public boolean getCounterVisibility() {
        return isCounterVisible;
    }

    public void setCounterVisibilityBoolean(Boolean isCounterVisible) {
        this.isCounterVisible = isCounterVisible;
    }
}
