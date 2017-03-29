package com.digitcreativestudio.registrasi.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faqiharifian on 29/03/17.
 */

public class Response {
    @SerializedName("error")
    protected boolean error;
    @SerializedName("success")
    protected boolean success;
    @SerializedName("message")
    protected String message;

    public boolean isError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
