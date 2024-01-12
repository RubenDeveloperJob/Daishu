package net.vidalibarraquer.daishu.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;

import net.vidalibarraquer.daishu.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PreguntaExamFragment extends Fragment {

    

     long temp = 0;

     TextView tv_title, tv_pregunta, tv_resposta, tv_temp;
     ImageView iv_pregunta, iv_resposta;
     RadioGroup radioGroup;
     RadioButton rb1, rb2, rb3, rb4, rb5;
     Button btn_next, btn_ant;

    ExamenActivity a = new ExamenActivity();


     int cont = 0;

    public PreguntaExamFragment(){
        //B12016(30), B22017(30), E222017(30), P62022(24), P62021(24), P52021(24), N62012(30), N22017(31), P62020(24) NO IMPRIME BIEN LAS PREGUNTAS.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ui
        tv_title=view.findViewById(R.id.tv_title);
        tv_pregunta=view.findViewById(R.id.tv_pregunta);
        tv_resposta=view.findViewById(R.id.tv_resposta);
        iv_pregunta=view.findViewById(R.id.iv_pregunta);
        iv_resposta=view.findViewById(R.id.iv_resposta);
        radioGroup=view.findViewById(R.id.radioGroup);
        btn_ant=view.findViewById(R.id.btn_ant);
        tv_temp=view.findViewById(R.id.tv_temp);
        rb1=view.findViewById(R.id.btn_res1);
        rb2=view.findViewById(R.id.btn_res2);
        rb3=view.findViewById(R.id.btn_res3);
        rb4=view.findViewById(R.id.btn_res4);
        rb5=view.findViewById(R.id.btn_res5);


        // Temporitzador
        new CountDownTimer(5400000, 1000) {

            public void onTick(long millisUntilFinished) {

                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;

                tv_temp.setText(String.valueOf(hour + ":" + min + ":" + sec));
                temp = millisUntilFinished;

            }

            public void onFinish() {
                // Quan acabi de contar el temps
                fragmentCanv();

            }
        }.start();



        //No permet que l'usuari pugui tirar cap enrere amb els botons del movil.
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {}
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        btn_next=view.findViewById(R.id.btn_next);

        btn_ant.setEnabled(false);
        aplicarPregunta(a);

        //Metode per fer zoom a l'imatge de resposta
        Zoomy.Builder builder = new Zoomy.Builder(this.getActivity())
                .target(iv_resposta)
                .animateZooming(false)
                .enableImmersiveMode(false)
                .tapListener(new TapListener() {
                    @Override
                    public void onTap(View v) {
                    }
                });
        builder.register();

        //Metode per fer zoom a l'imatge de pregunta
        Zoomy.Builder builder2 = new Zoomy.Builder(this.getActivity())
                .target(iv_pregunta)
                .animateZooming(false)
                .enableImmersiveMode(false)
                .tapListener(new TapListener() {
                    @Override
                    public void onTap(View v) {
                    }
                });
        builder2.register();


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ant.setEnabled(true);
                guardarResposta(a,v);
                //Condicional per passar a la pantalla dels punts quan l'usuari acabi l'examen.

                if(cont == a.questions.size()-1){
                    fragmentCanv();
                }else {
                    cont++;
                    radioGroup.clearCheck();
                    aplicarPregunta(a);
                }
            }
        });

        btn_ant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cont--;
                if(cont==0){
                   btn_ant.setEnabled(false);
                }
                    aplicarPregunta(a);
            }

        });

    }

    private void fragmentCanv() {
        Bundle datos = new Bundle();
        datos.putString("temp",tv_temp.getText().toString());
        datos.putLong("tiempoMarc", temp);
        Fragment newFragment = new PuntsFragment();
        newFragment.setArguments(datos);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout_examen, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void radioButtonCheck() {
        if(a.questions.get(cont).getAnswer_selected() == 1){
            rb1.setChecked(true);
        }else if(a.questions.get(cont).getAnswer_selected() == 2){
            rb2.setChecked(true);
        }else if(a.questions.get(cont).getAnswer_selected() == 3){
            rb3.setChecked(true);
        }else if(a.questions.get(cont).getAnswer_selected() == 4){
            rb4.setChecked(true);
        }else if(a.questions.get(cont).getAnswer_selected() == 5){
            rb5.setChecked(true);
        }else if(a.questions.get(cont).getAnswer_selected()== 0){
            rb1.setChecked(false);
            rb2.setChecked(false);
            rb3.setChecked(false);
            rb4.setChecked(false);
            rb5.setChecked(false);
        }
    }

    private void guardarResposta(ExamenActivity a, View v) {

        //Guardem la resposta que tenim seleccionada al radioButton en l'array questions

        if(rb1.isChecked()){
            a.questions.get(cont).setAnswer_selected(1);
        }else if(rb2.isChecked()){
            a.questions.get(cont).setAnswer_selected(2);
        }else if(rb3.isChecked()){
            a.questions.get(cont).setAnswer_selected(3);
        }else if(rb4.isChecked()){
            a.questions.get(cont).setAnswer_selected(4);
        }else if(rb5.isChecked()){
            a.questions.get(cont).setAnswer_selected(5);
        }else{
            a.questions.get(cont).setAnswer_selected(0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_questions, container, false);
    }

    private void aplicarPregunta(ExamenActivity a) {

        //Apliquem l'informació que tenim a l'arraylist a cada view.

        tv_title.setText(a.questions.get(cont).getTitle());
        tv_pregunta.setText(a.questions.get(cont).getExercise());

        if(!a.questions.get(cont).getExercise_image().equals("null")){
            iv_pregunta.setVisibility(View.VISIBLE);

            Glide.with(requireContext()).load(a.questions.get(cont).getExercise_image()).placeholder(android.R.drawable.progress_indeterminate_horizontal).error(android.R.drawable.stat_notify_error).into(iv_pregunta);
        }else{
            iv_pregunta.setVisibility(View.GONE);
        }

        if(!a.questions.get(cont).getOptions().equals("null") && a.questions.get(cont).getAnswer_image().equals("null") ){
            tv_resposta.setVisibility(View.VISIBLE);
            iv_resposta.setVisibility(View.GONE);

            tv_resposta.setText(a.questions.get(cont).getOptions());
        }else if (a.questions.get(cont).getOptions().equals("null") && !a.questions.get(cont).getAnswer_image().equals("null")){
            tv_resposta.setVisibility(View.GONE);
            iv_resposta.setVisibility(View.VISIBLE);

            Glide.with(requireContext()).load(a.questions.get(cont).getAnswer_image()).placeholder(android.R.drawable.progress_indeterminate_horizontal).error(android.R.drawable.stat_notify_error).into(iv_resposta);
        } else if (!a.questions.get(cont).getOptions().equals("null") && !a.questions.get(cont).getAnswer_image().equals("null")) {
            tv_resposta.setVisibility(View.VISIBLE);
            iv_resposta.setVisibility(View.VISIBLE);

            tv_resposta.setText(a.questions.get(cont).getOptions());
            Glide.with(requireContext()).load(a.questions.get(cont).getAnswer_image()).placeholder(android.R.drawable.progress_indeterminate_horizontal).error(android.R.drawable.stat_notify_error).into(iv_resposta);
        }else {
            iv_resposta.setVisibility(View.GONE);
            tv_resposta.setVisibility(View.GONE);
        }
        //Fem un check per saber si la pregunta té ya una resposta seleccionade o no.
        radioButtonCheck();

    }
}