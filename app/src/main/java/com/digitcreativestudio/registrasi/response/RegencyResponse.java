package com.digitcreativestudio.registrasi.response;

import com.digitcreativestudio.registrasi.entity.Regency;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by faqiharifian on 29/03/17.
 */

public class RegencyResponse extends Response {
    @SerializedName("result")
    List<Regency> result;

    public List<Regency> getResult() {
        return result;
    }
}
