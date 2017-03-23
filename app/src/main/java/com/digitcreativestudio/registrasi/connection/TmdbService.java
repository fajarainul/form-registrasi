package com.digitcreativestudio.registrasi.connection;

import com.digitcreativestudio.registrasi.entity.District;
import com.digitcreativestudio.registrasi.entity.Province;
import com.digitcreativestudio.registrasi.entity.Regency;
import com.digitcreativestudio.registrasi.entity.Village;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by faqiharifian on 23/09/16.
 */
public interface TmdbService {
    @Headers("Content-Type: application/json")
    @GET("get_provinsi.php")
    Call<List<Province>> getProvinces();

    @Headers("Content-Type: application/json")
    @GET("get_kabupaten.php")
    Call<List<Regency>> getRegencies(@Query("id") int idProvince);

    @Headers("Content-Type: application/json")
    @GET("get_kecamatan.php")
    Call<List<District>> getDistricts(@Query("id") int idRegency);

    @Headers("Content-Type: application/json")
    @GET("get_kelurahan.php")
    Call<List<Village>> getVillages(@Query("id") int idDistrict);
}
