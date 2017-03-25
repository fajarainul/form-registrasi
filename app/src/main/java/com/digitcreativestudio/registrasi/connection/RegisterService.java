package com.digitcreativestudio.registrasi.connection;

import com.digitcreativestudio.registrasi.entity.District;
import com.digitcreativestudio.registrasi.entity.License;
import com.digitcreativestudio.registrasi.entity.LicenseRegion;
import com.digitcreativestudio.registrasi.entity.Province;
import com.digitcreativestudio.registrasi.entity.Regency;
import com.digitcreativestudio.registrasi.entity.SubmitResponse;
import com.digitcreativestudio.registrasi.entity.Village;

import java.util.List;

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

    @Headers("Content-Type: application/json")
    @GET("get_kelurahan.php")
    Call<List<License>> getLicenses();

    @Headers("Content-Type: application/json")
    @GET("get_kelurahan.php")
    Call<List<LicenseRegion>> getLicenseRegions(@Query("id") int idLicense);

    @FormUrlEncoded
    @POST("register.php")
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

                                @Field("id_izin") String licenseId,
                                @Field("nama_izin") String licenseName,
                                @Field("id_unit_kerja") String licenseRegionId,
                                @Field("nama_unit_kerja") String licenseRegionName,

                                @Field("nama_lampiran") String attachmentName,
                                @Field("lampiran") String attachment);

}
