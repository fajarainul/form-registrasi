package com.digitcreativestudio.registrasi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faqiharifian on 24/03/17.
 */

public class License {
    @SerializedName("id_izin")
    protected int id;
    @SerializedName("nama_izin")
    protected String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
