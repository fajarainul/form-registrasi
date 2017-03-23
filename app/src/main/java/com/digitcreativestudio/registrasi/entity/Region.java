package com.digitcreativestudio.registrasi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faqiharifian on 23/03/17.
 */

public class Region {
    @SerializedName("id")
    protected int id;
    @SerializedName("name")
    protected String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
