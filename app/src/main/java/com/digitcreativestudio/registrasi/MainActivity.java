package com.digitcreativestudio.registrasi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.digitcreativestudio.registrasi.CAPTCHA.Captcha;
import com.digitcreativestudio.registrasi.CAPTCHA.MathCaptcha;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    List<String> mIdTypes;
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

        getProvinces();
        getCompanyProvinces();
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

    private void getProvinces(){

    }

    private void getCompanyProvinces(){

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
