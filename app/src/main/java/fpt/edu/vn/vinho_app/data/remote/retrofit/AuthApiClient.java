package fpt.edu.vn.vinho_app.data.remote.retrofit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthApiClient {
    private static final String BASE_URL = "https://10.0.2.2:7027/api/"; // Use 10.0.2.2 for Android emulator to connect to localhost
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = getUnsafeOkHttpClientBuilder();
            httpClient.addInterceptor(chain -> {
                SharedPreferences sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                String token = sharedPref.getString("token", null);
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                if (token != null) {
                    requestBuilder.header("Authorization", "Bearer " + token);
                }
                Request request = requestBuilder.build();
                return chain.proceed(request);
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    // This is insecure, only for development purposes
    private static OkHttpClient.Builder getUnsafeOkHttpClientBuilder() {
        try {
            // Create a trust manager that does not validate certificate chains
            @SuppressLint("CustomX509TrustManager") final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType){}

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType){}

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            // Add the timeouts here
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
            builder.writeTimeout(60, TimeUnit.SECONDS);

            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
