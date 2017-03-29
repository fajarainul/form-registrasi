package com.digitcreativestudio.registrasi.response;

import com.digitcreativestudio.registrasi.entity.District;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by faqiharifian on 29/03/17.
 */

public class DistrictResponse extends Response {
    @SerializedName("result")
    List<District> result;

    public List<District> getResult() {
        return result;
    }
}
