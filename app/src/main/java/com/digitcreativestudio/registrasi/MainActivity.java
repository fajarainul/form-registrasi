package com.digitcreativestudio.registrasi;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.digitcreativestudio.registrasi.CAPTCHA.Captcha;
import com.digitcreativestudio.registrasi.CAPTCHA.MathCaptcha;
import com.digitcreativestudio.registrasi.connection.RegisterClient;
import com.digitcreativestudio.registrasi.connection.RegisterService;
import com.digitcreativestudio.registrasi.entity.District;
import com.digitcreativestudio.registrasi.entity.License;
import com.digitcreativestudio.registrasi.entity.LicenseRegion;
import com.digitcreativestudio.registrasi.entity.Province;
import com.digitcreativestudio.registrasi.entity.Regency;
import com.digitcreativestudio.registrasi.entity.Village;
import com.digitcreativestudio.registrasi.response.DistrictResponse;
import com.digitcreativestudio.registrasi.response.LicenseRegionResponse;
import com.digitcreativestudio.registrasi.response.LicenseResponse;
import com.digitcreativestudio.registrasi.response.ProvinceResponse;
import com.digitcreativestudio.registrasi.response.RegencyResponse;
import com.digitcreativestudio.registrasi.response.VillageResponse;
import com.digitcreativestudio.registrasi.utils.FileUtil;
import com.digitcreativestudio.registrasi.utils.PermissionUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity{
    private static final int FILE_SELECT_CODE = 0;

    Spinner idTypeSpinner, provinceSpinner, regencySpinner, districtSpinner, villageSpinner;
    EditText idEditText, nameEditText, phoneEditText, addressEditText;

    Spinner companyProvinceSpinner, companyRegencySpinner, companyDistrictSpinner, companyVillageSpinner;
    EditText companyNpwpEditText, companyNoEditText, companyNameEditText, companyAddressEditText, companyPhoneEditText;

    Spinner licenseSpinner, licenseRegionSpinner;

    Captcha captcha;

    ImageView captchaImageView;
    EditText captchaEditText;
    int captchaHeight, captchaWidth;

    Button attachButton;
    TextView atachTextView;
    Button saveButton;

    List<String> mIdTypes = new ArrayList<>();
    List<Province> provinces = new ArrayList<>();
    List<Regency> regencies = new ArrayList<>();
    List<District> districts = new ArrayList<>();
    List<Village> villages = new ArrayList<>();

    List<Province> provincesCompany = new ArrayList<>();
    List<Regency> regenciesCompany = new ArrayList<>();
    List<District> districtsCompany = new ArrayList<>();
    List<Village> villagesCompany = new ArrayList<>();

    List<License> licenses = new ArrayList<>();
    List<LicenseRegion> licenseRegions = new ArrayList<>();

    String attachmentPath = "";
    String attachmentBase64 = "";
    Uri attachmentUri;
    File attachment;

    ProgressDialog progDialog;
    List<String> processes;
    AlertDialog alertDialog;

    private String [] mimeTypes = {
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    };

    @TargetApi(23)
    protected void askPermissions(String[] permissions) {
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = new String[]{
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.INTERNET
        };

        if(PermissionUtil.shouldAskPermissions(this, permissions)){
            askPermissions(permissions);
        }

        processes = new ArrayList<>();
        progDialog = new ProgressDialog(this);
        progDialog.setIndeterminate(true);
        progDialog.setCancelable(false);
        alertDialog = new AlertDialog.Builder(this).create();

        idEditText = (EditText) findViewById(R.id.id_edittext);
        nameEditText = (EditText) findViewById(R.id.name_edittext);
        phoneEditText = (EditText) findViewById(R.id.phone_edittext);
        addressEditText = (EditText) findViewById(R.id.address_edittext);

        idTypeSpinner = (Spinner) findViewById(R.id.id_type_spinner);
        provinceSpinner = (Spinner) findViewById(R.id.province_spinner);
        regencySpinner = (Spinner) findViewById(R.id.regency_spinner);
        districtSpinner = (Spinner) findViewById(R.id.district_spinner);
        villageSpinner = (Spinner) findViewById(R.id.village_spinner);

        companyNpwpEditText = (EditText) findViewById(R.id.company_npwp_edittext);
        companyNoEditText = (EditText) findViewById(R.id.company_no_edittext);
        companyNameEditText = (EditText) findViewById(R.id.company_name_edittext);
        companyAddressEditText = (EditText) findViewById(R.id.company_address_edittext);
        companyPhoneEditText = (EditText) findViewById(R.id.company_phone_edittext);

        companyProvinceSpinner = (Spinner) findViewById(R.id.company_province_spinner);
        companyRegencySpinner = (Spinner) findViewById(R.id.company_regency_spinner);
        companyDistrictSpinner = (Spinner) findViewById(R.id.company_district_spinner);
        companyVillageSpinner = (Spinner) findViewById(R.id.company_village_spinner);

        licenseSpinner = (Spinner) findViewById(R.id.license_spinner);
        licenseRegionSpinner = (Spinner) findViewById(R.id.license_region_spinner);

        attachButton = (Button) findViewById(R.id.attachment_button);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachButton.requestFocus();
                showFileChooser();
            }
        });
        atachTextView = (TextView) findViewById(R.id.attachment_textview);

        captchaImageView = (ImageView) findViewById(R.id.captcha_imageview);
        captchaEditText = (EditText) findViewById(R.id.captcha_edittext);

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton.setText("Menyimpan...");
                showProgressBar("submit");
                validate();
            }
        });

        captchaImageView.post(new Runnable() {
            @Override
            public void run() {
                captchaWidth = captchaImageView.getMeasuredWidth();
                captchaHeight = captchaImageView.getMeasuredWidth() / 3;
                initiateCaptcha();
            }
        });

        initiate();

        getIdType();

        getProvinces(true);
        getProvinces(false);

        getLicenses();
    }

    private void initiate(){
        String[] provincesArray = new String[1];
        String[] regenciesArray = new String[1];
        String[] districtsArray = new String[1];
        String[] villagesArray = new String[1];
        String[] licensesArray = new String[1];
        String[] licenseRegionsArray = new String[1];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            idEditText.setHint(Html.fromHtml("ID Pemohon<sup>*</sup>", Html.FROM_HTML_MODE_LEGACY).toString());
            nameEditText.setHint(Html.fromHtml("Nama Pemohon<sup>*</sup>", Html.FROM_HTML_MODE_LEGACY).toString());
            phoneEditText.setHint(Html.fromHtml("Telp Pemohon<sup>*</sup>", Html.FROM_HTML_MODE_LEGACY).toString());
            addressEditText.setHint(Html.fromHtml("Alamat Pemohon<sup>*</sup>", Html.FROM_HTML_MODE_LEGACY).toString());

            captchaEditText.setHint(Html.fromHtml("<sup>*</sup>", Html.FROM_HTML_MODE_LEGACY).toString());

            provincesArray[0] = Html.fromHtml("Pilih Provinsi<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
            regenciesArray[0] = Html.fromHtml("Pilih Kabupaten<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
            districtsArray[0] = Html.fromHtml("Pilih Kecamatan<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
            villagesArray[0] = Html.fromHtml("Pilih Kelurahan<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();

            licensesArray[0] = Html.fromHtml("Pilih Izin<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
            licenseRegionsArray[0] = Html.fromHtml("Pilih Unit Kerja<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();

            attachButton.setText(Html.fromHtml("Pilih Lampiran<sup>*</sup> (max: 2Mb)", Html.FROM_HTML_MODE_LEGACY).toString());
        } else {
            idEditText.setHint(Html.fromHtml("ID Pemohon<sup>*</sup>").toString());
            nameEditText.setHint(Html.fromHtml("Nama Pemohon<sup>*</sup>").toString());
            phoneEditText.setHint(Html.fromHtml("Telp Pemohon<sup>*</sup>").toString());
            addressEditText.setHint(Html.fromHtml("Alamat Pemohon<sup>*</sup>").toString());

            captchaEditText.setHint(Html.fromHtml("<sup>*</sup>").toString());

            provincesArray[0] = Html.fromHtml("Pilih Provinsi<sup>*</sup>:").toString();
            regenciesArray[0] = Html.fromHtml("Pilih Kabupaten<sup>*</sup>:").toString();
            districtsArray[0] = Html.fromHtml("Pilih Kecamatan<sup>*</sup>:").toString();
            villagesArray[0] = Html.fromHtml("Pilih Kelurahan<sup>*</sup>:").toString();

            licensesArray[0] = Html.fromHtml("Pilih Izin<sup>*</sup>:").toString();
            licenseRegionsArray[0] = Html.fromHtml("Pilih Unit Kerja<sup>*</sup>:").toString();

            attachButton.setText(Html.fromHtml("Pilih Lampiran<sup>*</sup> (max: 2Mb)").toString());
        }

        populateSpinner(provinceSpinner, provincesArray);
        populateSpinner(regencySpinner, regenciesArray);
        populateSpinner(districtSpinner, districtsArray);
        populateSpinner(villageSpinner, villagesArray);

        String[] provincesCompanyArray = new String[1];
        provincesCompanyArray[0] = "Pilih Provinsi:";
        String[] regenciesCompanyArray = new String[1];
        regenciesCompanyArray[0] = "Pilih Kabupaten:";
        String[] districtsCompanyArray = new String[1];
        districtsCompanyArray[0] = "Pilih Kecamatan:";
        String[] villagesCompanyArray = new String[1];
        villagesCompanyArray[0] = "Pilih Kelurahan:";
        populateSpinner(companyProvinceSpinner, provincesCompanyArray);
        populateSpinner(companyRegencySpinner, regenciesCompanyArray);
        populateSpinner(companyDistrictSpinner, districtsCompanyArray);
        populateSpinner(companyVillageSpinner, villagesCompanyArray);

        populateSpinner(licenseSpinner, licensesArray);
        populateSpinner(licenseRegionSpinner, licenseRegionsArray);

        findViewById(R.id.main_layout).requestFocus();
    }

    private void populateSpinner(Spinner spinner, String[] strings){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, strings);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    private void showProgressBar(String process){
        this.processes.add(process);
        if(!progDialog.isShowing()) {
            progDialog.show();
            progDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progDialog.setContentView(R.layout.progress_bar);
        }
    }

    private void dismissProgressBar(String process){
        saveButton.setText("Simpan");
        this.processes.remove(process);
        if(this.processes.size() == 0 && progDialog.isShowing()){
            progDialog.dismiss();
        }
    }

    private void showAlert(String title, String message, String positiveButton, DialogInterface.OnClickListener listener){
        if(!alertDialog.isShowing()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setPositiveButton(positiveButton, listener);
            alertDialogBuilder.setCancelable(false);
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void getIdType(){
        mIdTypes = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mIdTypes.add(Html.fromHtml("Pilih Identitas<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString());
        }else {
            mIdTypes.add(Html.fromHtml("Pilih Identitas<sup>*</sup>:").toString());
        }
        mIdTypes.add("KTP");
        mIdTypes.add("SIM");
        mIdTypes.add("PASSPORT");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, mIdTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idTypeSpinner.setAdapter(adapter);
    }

    private void getProvinces(final boolean isSelf){
        final String process = "provinces";
        showProgressBar(process);

        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<ProvinceResponse> provinceCall = registerService.getProvinces();
        provinceCall.enqueue(new Callback<ProvinceResponse>() {
            @Override
            public void onResponse(Call<ProvinceResponse> call, Response<ProvinceResponse> response) {
                dismissProgressBar(process);

                if(response.body().isSuccess()){
                    Spinner spinner;
                    String[] provArray;
                    if(isSelf){
                        provinces = response.body().getResult();
                        spinner = provinceSpinner;
                    }else{
                        provincesCompany = response.body().getResult();
                        spinner = companyProvinceSpinner;
                    }
                    final List<Province> prov = response.body().getResult();

                    provArray = new String[prov.size()+1];
                    if(isSelf) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            provArray[0] = Html.fromHtml("Pilih Provinsi<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
                        } else {
                            provArray[0] = Html.fromHtml("Pilih Provinsi<sup>*</sup>:").toString();
                        }
                    }else{
                        provArray[0] = "Pilih Provinsi:";
                    }
                    for(int i = 1; i <= prov.size(); i++){
                        provArray[i] = prov.get(i-1).getName();
                    }

                    populateSpinner(spinner, provArray);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if(isSelf){
                                regencySpinner.setSelection(0);
                                districtSpinner.setSelection(0);
                                villageSpinner.setSelection(0);
                            }else{
                                companyRegencySpinner.setSelection(0);
                                companyDistrictSpinner.setSelection(0);
                                companyVillageSpinner.setSelection(0);
                            }
                            if(!(i-1 < 0)) {
                                Province province = prov.get(i-1);
//                                getRegencies(province.getId(), isSelf);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }else{
                    showAlert("Gagal", "Internal Server Error:\n"+response.body().getMessage(), "OK", null);
                }
            }

            @Override
            public void onFailure(Call<ProvinceResponse> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "Coba Lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = getIntent();
                        finish();
                        startActivity(i);
                    }
                });
            }
        });
    }

    private void getRegencies(int idProvince, final boolean isSelf){
        final String process = "regencies";
        showProgressBar(process);

        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<RegencyResponse> regencyCall = registerService.getRegencies(String.valueOf(idProvince));
        regencyCall.enqueue(new Callback<RegencyResponse>() {
            @Override
            public void onResponse(Call<RegencyResponse> call, Response<RegencyResponse> response) {
                dismissProgressBar(process);

                if(response.body().isSuccess()){
                    Spinner spinner;
                    String[] regArray;
                    if(isSelf){
                        regencies = response.body().getResult();
                        spinner = regencySpinner;
                    }else{
                        regenciesCompany = response.body().getResult();
                        spinner = companyRegencySpinner;
                    }
                    final List<Regency> reg = response.body().getResult();

                    regArray = new String[reg.size()+1];
                    if(isSelf) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            regArray[0] = Html.fromHtml("Pilih Kabupaten<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
                        } else {
                            regArray[0] = Html.fromHtml("Pilih Kabupaten<sup>*</sup>:").toString();
                        }
                    }else{
                        regArray[0] = "Pilih Kabupaten:";
                    }
                    for(int i = 1; i <= reg.size(); i++){
                        regArray[i] = reg.get(i-1).getName();
                    }

                    populateSpinner(spinner, regArray);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if(isSelf){
                                districtSpinner.setSelection(0);
                                villageSpinner.setSelection(0);
                            }else{
                                companyDistrictSpinner.setSelection(0);
                                companyVillageSpinner.setSelection(0);
                            }
                            if(!(i-1 < 0)) {
                                Regency regency = reg.get(i-1);
                                getDistricts(regency.getId(), isSelf);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }else{
                    showAlert("Gagal", "Internal Server Error:\n"+response.body().getMessage(), "OK", null);
                }
            }

            @Override
            public void onFailure(Call<RegencyResponse> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isSelf){
                            provinceSpinner.setSelection(0);
                        }else{
                            companyProvinceSpinner.setSelection(0);
                        }
                    }
                });
            }
        });
    }

    public void getDistricts(int idRegency, final boolean isSelf){
        final String process = "districts";
        showProgressBar(process);

        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<DistrictResponse> districtCall = registerService.getDistricts(String.valueOf(idRegency));
        districtCall.enqueue(new Callback<DistrictResponse>() {
            @Override
            public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                dismissProgressBar(process);

                if(response.body().isSuccess()){
                    Spinner spinner;
                    String[] disArray;
                    if(isSelf){
                        districts = response.body().getResult();
                        spinner = districtSpinner;
                    }else{
                        districtsCompany = response.body().getResult();
                        spinner = companyDistrictSpinner;
                    }
                    final List<District> dis = response.body().getResult();

                    disArray = new String[dis.size()+1];
                    if(isSelf) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            disArray[0] = Html.fromHtml("Pilih Kecamatan<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
                        } else {
                            disArray[0] = Html.fromHtml("Pilih Kecamatan<sup>*</sup>:").toString();
                        }
                    }else{
                        disArray[0] = "Pilih Kecamatan:";
                    }
                    for(int i = 1; i <= dis.size(); i++){
                        disArray[i] = dis.get(i-1).getName();
                    }

                    populateSpinner(spinner, disArray);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if(isSelf){
                                villageSpinner.setSelection(0);
                            }else{
                                companyVillageSpinner.setSelection(0);
                            }
                            if(!(i-1 < 0)) {
                                District district = dis.get(i-1);
                                getVillages(district.getId(), isSelf);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }else{
                    showAlert("Gagal", "Internal Server Error:\n"+response.body().getMessage(), "OK", null);
                }
            }

            @Override
            public void onFailure(Call<DistrictResponse> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isSelf){
                            regencySpinner.setSelection(0);
                        }else{
                            companyRegencySpinner.setSelection(0);
                        }
                    }
                });
            }
        });
    }

    public void getVillages(int idDistrict, final boolean isSelf){
        final String process = "villages";
        showProgressBar(process);

        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<VillageResponse> villageCall = registerService.getVillages(String.valueOf(idDistrict));
        villageCall.enqueue(new Callback<VillageResponse>() {
            @Override
            public void onResponse(Call<VillageResponse> call, Response<VillageResponse> response) {
                dismissProgressBar(process);

                if(response.body().isSuccess()){
                    Spinner spinner;
                    String[] vilArray;
                    if(isSelf){
                        villages = response.body().getResult();
                        spinner = villageSpinner;
                    }else{
                        villagesCompany = response.body().getResult();
                        spinner = companyVillageSpinner;
                    }
                    final List<Village> vil = response.body().getResult();

                    vilArray = new String[vil.size()+1];
                    if(isSelf) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            vilArray[0] = Html.fromHtml("Pilih Kelurahan<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
                        } else {
                            vilArray[0] = Html.fromHtml("Pilih Kelurahan<sup>*</sup>:").toString();
                        }
                    }else{
                        vilArray[0] = "Pilih Kelurahan:";
                    }
                    for(int i = 1; i <= vil.size(); i++){
                        vilArray[i] = vil.get(i-1).getName();
                    }

                    populateSpinner(spinner, vilArray);
                }else{
                    showAlert("Gagal", "Internal Server Error:\n"+response.body().getMessage(), "OK", null);
                }
            }

            @Override
            public void onFailure(Call<VillageResponse> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isSelf){
                            districtSpinner.setSelection(0);
                        }else{
                            companyDistrictSpinner.setSelection(0);
                        }
                    }
                });
            }
        });
    }

    private void getLicenses(){
        final String process = "licenses";
        showProgressBar(process);

        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<LicenseResponse> licenseCall = registerService.getLicenses();
        licenseCall.enqueue(new Callback<LicenseResponse>() {
            @Override
            public void onResponse(Call<LicenseResponse> call, Response<LicenseResponse> response) {
                dismissProgressBar(process);

                if(response.body().isSuccess()){
                    licenses = response.body().getResult();

                    String[] licensesArray = new String[licenses.size()+1];
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        licensesArray[0] = Html.fromHtml("Pilih Izin<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
                    } else {
                        licensesArray[0] = Html.fromHtml("Pilih Izin<sup>*</sup>:").toString();
                    }

                    for(int i = 1; i <= licenses.size(); i++){
                        licensesArray[i] = licenses.get(i-1).getName();
                    }

                    populateSpinner(licenseSpinner, licensesArray);

                    licenseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if(!(i-1 < 0)) {
                                License license = licenses.get(i-1);
                                getLicenseRegions(license.getId());
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }else{
                    showAlert("Gagal", "Internal Server Error:\n"+response.body().getMessage(), "OK", null);
                }
            }

            @Override
            public void onFailure(Call<LicenseResponse> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "Coba Lagi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = getIntent();
                        finish();
                        startActivity(i);
                    }
                });
            }
        });
    }

    private void getLicenseRegions(int licenseId){
        final String process = "license_regions";
        showProgressBar(process);

        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<LicenseRegionResponse> licenseCall = registerService.getLicenseRegions(String.valueOf(licenseId));
        licenseCall.enqueue(new Callback<LicenseRegionResponse>() {
            @Override
            public void onResponse(Call<LicenseRegionResponse> call, Response<LicenseRegionResponse> response) {
                dismissProgressBar(process);

                if(response.body().isSuccess()){
                    licenseRegions = response.body().getResult();

                    String[] licenseRegionsArray = new String[licenseRegions.size()+1];
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        licenseRegionsArray[0] = Html.fromHtml("Pilih Unit Kerja<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
                    } else {
                        licenseRegionsArray[0] = Html.fromHtml("Pilih Unit Kerja<sup>*</sup>:").toString();
                    }

                    for (int i = 1; i <= licenseRegions.size(); i++) {
                        licenseRegionsArray[i] = licenseRegions.get(i - 1).getName();
                    }

                    populateSpinner(licenseRegionSpinner, licenseRegionsArray);
                }else{
                    showAlert("Gagal", "Internal Server Error:\n"+response.body().getMessage(), "OK", null);
                }
            }

            @Override
            public void onFailure(Call<LicenseRegionResponse> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        licenseSpinner.setSelection(0);
                    }
                });
            }
        });
    }

    private void initiateCaptcha(){
        captchaEditText.setText("");
        captcha = new MathCaptcha(300, 100, MathCaptcha.MathOptions.PLUS_MINUS_MULTIPLY);
//        Captcha c = new TextCaptcha(captchaWidth/2, captchaHeight/2, 5, TextCaptcha.TextOpt'ions.NUMBERS_AND_LETTERS);
        captchaImageView.setImageBitmap(captcha.getImage());
        captchaImageView.setLayoutParams(new LinearLayout.LayoutParams(captchaWidth, captchaHeight));
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.setType("file/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Pilih Lampiran"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            showAlert("Aplikasi Tidak Ditemukan", "Silahkan install file manager terlebih dahulu.", "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://search?q=file%20manager&c=apps"));
                    startActivity(intent);
                }
            });
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    attachmentUri = data.getData();
                    attachmentPath = FileUtil.getPath(this, attachmentUri);
                    attachment = new File(attachmentPath);
                    Log.e("mime", FileUtil.getType(this, attachmentUri));
                    Log.e("exists", String.valueOf(Arrays.asList(mimeTypes).contains(FileUtil.getType(this, attachmentUri))));
                    if((attachment.length() / 1024 / 1024) > 2){
                        attachmentUri = null;
                        attachmentPath = "";
                        attachment = null;
                        atachTextView.setText("");
                        attachmentBase64 = "";

                        showAlert("Gagal", "Ukuran file tidak boleh lebih dari 2Mb.", "OK", null);
                    }else if(!Arrays.asList(mimeTypes).contains(FileUtil.getType(this, attachmentUri))){
                        attachmentUri = null;
                        attachmentPath = "";
                        attachment = null;
                        atachTextView.setText("");
                        attachmentBase64 = "";

                        showAlert("Gagal", "Silakan pilih file .xls .xlsx .doc .docx .pdf.", "OK", null);
                    }else{
                        atachTextView.setText(attachment.getName());
                        attachmentBase64 = FileUtil.convertFileToByteArray(attachment);
                        int maxLogSize = 100;
                        for(int i = 0; i <= attachmentBase64.length() / maxLogSize; i++) {
                            int start = i * maxLogSize;
                            int end = (i+1) * maxLogSize;
                            end = end > attachmentBase64.length() ? attachmentBase64.length() : end;
                            Log.e("base64", attachmentBase64.substring(start, end));
                        }
                        Log.e("filename", FileUtil.rename(attachment.getName()));
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void validate(){
//        if((idEditText.getText().toString().trim().equals("") ||
//            nameEditText.getText().toString().trim().equals("") ||
//            phoneEditText.getText().toString().trim().equals("") ||
//            addressEditText.getText().toString().trim().equals("") ||
//            (idTypeSpinner.getSelectedItemPosition()-1) < 0 ||
//            (provinceSpinner.getSelectedItemPosition()-1) < 0 ||
//            (regencySpinner.getSelectedItemPosition()-1) < 0 ||
//            (districtSpinner.getSelectedItemPosition()-1) < 0 ||
//            (villageSpinner.getSelectedItemPosition()-1) < 0 ||
//            (licenseSpinner.getSelectedItemPosition()-1) < 0 ||
//            (licenseRegionSpinner.getSelectedItemPosition()-1) < 0)){
//
//            showAlert("Gagal", "Form dengan tanda (*) wajib diisi.", "OK", null);
//
//            initiateCaptcha();
//            dismissProgressBar("submit");
//            return;
//        }
//        if(!captcha.checkAnswer(captchaEditText.getText().toString())){
//            showAlert("Gagal", "CAPTCHA tidak sesuai. Silahkan coba kembali.", "OK", null);
//
//            initiateCaptcha();
//            dismissProgressBar("submit");
//            return;
//        }
//        if(attachmentBase64.equals("")){
//            showAlert("Gagal", "Silahkan pilih file lampiran terlebih dahulu.", "OK", null);
//
//            initiateCaptcha();
//            dismissProgressBar("submit");
//            return;
//        }

        initiateCaptcha();
        submit();
    }

    private void submit(){
        final String process = "submit";

//        Province province = provinces.get(provinceSpinner.getSelectedItemPosition()-1);
//        Regency regency = regencies.get(regencySpinner.getSelectedItemPosition()-1);
//        District district = districts.get(districtSpinner.getSelectedItemPosition()-1);
//        Village village = villages.get(villageSpinner.getSelectedItemPosition()-1);
//
//        Province companyProvince = new Province ();
//        if(companyProvinceSpinner.getSelectedItemPosition() > 0){
//            provincesCompany.get(companyProvinceSpinner.getSelectedItemPosition()-1);
//        }
//        Regency companyRegency = new Regency ();
//        if(companyRegencySpinner.getSelectedItemPosition() > 0){
//            regenciesCompany.get(companyRegencySpinner.getSelectedItemPosition()-1);
//        }
//        District companyDistrict = new District ();
//        if(companyDistrictSpinner.getSelectedItemPosition() > 0){
//            districtsCompany.get(companyDistrictSpinner.getSelectedItemPosition()-1);
//        }
//        Village companyVillage = new Village ();
//        if(companyVillageSpinner.getSelectedItemPosition() > 0){
//            villagesCompany.get(companyVillageSpinner.getSelectedItemPosition()-1);
//        }
//
//        License license = licenses.get(licenseSpinner.getSelectedItemPosition()-1);
//        LicenseRegion licenseRegion = licenseRegions.get(licenseRegionSpinner.getSelectedItemPosition()-1);

//        RegisterService registerService =
//                RegisterClient.getClient().create(RegisterService.class);
//        Call<SubmitResponse> call = registerService.submit(
//                mIdTypes.get(idTypeSpinner.getSelectedItemPosition()),
//                idEditText.getText().toString(),
//                nameEditText.getText().toString(),
//                phoneEditText.getText().toString(),
//                addressEditText.getText().toString(),
//
//                String.valueOf(province.getId()),
//                province.getName(),
//                String.valueOf(regency.getId()),
//                regency.getName(),
//                String.valueOf(district.getId()),
//                district.getName(),
//                String.valueOf(village.getId()),
//                village.getName(),
//
//                companyNpwpEditText.getText().toString(),
//                companyNoEditText.getText().toString(),
//                companyNameEditText.getText().toString(),
//                companyAddressEditText.getText().toString(),
//                companyPhoneEditText.getText().toString(),
//
//                String.valueOf(companyProvince.getId() > 0 ? companyProvince.getId() : ""),
//                companyProvince.getName(),
//                String.valueOf(companyRegency.getId() > 0 ? companyRegency.getId() : ""),
//                companyRegency.getName(),
//                String.valueOf(companyDistrict.getId() > 0 ? companyDistrict.getId() : ""),
//                companyDistrict.getName(),
//                String.valueOf(companyVillage.getId() > 0 ? companyVillage.getId() : ""),
//                companyVillage.getName(),
//
//                String.valueOf(license.getId()),
//                license.getName(),
//                String.valueOf(licenseRegion.getId()),
//                licenseRegion.getName(),
//
//                FileUtil.rename(attachment.getName()),
//                attachmentBase64);

//        call.enqueue(new Callback<SubmitResponse>() {
//            @Override
//            public void onResponse(Call<SubmitResponse> call, Response<SubmitResponse> response) {
//                dismissProgressBar(process);
//                SubmitResponse submitResponse = response.body();
//                if(submitResponse.isSuccess()){
//                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                    View view = getLayoutInflater().inflate(R.layout.success_dialog, null);
//                    builder.setView(view);
//                    builder.setCancelable(false);
//                    builder.setPositiveButton("OK", null);
//                    builder.setTitle("Berhasil");
//                    ((TextView) view.findViewById(R.id.success_no_registration))
//                            .setText(submitResponse.getRegistrationNumber());
//                    ((TextView) view.findViewById(R.id.success_name))
//                            .setText(submitResponse.getName());
//                    if(!submitResponse.getCompanyName().equals(""))
//                        ((TextView) view.findViewById(R.id.success_company_name))
//                                .setText(submitResponse.getCompanyName());
//                    builder.show();
//                }else{
//                    showAlert("Gagal", "Internal Server Error:\n"+response.body().getMessage(), "OK", null);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SubmitResponse> call, Throwable t) {
//                dismissProgressBar(process);
//                showAlert("Gagal", "Periksa koneksi internet anda.", "OK", null);
//            }
//        });
        final MediaType TYPE = MediaType.parse(FileUtil.getType(this, attachmentUri));

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("jenis_identitas", /*mIdTypes.get(idTypeSpinner.getSelectedItemPosition())*/"KTP")
                .addFormDataPart("id_pemohon", /*idEditText.getText().toString()*/"121300054")
                .addFormDataPart("nama_pemohon", /*nameEditText.getText().toString()*/"Faqih")
                .addFormDataPart("telp_pemohon", /*phoneEditText.getText().toString()*/"082345075")
                .addFormDataPart("alamat_pemohon", /*addressEditText.getText().toString()*/"Kalitengah")

                .addFormDataPart("provinsi_pemohon", /*String.valueOf(province.getId())*/"13")
                .addFormDataPart("provinsi_pemohon_text", /*province.getName()*/"JAWA TENGAH")
                .addFormDataPart("kabupaten_pemohon", /*String.valueOf(regency.getId())*/"188")
                .addFormDataPart("kabupaten_pemohon_text", /*regency.getName()*/"KEBUMEN")
                .addFormDataPart("kecamatan_pemohon", /*String.valueOf(district.getId())*/"2620")
                .addFormDataPart("kecamatan_pemohon_text", /*district.getName()*/"GOMBONG")
                .addFormDataPart("kelurahan_pemohon", /*String.valueOf(village.getId())*/"31945")
                .addFormDataPart("kelurahan_pemohon_text", /*village.getName()*/"GOMBONG")

                .addFormDataPart("npwp_perusahaan", companyNpwpEditText.getText().toString())
                .addFormDataPart("no_register_perusahaan", companyNoEditText.getText().toString())
                .addFormDataPart("nama_perusahaan", companyNameEditText.getText().toString())
                .addFormDataPart("alamat_perusahaan", companyAddressEditText.getText().toString())
                .addFormDataPart("telepon_perusahaan", companyPhoneEditText.getText().toString())

                .addFormDataPart("provinsi_perusahaan", /*String.valueOf(companyProvince.getId() > 0 ? companyProvince.getId() : "")*/"")
                .addFormDataPart("provinsi_perusahaan_text", /*companyProvince.getName()*/"")
                .addFormDataPart("kabupaten_perusahaan", /*String.valueOf(companyRegency.getId() > 0 ? companyRegency.getId() : "")*/"")
                .addFormDataPart("kabupaten_perusahaan_text", /*companyRegency.getName()*/"")
                .addFormDataPart("kecamatan_perusahaan", /*String.valueOf(companyDistrict.getId() > 0 ? companyDistrict.getId() : "")*/"")
                .addFormDataPart("kecamatan_perusahaan_text", /*companyDistrict.getName()*/"")
                .addFormDataPart("kelurahan_perusahaan", /*String.valueOf(companyVillage.getId() > 0 ? companyVillage.getId() : "")*/"")
                .addFormDataPart("kelurahan_perusahaan_text", /*companyVillage.getName()*/"")

                .addFormDataPart("jenis_izin", /*String.valueOf(license.getId())*/"246")
                .addFormDataPart("nama_perizinan", /*license.getName()*/"IZIN KERJA PERAWAT ANESTESI (  SIKPA ) TERNATE")
                .addFormDataPart("unit_kerja_id", /*String.valueOf(licenseRegion.getId())*/"13481")
                .addFormDataPart("unit_kerja_text", /*licenseRegion.getName()*/"DINAS PENANAMAN MODAL DAN PELAYANAN PERIZINAN TERPADU SATU PINTU")

                .addFormDataPart("lampiran", FileUtil.rename(attachment.getName()), RequestBody.create(TYPE, attachment))
                .addFormDataPart("btn_submit", "true")
                .build();

//        (new MyAsyncTask()).execute(requestBody);

        Map<String, RequestBody> map = new HashMap<>();
        map.put("jenis_identitas", RequestBody.create(MultipartBody.FORM, "KTP"));
        map.put("id_pemohon", RequestBody.create(MultipartBody.FORM, "121300054"));
        map.put("nama_pemohon", RequestBody.create(MultipartBody.FORM, "Faqih"));
        map.put("telp_pemohon", RequestBody.create(MultipartBody.FORM, "082345075"));
        map.put("alamat_pemohon", RequestBody.create(MultipartBody.FORM, "Kalitengah"));

        map.put("provinsi_pemohon", RequestBody.create(MultipartBody.FORM, "13"));
        map.put("provinsi_pemohon_text", RequestBody.create(MultipartBody.FORM, "JAWA TENGAH"));
        map.put("kabupaten_pemohon", RequestBody.create(MultipartBody.FORM, "188"));
        map.put("kabupaten_pemohon_text", RequestBody.create(MultipartBody.FORM, "KEBUMEN"));
        map.put("kecamatan_pemohon", RequestBody.create(MultipartBody.FORM, "2620"));
        map.put("kecamatan_pemohon_text", RequestBody.create(MultipartBody.FORM, "GOMBONG"));
        map.put("kelurahan_pemohon", RequestBody.create(MultipartBody.FORM, "31945"));
        map.put("kelurahan_pemohon_text", RequestBody.create(MultipartBody.FORM, "GOMBONG"));

        map.put("npwp_perusahaan", RequestBody.create(MultipartBody.FORM, companyNpwpEditText.getText().toString()));
        map.put("no_register_perusahaan", RequestBody.create(MultipartBody.FORM, companyNoEditText.getText().toString()));
        map.put("nama_perusahaan", RequestBody.create(MultipartBody.FORM, companyNameEditText.getText().toString()));
        map.put("alamat_perusahaan", RequestBody.create(MultipartBody.FORM, companyAddressEditText.getText().toString()));
        map.put("telepon_perusahaan", RequestBody.create(MultipartBody.FORM, companyPhoneEditText.getText().toString()));

        map.put("provinsi_perusahaan", RequestBody.create(MultipartBody.FORM, ""));
        map.put("provinsi_perusahaan_text", RequestBody.create(MultipartBody.FORM, ""));
        map.put("kabupaten_perusahaan", RequestBody.create(MultipartBody.FORM, ""));
        map.put("kabupaten_perusahaan_text", RequestBody.create(MultipartBody.FORM, ""));
        map.put("kecamatan_perusahaan", RequestBody.create(MultipartBody.FORM, ""));
        map.put("kecamatan_perusahaan_text", RequestBody.create(MultipartBody.FORM, ""));
        map.put("kelurahan_perusahaan", RequestBody.create(MultipartBody.FORM, ""));
        map.put("kelurahan_perusahaan_text", RequestBody.create(MultipartBody.FORM, ""));

        map.put("jenis_izin", RequestBody.create(MultipartBody.FORM, "246"));
        map.put("nama_perizinan", RequestBody.create(MultipartBody.FORM, "IZIN KERJA PERAWAT ANESTESI (  SIKPA ) TERNATE"));
        map.put("unit_kerja_id", RequestBody.create(MultipartBody.FORM, "13481"));
        map.put("unit_kerja_text", RequestBody.create(MultipartBody.FORM, "DINAS PENANAMAN MODAL DAN PELAYANAN PERIZINAN TERPADU SATU PINTU"));

        map.put("btn_submit", RequestBody.create(MultipartBody.FORM, "true"));

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ternatekota.sicantik.layanan.go.id")
                .client(httpClient.build())
                .build();

        RequestBody filePart = RequestBody.create(MediaType.parse(FileUtil.getType(this, attachmentUri)), attachment);

        MultipartBody.Part part = MultipartBody.Part.createFormData("lampiran", attachment.getName(), filePart);

        RegisterService registerClient = retrofit.create(RegisterService.class);
        Call<ResponseBody> call = registerClient.submit2(map, part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseStr = response.body().string();
                    Document doc = Jsoup.parse(responseStr);
                    Element blockContent = doc.select(".block-content").first();
                    Element table = blockContent.getElementsByTag("table").first();
                    Elements bs = table.getElementsByTag("b");
                    for (Element b : bs) {
                        Log.e("ke berapa", b.toString());
                    }
                    int maxLogSize = 100;
                    for (int i = 0; i <= responseStr.length() / maxLogSize; i++) {
                        int start = i * maxLogSize;
                        int end = (i + 1) * maxLogSize;
                        end = end > responseStr.length() ? responseStr.length() : end;
                        Log.e("RESPONSE", responseStr.substring(start, end));
                    }
                }catch (IOException ie){
                    ie.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


//    public static class MyAsyncTask extends AsyncTask<RequestBody, Void, Void>{
//        @Override
//        protected Void doInBackground(RequestBody... params) {
//            Request request = new Request.Builder()
//                    .url("http://ternatekota.sicantik.layanan.go.id/perizinan_online")
//                    .post(params[0])
//                    .build();
//
//            try {
//                OkHttpClient client = new OkHttpClient();
//                okhttp3.Response response = client.newCall(request).execute();
//                String responseStr = response.body().string();
//                Document doc = Jsoup.parse(responseStr);
//                Element blockContent = doc.select(".block-content").first();
//                Element table = blockContent.getElementsByTag("table").first();
//                Elements bs = table.getElementsByTag("b");
//                for(Element b : bs){
//                    Log.e("ke berapa", b.toString());
//                }
////                int maxLogSize = 100;
////                for(int i = 0; i <= responseStr.length() / maxLogSize; i++) {
////                    int start = i * maxLogSize;
////                    int end = (i+1) * maxLogSize;
////                    end = end > responseStr.length() ? responseStr.length() : end;
////                    Log.e("RESPONSE", responseStr.substring(start, end));
////                }
//            }catch (IOException ie){
//                ie.printStackTrace();
//            }
//            return null;
//        }
//    }
}
