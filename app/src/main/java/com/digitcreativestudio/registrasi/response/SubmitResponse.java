package com.digitcreativestudio.registrasi.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faqiharifian on 23/03/17.
 */

public class SubmitResponse extends Response{
    @SerializedName("no_pendaftaran")
    String registrationNumber;
    @SerializedName("nama_pemohon")
    String name;
    @SerializedName("nama_perusahaan")
    String companyName;

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getName() {
        return name;
    }

    public String getCompanyName() {
        return companyName;
    }
}
