package com.digitcreativestudio.registrasi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faqiharifian on 24/03/17.
 */

public class License {
    @SerializedName("id_perizinan")
    protected int id;
    @SerializedName("nama_perizinan")
    protected String name;

    public License() {
        this.id = 0;
        this.name = "";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
