package br.com.alura.ceep.api.retrofit;

import br.com.alura.ceep.api.retrofit.service.FeedbackService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

public class RetrofitInicializador {

    private final Retrofit retrofit;

    public RetrofitInicializador() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.65.40:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public FeedbackService getFeedbackService(){
        return retrofit.create(FeedbackService.class);
    }
}
