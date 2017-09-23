package com.hfad.mypasswords.data;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by Khaled Jamal on 23/09/2017.
 */

public class Password implements Serializable {

    @DatabaseField(generatedId = true, columnName = "id")
    public int id;

    public String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
