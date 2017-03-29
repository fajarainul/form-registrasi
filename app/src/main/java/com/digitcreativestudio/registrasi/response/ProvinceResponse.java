package com.digitcreativestudio.registrasi.response;

import com.digitcreativestudio.registrasi.entity.Province;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by faqiharifian on 29/03/17.
 */

public class ProvinceResponse extends Response {
    @SerializedName("result")
    List<Province> result;

    public List<Province> getResult() {
        return result;
    }
}
