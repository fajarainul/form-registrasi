package com.digitcreativestudio.registrasi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faqiharifian on 23/03/17.
 */

public class Province{
    @SerializedName("id_propinsi")
    protected int id;
    @SerializedName("nama_propinsi")
    protected String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
