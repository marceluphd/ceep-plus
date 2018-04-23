package br.com.alura.ceep.ui.activity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import br.com.alura.ceep.R;
import br.com.alura.ceep.api.retrofit.RetrofitInicializador;
import br.com.alura.ceep.model.Erro;
import br.com.alura.ceep.model.Feedback;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity {

    private static final String CONTEUDO = "conteudo";
    private static final String EMAIL = "email";
    private static final String FEEDBACK_ENVIADO_COM_SUCESSO = "Feedback enviado com sucesso!";
    private static final String OCORREU_UM_PROBLEMA = "Ocorreu um problema";
    private static final String SEM_CONEXAO_COM_A_INTERNET = "Sem conexão com a internet";
    private static final String TITULO_APPBAR = "Feedback";
    private static final String FALHA_NA_COMUNICACAO = "Falha na comunicação";
    private final Feedback feedback = new Feedback();
    private TextInputLayout email;
    private TextInputLayout conteudo;
    private ContentLoadingProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle(TITULO_APPBAR);
        inicializaCampos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_feedback_enviar) {
            tentaEnviarFeedback();
        }
        return super.onOptionsItemSelected(item);
    }

    private void inicializaCampos() {
        email = findViewById(R.id.feedback_email);
        conteudo = findViewById(R.id.feedback_conteudo);
        progressBar = findViewById(R.id.feedback_progressbar);
    }

    private void tentaEnviarFeedback() {
        if (temConexao()) {
            enviaFeedback();
        } else {
            mostraErro(SEM_CONEXAO_COM_A_INTERNET);
        }
    }

    private boolean temConexao() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void enviaFeedback() {
        progressBar.show();
        limpaErros();
        preencheFeedback();
        Call<Feedback> call = new RetrofitInicializador().getFeedbackService().salva(feedback);
        call.enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(Call<Feedback> call, Response<Feedback> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        mostraErro(FEEDBACK_ENVIADO_COM_SUCESSO);
                        finish();
                    } else {
                        ResponseBody body = response.errorBody();
                        Gson gson = new Gson();
                        try {
                            Erro erro = gson.fromJson(body.string(), Erro.class);
                            apresentaErros(erro);
                        } catch (IOException e) {
                            e.printStackTrace();
                            mostraErro(OCORREU_UM_PROBLEMA);
                        }
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.hide();

                    }
                }, 5000);

            }

            @Override
            public void onFailure(Call<Feedback> call, Throwable t) {
                Log.e("failure", t.getMessage());
                mostraErro(FALHA_NA_COMUNICACAO);
                progressBar.hide();
            }
        });
    }

    private void limpaErros() {
        email.setErrorEnabled(false);
        email.setError(null);
        conteudo.setErrorEnabled(false);
        conteudo.setError(null);
    }

    private void mostraErro(String erro) {
        Toast.makeText(this, erro, Toast.LENGTH_SHORT).show();
    }

    private void apresentaErros(Erro erro) {
        Map<String, String> erros = erro.getErros();
        String email = erros.get(EMAIL);
        if (email != null) {
            this.email.setError(email);
        }
        String conteudo = erros.get(CONTEUDO);
        if (conteudo != null) {
            this.conteudo.setError(conteudo);
        }
    }

    private void preencheFeedback() {
        feedback.setEmail(email.getEditText().getText().toString());
        feedback.setConteudo(conteudo.getEditText().getText().toString());
    }

}