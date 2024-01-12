package net.vidalibarraquer.daishu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import net.vidalibarraquer.daishu.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PuntsFragment extends Fragment {
    //firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = MainActivity.mAuth;

    //vars
    ExamenActivity a = new ExamenActivity();
    long punts = 0;

    TextView tv_Punts, temp_2;
    ImageButton close;

    Button bt_inici;

    String temp;
    Long tempsMarc;
    int segons = 5400;

    public PuntsFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //No permite que el usuario pueda tirar hacia atras con los botones del movil, de esta manera una vez le ha dado al boton de acabar no puede volver atras.
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        tv_Punts = view.findViewById(R.id.tv_Punts);
        bt_inici = view.findViewById(R.id.bt_inici);
        temp_2 = view.findViewById(R.id.temp_2);
        close = view.findViewById(R.id.close_btn_view_points);

        Bundle datosRec = getArguments();
        temp = datosRec.getString("temp");
        //Temps en segons
        tempsMarc = datosRec.getLong("tiempoMarc");
        System.out.println(tempsMarc);

        temp_2.setText(temp);

        calcularPunts();
        savePoints();

        bt_inici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Cuando le de al boton volvera al fragment de seleccion de examenes
                Fragment newFragment = new SelectExamFragment();
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

    private void calcularPunts() {
        //Calcul de l'usuari la puntuació
        int cont = 0;
        for(int i =0; i< a.questions.size(); i++){
            if(a.questions.get(i).getAnswer() == a.questions.get(i).getAnswer_selected()){

                //Los puntos no son definitivos
                if(cont<10){
                    punts = punts + 1;
                }else if(cont<20){
                    punts = punts + 2;
                }else{
                    punts = punts + 3;
                }
            }
            cont++;
        }
        //Passar de milisegons a segons
        tempsMarc = tempsMarc/1000;
        Long tempsRes = segons - tempsMarc;

        if(tempsRes == 0){
            tempsRes = Long.valueOf(1);
        }
        punts = punts * tempsRes;
        String puntsF = Long.toString(punts);

        tv_Punts.setText(puntsF);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_punts, container, false);
    }

    private void savePoints(){
        //Definim l'usuari
        Map<String, Object> exam = new HashMap<>();

        //Afegim l'examen
        exam.put("fecha", new Date());
        exam.put("puntuacion", punts);
        exam.put("tiempo", tempsMarc);
        exam.put("nivel", db.document(a.level));

        //Afegim la prova a l'usuari
        db.collection("usuarios").document(mAuth.getUid()).collection("pruebas").document(a.exam).set(exam)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) { //si el usuario se ha añadido de forma correcta
                        Log.d("Firestore", "El examen se ha añadido a la base de datos.");
                    }
                });

        String id = db.collection("ranking").document().getId();
        DocumentReference usuario = db.collection("usuarios").document(mAuth.getUid());

        //Definim l'usuari
        Map<String, Object> ranking = new HashMap<>();

        //Afegim l'examen
        ranking.put("fecha", new Date());
        ranking.put("puntuacion", punts);
        ranking.put("usuario", usuario);
        ranking.put("prueba", a.exam);
        ranking.put("tiempo", 0);
        ranking.put("nivel", db.document(a.level));

        //Afegim la prova al ranking
        db.collection("ranking").document(id).set(ranking)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) { //si el usuario se ha añadido de forma correcta
                        Log.d("Firestore", "El ranking se ha añadido a la base de datos.");
                    }
                });
    }
}
