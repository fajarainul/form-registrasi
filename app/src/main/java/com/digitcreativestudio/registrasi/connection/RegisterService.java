package com.digitcreativestudio.registrasi.connection;

import com.digitcreativestudio.registrasi.response.DistrictResponse;
import com.digitcreativestudio.registrasi.response.LicenseRegionResponse;
import com.digitcreativestudio.registrasi.response.LicenseResponse;
import com.digitcreativestudio.registrasi.response.ProvinceResponse;
import com.digitcreativestudio.registrasi.response.RegencyResponse;
import com.digitcreativestudio.registrasi.response.SubmitResponse;
import com.digitcreativestudio.registrasi.response.VillageResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by faqiharifian on 23/09/16.
 */
public interface RegisterService {
    @Headers("Content-Type: application/json")
    @GET("get_provinsi.php")
    Call<ProvinceResponse> getProvinces();

    @Headers("Content-Type: application/json")
    @GET("get_kabupaten.php")
    Call<RegencyResponse> getRegencies(@Query("id") String idProvince);

    @Headers("Content-Type: application/json")
    @GET("get_kecamatan.php")
    Call<DistrictResponse> getDistricts(@Query("id") String idRegency);

    @Headers("Content-Type: application/json")
    @GET("get_kelurahan.php")
    Call<VillageResponse> getVillages(@Query("id") String idDistrict);

    @Headers("Content-Type: application/json")
    @GET("get_jenis_izin.php")
    Call<LicenseResponse> getLicenses();

    @Headers("Content-Type: application/json")
    @GET("get_unit_kerja.php")
    Call<LicenseRegionResponse> getLicenseRegions(@Query("id") String idLicense);

    @FormUrlEncoded
    @POST("new_register.php")
    Call<SubmitResponse> submit(@Field("jenis_identitas") String type,
                                @Field("id_pemohon") String id,
                                @Field("nama_pemohon") String name,
                                @Field("telp_pemohon") String phone,
                                @Field("alamat_pemohon") String address,

                                @Field("id_provinsi") String provinceId,
                                @Field("nama_provinsi") String provinceName,
                                @Field("id_kabupaten") String regencyId,
                                @Field("nama_kabupaten") String regencyName,
                                @Field("id_kecamatan") String districtId,
                                @Field("nama_kecamatan") String districtName,
                                @Field("id_kelurahan") String villageId,
                                @Field("nama_kelurahan") String villageName,

                                @Field("npwp") String npwp,
                                @Field("no_register") String noCompany,
                                @Field("nama_perusahaan") String companyName,
                                @Field("alamat_perusahaan") String companyAddress,
                                @Field("telp_perusahaan") String companyPhone,

                                @Field("id_provinsi_perusahaan") String companyProvinceId,
                                @Field("nama_provinsi_perusahaan") String companyProvinceName,
                                @Field("id_kabupaten_perusahaan") String companyRegencyId,
                                @Field("nama_kabupaten_perusahaan") String companyRegencyName,
                                @Field("id_kecamatan_perusahaan") String companyDistrictId,
                                @Field("nama_kecamatan_perusahaan") String companyDistrictName,
                                @Field("id_kelurahan_perusahaan") String companyVillageId,
                                @Field("nama_kelurahan_perusahaan") String companyVillageName,

                                @Field("jenis_izin_id") String licenseId,
                                @Field("jenis_izin_text") String licenseName,
                                @Field("unit_kerja_id") String licenseRegionId,
                                @Field("unit_kerja_text") String licenseRegionName,

                                @Field("nama_lampiran") String attachmentName,
                                @Field("lampiran") String attachment);

}
