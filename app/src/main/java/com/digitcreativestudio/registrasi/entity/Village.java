package com.digitcreativestudio.registrasi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faqiharifian on 23/03/17.
 */

public class Village {
    @SerializedName("id_kelurahan")
    protected int id;
    @SerializedName("nama_kelurahan")
    protected String name;

    public Village() {
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
