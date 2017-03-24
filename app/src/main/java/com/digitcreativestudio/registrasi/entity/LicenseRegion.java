package com.digitcreativestudio.registrasi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faqiharifian on 24/03/17.
 */

public class LicenseRegion {
    @SerializedName("id_unit_kerja")
    protected int id;
    @SerializedName("nama_unit_kerja")
    protected String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
