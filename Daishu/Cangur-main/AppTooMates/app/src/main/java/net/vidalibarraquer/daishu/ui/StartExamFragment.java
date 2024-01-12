package net.vidalibarraquer.daishu.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import net.vidalibarraquer.daishu.R;

import java.util.Timer;
import java.util.TimerTask;


public class StartExamFragment extends Fragment {

    private TextView tvNivel, tvYear , timerText;

    private  Button btnStartExam;

   private Timer timer;

   private TimerTask  timerTask;

    ImageButton close;

   private double time = 0.0;

    public StartExamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timer = new Timer();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timer = new Timer();

        btnStartExam = view.findViewById(R.id.continue_select_exam);

        close = view.findViewById(R.id.close_btn_start_exam);

        btnStartExam.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ExamenActivity a = new ExamenActivity(); // declarar activity donde queremos cojer el metodo
                a.cuentaatras();
                btnStartExam.setEnabled(false);

                Fragment newFragment = new PreguntaExamFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.layout_examen, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start_exam, container, false);
    }
}









