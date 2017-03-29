package com.digitcreativestudio.registrasi.response;

import com.digitcreativestudio.registrasi.entity.Village;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by faqiharifian on 29/03/17.
 */

public class VillageResponse extends Response {
    @SerializedName("result")
    List<Village> result;

    public List<Village> getResult() {
        return result;
    }
}
