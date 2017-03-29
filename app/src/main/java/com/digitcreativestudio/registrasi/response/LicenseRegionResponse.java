package com.digitcreativestudio.registrasi.response;

import com.digitcreativestudio.registrasi.entity.LicenseRegion;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by faqiharifian on 29/03/17.
 */

public class LicenseRegionResponse extends Response {
    @SerializedName("result")
    List<LicenseRegion> result;

    public List<LicenseRegion> getResult() {
        return result;
    }
}
