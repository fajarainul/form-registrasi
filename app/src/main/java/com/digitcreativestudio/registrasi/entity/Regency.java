package com.digitcreativestudio.registrasi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faqiharifian on 23/03/17.
 */

public class Regency extends Region {
    @SerializedName("id_kabupaten")
    protected int id;
    @SerializedName("nama_kabupaten")
    protected String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @SerializedName("province_id")
    private int provinceId;

    public int getProvinceId() {
        return provinceId;
    }
}
