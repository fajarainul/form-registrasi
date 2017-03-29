package com.digitcreativestudio.registrasi.response;

import com.digitcreativestudio.registrasi.entity.License;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by faqiharifian on 29/03/17.
 */

public class LicenseResponse extends Response {
    @SerializedName("result")
    List<License> result;

    public List<License> getResult() {
        return result;
    }
}
