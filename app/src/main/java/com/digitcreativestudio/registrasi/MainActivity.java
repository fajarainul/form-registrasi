package com.digitcreativestudio.registrasi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.digitcreativestudio.registrasi.connection.TmdbClient;
import com.digitcreativestudio.registrasi.connection.TmdbService;
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

    Button saveButton;

    Captcha captcha;
    ImageView captchaImageView;
    EditText captchaEditText;

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

        captchaImageView = (ImageView) findViewById(R.id.captcha_imageview);
        captchaEditText = (EditText) findViewById(R.id.captcha_edittext);

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        initiateCaptcha();
        getIdType();

        getProvinces(true);
        getProvinces(false);
    }

    private void getIdType(){
        mIdTypes = new ArrayList<>();
        mIdTypes.add("Pilih Identitas:");
        mIdTypes.add("KTP");
        mIdTypes.add("SIM");
        mIdTypes.add("PASSPORT");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, mIdTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idTypeSpinner.setAdapter(adapter);
    }

    private void getProvinces(final boolean isSelf){
        TmdbService tmdbService =
                TmdbClient.getClient().create(TmdbService.class);
        Call<List<Province>> provinceCall = tmdbService.getProvinces();
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
                provinceArray[0] = "Pilih Provinsi:";
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
        TmdbService tmdbService =
                TmdbClient.getClient().create(TmdbService.class);
        Call<List<Regency>> regencyCall = tmdbService.getRegencies(idProvince);
        regencyCall.enqueue(new Callback<List<Regency>>() {
            @Override
            public void onResponse(Call<List<Regency>> call, Response<List<Regency>> response) {
                if(isSelf){
                    regencies = response.body();
                }else{
                    regenciesCompany = response.body();
                }
                final List<Regency> reg = response.body();

                String[] regencyArray = new String[reg.size()+1];
                regencyArray[0] = "Pilih Kabupaten:";
                for(int i = 1; i <= reg.size(); i++){
                    regencyArray[i] = reg.get(i-1).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, regencyArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                if(isSelf)
                    regencySpinner.setAdapter(adapter);
                else
                    companyRegencySpinner.setAdapter(adapter);
//
//                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                        if(!(i-1 < 0)) {
//                            Regency regency = reg.get(i-1);
//                            getDistricts(regency.getId(), isSelf);
//                        }
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                    }
//                });
            }

            @Override
            public void onFailure(Call<List<Regency>> call, Throwable t) {

            }
        });
    }

    public void getDistricts(int idRegency, final boolean isSelf){
        TmdbService tmdbService =
                TmdbClient.getClient().create(TmdbService.class);
        Call<List<District>> districtCall = tmdbService.getDistricts(idRegency);
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
                districtArray[0] = "Pilih Kecamatan:";
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
                            Log.e("self "+isSelf, district.getId()+" - "+district.getName());
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

    public void getRegions(char type, int id, final boolean isSelf){

    }

    private void populateSpinner(char type, Spinner spinner){

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
