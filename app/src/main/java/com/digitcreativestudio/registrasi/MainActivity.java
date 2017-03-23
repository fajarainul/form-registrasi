package com.digitcreativestudio.registrasi;

import android.os.Bundle;
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
import android.widget.Toast;

import com.digitcreativestudio.registrasi.CAPTCHA.Captcha;
import com.digitcreativestudio.registrasi.CAPTCHA.MathCaptcha;
import com.digitcreativestudio.registrasi.connection.RegisterClient;
import com.digitcreativestudio.registrasi.connection.RegisterService;
import com.digitcreativestudio.registrasi.entity.District;
import com.digitcreativestudio.registrasi.entity.Province;
import com.digitcreativestudio.registrasi.entity.Regency;
import com.digitcreativestudio.registrasi.entity.Village;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Spinner idTypeSpinner, provinceSpinner, regencySpinner, districtSpinner, villageSpinner;
    EditText idEditText, nameEditText, phoneEditText, addressEditText;

    Spinner companyProvinceSpinner, companyRegencySpinner, companyDistrictSpinner, companyVillageSpinner;
    EditText companyNpwpEditText, companyNoEditText, companyNameEditText, companyAddressEditText, companyPhoneEditText;

    Spinner licenseSpinner, licenseRegionSpinner;

    Captcha captcha;

    ImageView captchaImageView;
    EditText captchaEditText;

    Button attachButton;
    Button saveButton;

    List<String> mIdTypes = new ArrayList<>();
    List<Province> provinces = new ArrayList<>();
    List<Province> provincesCompany = new ArrayList<>();
    List<Regency> regencies = new ArrayList<>();
    List<Regency> regenciesCompany = new ArrayList<>();
    List<District> districts = new ArrayList<>();
    List<District> districtsCompany = new ArrayList<>();
    List<Village> villages = new ArrayList<>();
    List<Village> villagesCompany = new ArrayList<>();

    Map<String, String> input = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        String[] defaultRegency = new String[1];
        String[] defaultDistrict = new String[1];
        String[] defaultVillage = new String[1];
        String[] defaultLicenseRegion = new String[1];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            idEditText.setHint(Html.fromHtml("ID Pemohon<sup>*</sup>", Html.FROM_HTML_MODE_LEGACY).toString());
            nameEditText.setHint(Html.fromHtml("Nama Pemohon<sup>*</sup>", Html.FROM_HTML_MODE_LEGACY).toString());
            phoneEditText.setHint(Html.fromHtml("Telp Pemohon<sup>*</sup>", Html.FROM_HTML_MODE_LEGACY).toString());
            addressEditText.setHint(Html.fromHtml("Alamat Pemohon<sup>*</sup>", Html.FROM_HTML_MODE_LEGACY).toString());

            defaultRegency[0] = Html.fromHtml("Pilih Kabupaten<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
            defaultDistrict[0] = Html.fromHtml("Pilih Kecamatan<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
            defaultVillage[0] = Html.fromHtml("Pilih Kelurahan<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
            defaultLicenseRegion[0] = Html.fromHtml("Pilih Unit Kerja<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            idEditText.setHint(Html.fromHtml("ID Pemohon<sup>*</sup>").toString());
            nameEditText.setHint(Html.fromHtml("Nama Pemohon<sup>*</sup>").toString());
            phoneEditText.setHint(Html.fromHtml("Telp Pemohon<sup>*</sup>").toString());
            addressEditText.setHint(Html.fromHtml("Alamat Pemohon<sup>*</sup>").toString());

            defaultRegency[0] = Html.fromHtml("Pilih Kabupaten<sup>*</sup>:").toString();
            defaultDistrict[0] = Html.fromHtml("Pilih Kecamatan<sup>*</sup>:").toString();
            defaultVillage[0] = Html.fromHtml("Pilih Kelurahan<sup>*</sup>:").toString();
            defaultLicenseRegion[0] = Html.fromHtml("Pilih Unit Kerja<sup>*</sup>:").toString();

            attachButton.setText(Html.fromHtml("Pilih Lampiran<sup>*</sup>...").toString());
        }

        ArrayAdapter<String> adapterRegency = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, defaultRegency);
        adapterRegency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regencySpinner.setAdapter(adapterRegency);

        String[] defaultRegencyCompany = new String[1];
        defaultRegencyCompany[0] = "Pilih Kabupaten:";
        ArrayAdapter<String> adapterRegencyCompany = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, defaultRegencyCompany);
        adapterRegency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companyRegencySpinner.setAdapter(adapterRegencyCompany);

        ArrayAdapter<String> adapterDistrict = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, defaultDistrict);
        adapterDistrict.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapterDistrict);

        String[] defaultDistrictCompany = new String[1];
        defaultDistrictCompany[0] = "Pilih Kecamatan:";
        ArrayAdapter<String> adapterDistrictCompany = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, defaultDistrictCompany);
        adapterRegency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companyDistrictSpinner.setAdapter(adapterDistrictCompany);

        ArrayAdapter<String> adapterVillage = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, defaultVillage);
        adapterVillage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villageSpinner.setAdapter(adapterVillage);

        String[] defaultVillageCompany = new String[1];
        defaultVillageCompany[0] = "Pilih Kelurahan:";
        ArrayAdapter<String> adapterVillageCompany = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, defaultVillageCompany);
        adapterRegency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companyVillageSpinner.setAdapter(adapterVillageCompany);

        ArrayAdapter<String> adapterLicenseRegion = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, defaultLicenseRegion);
        adapterLicenseRegion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        licenseRegionSpinner.setAdapter(adapterLicenseRegion);
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
        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<List<Province>> provinceCall = registerService.getProvinces();
        provinceCall.enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                Spinner spinner;
                if(isSelf){
                    provinces = response.body();
                    spinner = provinceSpinner;
                }else{
                    provincesCompany = response.body();
                    spinner = companyProvinceSpinner;
                }
                final List<Province> prov = response.body();

                String[] provinceArray = new String[prov.size()+1];
                if(isSelf) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        provinceArray[0] = Html.fromHtml("Pilih Provinsi<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
                    } else {
                        provinceArray[0] = Html.fromHtml("Pilih Provinsi<sup>*</sup>:").toString();
                    }
                }else{
                    provinceArray[0] = "Pilih Provinsi:";
                }
                for(int i = 1; i <= prov.size(); i++){
                    provinceArray[i] = prov.get(i-1).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, provinceArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

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

            }
        });
    }

    private void getRegencies(int idProvince, final boolean isSelf){
        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<List<Regency>> regencyCall = registerService.getRegencies(idProvince);
        regencyCall.enqueue(new Callback<List<Regency>>() {
            @Override
            public void onResponse(Call<List<Regency>> call, Response<List<Regency>> response) {
                Spinner spinner;
                if(isSelf){
                    regencies = response.body();
                    spinner = regencySpinner;
                }else{
                    regenciesCompany = response.body();
                    spinner = companyRegencySpinner;
                }
                final List<Regency> reg = response.body();

                String[] regencyArray = new String[reg.size()+1];
                if(isSelf) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        regencyArray[0] = Html.fromHtml("Pilih Kabupaten<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
                    } else {
                        regencyArray[0] = Html.fromHtml("Pilih Kabupaten<sup>*</sup>:").toString();
                    }
                }else{
                    regencyArray[0] = "Pilih Kabupaten:";
                }
                for(int i = 1; i <= reg.size(); i++){
                    regencyArray[i] = reg.get(i-1).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, regencyArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

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

            }
        });
    }

    public void getDistricts(int idRegency, final boolean isSelf){
        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<List<District>> districtCall = registerService.getDistricts(idRegency);
        districtCall.enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                Spinner spinner;
                if(isSelf){
                    districts = response.body();
                    spinner = regencySpinner;
                }else{
                    districtsCompany = response.body();
                    spinner = companyRegencySpinner;
                }
                final List<District> dis = response.body();

                String[] districtArray = new String[dis.size()+1];
                if(isSelf) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        districtArray[0] = Html.fromHtml("Pilih Kecamatan<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
                    } else {
                        districtArray[0] = Html.fromHtml("Pilih Kecamatan<sup>*</sup>:").toString();
                    }
                }else{
                    districtArray[0] = "Pilih Kecamatan:";
                }
                for(int i = 1; i <= dis.size(); i++){
                    districtArray[i] = dis.get(i-1).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, districtArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

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

            }
        });
    }

    public void getVillages(int idDistrict, final boolean isSelf){
        RegisterService registerService =
                RegisterClient.getClient().create(RegisterService.class);
        Call<List<Village>> villageCall = registerService.getVillages(idDistrict);
        villageCall.enqueue(new Callback<List<Village>>() {
            @Override
            public void onResponse(Call<List<Village>> call, Response<List<Village>> response) {
                Spinner spinner;
                if(isSelf){
                    villages = response.body();
                    spinner = regencySpinner;
                }else{
                    villagesCompany = response.body();
                    spinner = companyRegencySpinner;
                }
                final List<Village> vil = response.body();

                String[] districtArray = new String[vil.size()+1];
                if(isSelf) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        districtArray[0] = Html.fromHtml("Pilih Kelurahan<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
                    } else {
                        districtArray[0] = Html.fromHtml("Pilih Kelurahan<sup>*</sup>:").toString();
                    }
                }else{
                    districtArray[0] = "Pilih Kelurahan:";
                }
                for(int i = 1; i <= vil.size(); i++){
                    districtArray[i] = vil.get(i-1).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, districtArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(!(i-1 < 0)) {
                            Village village = vil.get(i-1);
                            Log.e("self "+isSelf, village.getId()+" - "+village.getName());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Village>> call, Throwable t) {

            }
        });
    }

    private void getLicenses(){
        String[] provinceArray = new String[1];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            provinceArray[0] = Html.fromHtml("Pilih Izin<sup>*</sup>:", Html.FROM_HTML_MODE_LEGACY).toString();
        }else {
            provinceArray[0] = Html.fromHtml("Pilih Izin<sup>*</sup>:").toString();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, provinceArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        licenseSpinner.setAdapter(adapter);
    }

    private void initiateCaptcha(){
        captcha = new MathCaptcha(300, 100, MathCaptcha.MathOptions.PLUS_MINUS_MULTIPLY);
//        Captcha c = new TextCaptcha(300, 100, 5, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
        captchaImageView.setImageBitmap(captcha.getImage());
        captchaImageView.setLayoutParams(new LinearLayout.LayoutParams(captcha.getWidth() *2, captcha.getHeight() *2));
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
            Toast.makeText(MainActivity.this, "Periksa kembali form yang harus diisi", Toast.LENGTH_LONG).show();

            initiateCaptcha();
            return;
        }
        if(!captcha.checkAnswer(captchaEditText.getText().toString())){
            Toast.makeText(MainActivity.this, "CAPTCHA gagal. Silahkan coba kembali.", Toast.LENGTH_LONG).show();
            captchaEditText.setText("");

            initiateCaptcha();
            return;
        }
        submit();
    }

    private void submit(){}
}
