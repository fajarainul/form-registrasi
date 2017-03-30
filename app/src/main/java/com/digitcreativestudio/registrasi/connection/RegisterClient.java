package com.digitcreativestudio.registrasi.connection;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by faqiharifian on 23/09/16.
 */
public class RegisterClient {
    public static final String BASE_URL = "http://fajarainul.informatikaundip.com/register/";

//    public static final String BASE_URL = "https://tranquil-garden-73496.herokuapp.com/register/";
    private static Retrofit retrofit = null;

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static Retrofit getClient() {
        if (retrofit==null) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
