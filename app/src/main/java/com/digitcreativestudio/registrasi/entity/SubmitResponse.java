package com.digitcreativestudio.registrasi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faqiharifian on 23/03/17.
 */

public class SubmitResponse {
    @SerializedName("error")
    boolean error;
    @SerializedName("success")
    boolean success;

    public boolean isError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }
}
