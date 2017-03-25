package com.digitcreativestudio.registrasi;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
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
import com.digitcreativestudio.registrasi.entity.SubmitResponse;
import com.digitcreativestudio.registrasi.entity.Village;
import com.digitcreativestudio.registrasi.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 0;

    Spinner idTypeSpinner, provinceSpinner, regencySpinner, districtSpinner, villageSpinner;
    EditText idEditText, nameEditText, phoneEditText, addressEditText;

    Spinner companyProvinceSpinner, companyRegencySpinner, companyDistrictSpinner, companyVillageSpinner;
    EditText companyNpwpEditText, companyNoEditText, companyNameEditText, companyAddressEditText, companyPhoneEditText;

    Spinner licenseSpinner, licenseRegionSpinner;

    Captcha captcha;

    ImageView captchaImageView;
    EditText captchaEditText;

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

    ArrayAdapter<String> mIdTypesAdapter;
    ArrayAdapter<String> provincesAdapter;
    ArrayAdapter<String> regenciesAdapter;
    ArrayAdapter<String> districtsAdapter;
    ArrayAdapter<String> villagesAdapter;

    ArrayAdapter<String> provincesCompanyAdapter;
    ArrayAdapter<String> regenciesCompanyAdapter;
    ArrayAdapter<String> districtsCompanyAdapter;
    ArrayAdapter<String> villagesCompanyAdapter;

    ArrayAdapter<String> licensesAdapter;
    ArrayAdapter<String> licenseRegionsAdapter;

    String attachmentPath = "";
    String attachmentBase64 = "";
    Uri attachmentUri;
    File attachment;

    ProgressDialog progDialog;
    List<String> processes;
    AlertDialog alertDialog;

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (shouldAskPermissions()) {
            askPermissions();
        }

        processes = new ArrayList<>();
        progDialog = new ProgressDialog(this);
        progDialog.setIndeterminate(true);
        progDialog.setCancelable(false);

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
                validate();
            }
        });

        initiate();

        initiateCaptcha();
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

            attachButton.setText(Html.fromHtml("Pilih Lampiran<sup>*</sup>...").toString());
        }

        populateSpinner(provinceSpinner, provincesAdapter, provincesArray);
        populateSpinner(regencySpinner, regenciesAdapter, regenciesArray);
        populateSpinner(districtSpinner, districtsAdapter, districtsArray);
        populateSpinner(villageSpinner, villagesAdapter, villagesArray);

        String[] provincesCompanyArray = new String[1];
        provincesCompanyArray[0] = "Pilih Provinsi:";
        String[] regenciesCompanyArray = new String[1];
        regenciesCompanyArray[0] = "Pilih Kabupaten:";
        String[] districtsCompanyArray = new String[1];
        districtsCompanyArray[0] = "Pilih Kecamatan:";
        String[] villagesCompanyArray = new String[1];
        villagesCompanyArray[0] = "Pilih Kelurahan:";
        populateSpinner(companyProvinceSpinner, provincesCompanyAdapter, provincesCompanyArray);
        populateSpinner(companyRegencySpinner, regenciesCompanyAdapter, regenciesCompanyArray);
        populateSpinner(companyDistrictSpinner, districtsCompanyAdapter, districtsCompanyArray);
        populateSpinner(companyVillageSpinner, villagesCompanyAdapter, villagesCompanyArray);

        populateSpinner(licenseSpinner, licensesAdapter, licensesArray);
        populateSpinner(licenseRegionSpinner, licenseRegionsAdapter, licenseRegionsArray);

        findViewById(R.id.main_layout).requestFocus();
    }

    private void populateSpinner(Spinner spinner, ArrayAdapter<String> arrayAdapter, String[] strings){
        arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, strings);
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
        this.processes.remove(process);
        if(this.processes.size() == 0 && progDialog.isShowing()){
            progDialog.dismiss();
        }
    }

    private void showAlert(String title, String message, String positiveButton, DialogInterface.OnClickListener listener){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(title);
        alertDialogBuilder.setTitle(message);
        alertDialogBuilder.setPositiveButton(positiveButton, listener);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
        Call<List<Province>> provinceCall = registerService.getProvinces();
        provinceCall.enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                dismissProgressBar(process);

                Spinner spinner;
                String[] provArray;
                ArrayAdapter<String> adapter;
                if(isSelf){
                    provinces = response.body();
                    spinner = provinceSpinner;
                    adapter = provincesAdapter;
                }else{
                    provincesCompany = response.body();
                    spinner = companyProvinceSpinner;
                    adapter = provincesCompanyAdapter;
                }
                final List<Province> prov = response.body();

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

                populateSpinner(spinner, adapter, provArray);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(!(i-1 < 0)) {
                            Province province = prov.get(i-1);
                            getRegencies(province.getId(), isSelf);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "OK", null);
            }
        });
    }

    private void getRegencies(int idProvince, final boolean isSelf){
        final String process = "regencies";
        showProgressBar(process);

        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<List<Regency>> regencyCall = registerService.getRegencies(idProvince);
        regencyCall.enqueue(new Callback<List<Regency>>() {
            @Override
            public void onResponse(Call<List<Regency>> call, Response<List<Regency>> response) {
                dismissProgressBar(process);

                Spinner spinner;
                ArrayAdapter<String> adapter;
                String[] regArray;
                if(isSelf){
                    regencies = response.body();
                    spinner = regencySpinner;
                    adapter =  regenciesAdapter;
                }else{
                    regenciesCompany = response.body();
                    spinner = companyRegencySpinner;
                    adapter =  regenciesCompanyAdapter;
                }
                final List<Regency> reg = response.body();

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

                populateSpinner(spinner, adapter, regArray);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(!(i-1 < 0)) {
                            Regency regency = reg.get(i-1);
                            getDistricts(regency.getId(), isSelf);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Regency>> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "OK", null);
            }
        });
    }

    public void getDistricts(int idRegency, final boolean isSelf){
        final String process = "districts";
        showProgressBar(process);

        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<List<District>> districtCall = registerService.getDistricts(idRegency);
        districtCall.enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                dismissProgressBar(process);

                Spinner spinner;
                ArrayAdapter<String> adapter;
                String[] disArray;
                if(isSelf){
                    districts = response.body();
                    spinner = districtSpinner;
                    adapter = districtsAdapter;
                }else{
                    districtsCompany = response.body();
                    spinner = companyDistrictSpinner;
                    adapter = districtsCompanyAdapter;
                }
                final List<District> dis = response.body();

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

                populateSpinner(spinner, adapter, disArray);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(!(i-1 < 0)) {
                            District district = dis.get(i-1);
                            getVillages(district.getId(), isSelf);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<District>> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "OK", null);
            }
        });
    }

    public void getVillages(int idDistrict, final boolean isSelf){
        final String process = "villages";
        showProgressBar(process);

        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<List<Village>> villageCall = registerService.getVillages(idDistrict);
        villageCall.enqueue(new Callback<List<Village>>() {
            @Override
            public void onResponse(Call<List<Village>> call, Response<List<Village>> response) {
                dismissProgressBar(process);

                Spinner spinner;
                ArrayAdapter<String> adapter;
                String[] vilArray;
                if(isSelf){
                    villages = response.body();
                    spinner = villageSpinner;
                    adapter = villagesAdapter;
                }else{
                    villagesCompany = response.body();
                    spinner = companyVillageSpinner;
                    adapter = villagesCompanyAdapter;
                }
                final List<Village> vil = response.body();

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

                populateSpinner(spinner, adapter, vilArray);
            }

            @Override
            public void onFailure(Call<List<Village>> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "OK", null);
            }
        });
    }

    private void getLicenses(){
        final String process = "licenses";
        showProgressBar(process);

        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<List<License>> licenseCall = registerService.getLicenses();
        licenseCall.enqueue(new Callback<List<License>>() {
            @Override
            public void onResponse(Call<List<License>> call, Response<List<License>> response) {
                dismissProgressBar(process);

                licenses = response.body();

                String[] licensesArray = new String[licenses.size()+1];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    licensesArray[0] = Html.fromHtml("Pilih Izin<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
                } else {
                    licensesArray[0] = Html.fromHtml("Pilih Izin<sup>*</sup>:").toString();
                }

                for(int i = 1; i <= licenses.size(); i++){
                    licensesArray[i] = licenses.get(i-1).getName();
                }

                populateSpinner(licenseSpinner, licensesAdapter, licensesArray);

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
            }

            @Override
            public void onFailure(Call<List<License>> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "OK", null);
            }
        });
    }

    private void getLicenseRegions(int licenseId){
        final String process = "license_regions";
        showProgressBar(process);

        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<List<LicenseRegion>> licenseCall = registerService.getLicenseRegions(licenseId);
        licenseCall.enqueue(new Callback<List<LicenseRegion>>() {
            @Override
            public void onResponse(Call<List<LicenseRegion>> call, Response<List<LicenseRegion>> response) {
                dismissProgressBar(process);

                licenseRegions = response.body();

                String[] licenseRegionsArray = new String[licenses.size()+1];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    licenseRegionsArray[0] = Html.fromHtml("Pilih Unit Kerja<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
                } else {
                    licenseRegionsArray[0] = Html.fromHtml("Pilih Unit Kerja<sup>*</sup>:").toString();
                }

                for(int i = 1; i <= licenseRegions.size(); i++){
                    licenseRegionsArray[i] = licenseRegions.get(i-1).getName();
                }

                populateSpinner(licenseRegionSpinner, licenseRegionsAdapter, licenseRegionsArray);
            }

            @Override
            public void onFailure(Call<List<LicenseRegion>> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "OK", null);
            }
        });
    }

    private void initiateCaptcha(){
        captcha = new MathCaptcha(300, 100, MathCaptcha.MathOptions.PLUS_MINUS_MULTIPLY);
//        Captcha c = new TextCaptcha(300, 100, 5, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
        captchaImageView.setImageBitmap(captcha.getImage());
        captchaImageView.setLayoutParams(new LinearLayout.LayoutParams(captcha.getWidth() *2, captcha.getHeight() *2));
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
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
                    atachTextView.setText(attachment.getName());
                    attachmentBase64 = FileUtil.convertFileToByteArray(attachment);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void validate(){
        Log.e("CAPTCHA", String.valueOf(captcha.checkAnswer(captchaEditText.getText().toString())));
        if((idEditText.getText().toString().trim().equals("") ||
            nameEditText.getText().toString().trim().equals("") ||
            phoneEditText.getText().toString().trim().equals("") ||
            addressEditText.getText().toString().trim().equals("") ||
            (idTypeSpinner.getSelectedItemPosition()-1) < 0 ||
            (provinceSpinner.getSelectedItemPosition()-1) < 0 ||
            (regencySpinner.getSelectedItemPosition()-1) < 0 ||
            (districtSpinner.getSelectedItemPosition()-1) < 0 ||
            (villageSpinner.getSelectedItemPosition()-1) < 0 ||
            (licenseSpinner.getSelectedItemPosition()-1) < 0 ||
            (licenseRegionSpinner.getSelectedItemPosition()-1) < 0)){

            showAlert("Gagal", "Form dengan tanda (*) wajib diisi.", "OK", null);

            initiateCaptcha();
            return;
        }
        if(!captcha.checkAnswer(captchaEditText.getText().toString())){
            Toast.makeText(MainActivity.this, "CAPTCHA gagal. Silahkan coba kembali.", Toast.LENGTH_LONG).show();
            captchaEditText.setText("");

            showAlert("Gagal", "CAPTCHA tidak sesuai. Silahkan coba kembali.", "OK", null);

            initiateCaptcha();
            return;
        }
        if(attachmentBase64.equals("")){
            showAlert("Gagal", "Silahkan pilih file lampiran terlebih dahulu.", "OK", null);

            initiateCaptcha();
            return;
        }

        initiateCaptcha();
        submit();
    }

    private void submit(){
        final String process = "submit";
        showProgressBar(process);

        Province province = provinces.get(provinceSpinner.getSelectedItemPosition()-1);
        Regency regency = regencies.get(regencySpinner.getSelectedItemPosition()-1);
        District district = districts.get(districtSpinner.getSelectedItemPosition()-1);
        Village village = villages.get(villageSpinner.getSelectedItemPosition()-1);

        Province companyProvince = provincesCompany.get(companyProvinceSpinner.getSelectedItemPosition()-1);
        Regency companyRegency = regenciesCompany.get(companyRegencySpinner.getSelectedItemPosition()-1);
        District companyDistrict = districtsCompany.get(companyDistrictSpinner.getSelectedItemPosition()-1);
        Village companyVillage = villagesCompany.get(companyVillageSpinner.getSelectedItemPosition()-1);

        License license = licenses.get(licenseSpinner.getSelectedItemPosition()-1);
        LicenseRegion licenseRegion = licenseRegions.get(licenseRegionSpinner.getSelectedItemPosition()-1);

        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<SubmitResponse> call = registerService.submit(
                mIdTypes.get(idTypeSpinner.getSelectedItemPosition()),
                idEditText.getText().toString(),
                nameEditText.getText().toString(),
                phoneEditText.getText().toString(),
                addressEditText.getText().toString(),

                String.valueOf(province.getId()),
                province.getName(),
                String.valueOf(regency.getId()),
                regency.getName(),
                String.valueOf(district.getId()),
                district.getName(),
                String.valueOf(village.getId()),
                village.getName(),

                companyNpwpEditText.getText().toString(),
                companyNoEditText.getText().toString(),
                companyNameEditText.getText().toString(),
                companyAddressEditText.getText().toString(),
                companyPhoneEditText.getText().toString(),

                String.valueOf(companyProvince.getId()),
                companyProvince.getName(),
                String.valueOf(companyRegency.getId()),
                companyRegency.getName(),
                String.valueOf(companyDistrict.getId()),
                companyDistrict.getName(),
                String.valueOf(companyVillage.getId()),
                companyVillage.getName(),

                String.valueOf(license.getId()),
                license.getName(),
                String.valueOf(licenseRegion.getId()),
                licenseRegion.getName(),

                attachment.getName(),
                attachmentBase64);

        call.enqueue(new Callback<SubmitResponse>() {
            @Override
            public void onResponse(Call<SubmitResponse> call, Response<SubmitResponse> response) {
                dismissProgressBar(process);

                Toast.makeText(MainActivity.this, "Berhasil Disimpan.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<SubmitResponse> call, Throwable t) {
                dismissProgressBar(process);
                showAlert("Gagal", "Periksa koneksi internet anda.", "OK", null);
            }
        });
    }
}
