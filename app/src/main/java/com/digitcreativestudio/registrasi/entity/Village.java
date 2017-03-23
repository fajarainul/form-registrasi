package com.digitcreativestudio.registrasi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faqiharifian on 23/03/17.
 */

public class Village extends Region {
    @SerializedName("id_kelurahan")
    protected int id;
    @SerializedName("nama_kelurahan")
    protected String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @SerializedName("district_id")
    private int districtId;

    public int getDistrictId() {
        return districtId;
    }
}
