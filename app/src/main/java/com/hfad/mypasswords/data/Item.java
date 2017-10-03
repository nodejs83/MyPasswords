package com.hfad.mypasswords.data;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;


public class Item implements Serializable {


    @DatabaseField(generatedId = true, columnName = "id")
    public int id;

    public String name;
    public String login;
    public String password;

    public boolean isGroup;

    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true)
    public Item groupItem;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public void setGroupItem(Item groupItem) {
        this.groupItem = groupItem;
    }

    public Item getGroupItem() {
        return groupItem;
    }

    public String toString(){
        return this.name;
    }

}
