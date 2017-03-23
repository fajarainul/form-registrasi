package com.digitcreativestudio.registrasi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faqiharifian on 23/03/17.
 */

public class District extends Region {
    @SerializedName("id_kecamatan")
    protected int id;
    @SerializedName("nama_kecamatan")
    protected String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @SerializedName("regency_id")
    private int regencyId;

    public int getRegencyId() {
        return regencyId;
    }
}
