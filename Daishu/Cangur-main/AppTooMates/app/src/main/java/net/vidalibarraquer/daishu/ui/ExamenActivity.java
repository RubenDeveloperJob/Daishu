package net.vidalibarraquer.daishu.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;

import android.os.CountDownTimer;

import android.view.View;

import net.vidalibarraquer.daishu.R;
import net.vidalibarraquer.daishu.objects.Question;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExamenActivity extends AppCompatActivity implements View.OnClickListener {
    //var
    public static String exam, level;
    public static ArrayList<Question> questions = new ArrayList<Question>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_examen);



        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_examen, new SelectExamFragment())
                .commit();

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static void cuentaatras() {

        //Funció per ordenar les preguntes de array
        Collections.sort(questions, new Comparator<Question>() {
            @Override
            public int compare(Question o1, Question o2) {
                return o1.getFile().compareTo(o2.getFile());
            }
        }
        );

        new CountDownTimer(5400000, 1000) {

            public void onTick(long millisUntilFinished) {

                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
            }

            public void onFinish() {
                // Quan acabi de contar el temps
            }
        }.start();
    }

    @Override
    public void onClick(View v) {

    }

}