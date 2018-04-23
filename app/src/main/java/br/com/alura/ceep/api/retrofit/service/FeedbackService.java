package br.com.alura.ceep.api.retrofit.service;

import br.com.alura.ceep.model.Feedback;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FeedbackService {

    @POST("feedback")
    Call<Feedback> salva(@Body Feedback feedback);

}
