package net.vidalibarraquer.daishu.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.vidalibarraquer.daishu.R;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    //firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = MainActivity.mAuth;

    //ui
    FloatingActionButton startExamen;
    ImageView image;
    TextView name, exams, median;
    Button logout;
    BottomNavigationView bottom_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //ui
        startExamen = findViewById(R.id.start_exam_profile);
        image = findViewById(R.id.image_profile);
        name = findViewById(R.id.name_profile);
        exams = findViewById(R.id.number_exams_profile);
        median = findViewById(R.id.media_exams_profile);
        logout = findViewById(R.id.logout_btn_profile);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        startExamen.setOnClickListener(this);
        logout.setOnClickListener(this);

        BottomNavigationItemView item_menu = findViewById(R.id.ranking_nav);

        item_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottom_navigation.getMenu().getItem(0).setChecked(false);
                bottom_navigation.getMenu().getItem(1).setChecked(true);

                startActivity(new Intent(getBaseContext(), RankingActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });

        afterStart();

    }

    @Override
    public void onClick(View v) {
        //vars
        Intent i;

        switch (v.getId()){
            case R.id.start_exam_profile:
                i = new Intent(this, ExamenActivity.class);
                startActivity(i);
                finish();
                break;

            case R.id.logout_btn_profile:
                mAuth.signOut();

                i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
        }

    }

    private void afterStart(){
        //comprovem si hi ha un usuari amb aquesta id
        db.collection("usuarios").document(String.valueOf(mAuth.getUid())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) { //si la tarea se ha completado
                            if (task.getResult().exists()) { //si el usuario existe
                                name.setText(mAuth.getCurrentUser().getDisplayName());
                                Glide.with(getBaseContext()).load(task.getResult().getString("imagen")).into(image);
                            }
                        } else { //si la tasca no s'ha completat
                            Log.d("Firestore", "Error en la tarea", task.getException());
                        }
                    }
                });

        // Get a reference to the parent collection
        CollectionReference parentCollectionRef = db.collection("usuarios");

        // Get a reference to the child collection
        CollectionReference childCollectionRef = parentCollectionRef.document(String.valueOf(mAuth.getUid())).collection("pruebas");

        // Get a QuerySnapshot of the child collection
        childCollectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int numElements = task.getResult().size(); // Get the number of elements
                exams.setText(String.valueOf(numElements));

                long notas = 0;
                for (DocumentSnapshot d : task.getResult().getDocuments()){
                    addButton(String.valueOf(d.getLong("puntuacion")), d.getId());
                    notas = notas + d.getLong("puntuacion");
                }

                if (numElements != 0){
                    median.setText(String.valueOf(notas / numElements));
                }
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });
    }

    //add a new button
    private void addButton(String puntuacion, String examen) {
        //get the grid
        GridLayout grid = (GridLayout) findViewById(R.id.grid_profile);

        LinearLayout layout = new LinearLayout(this);
        layout.setBackground(ContextCompat.getDrawable(this, R.drawable.item_profile));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        TextView p = new TextView(this);
        p.setText(puntuacion);
        p.setTextSize(25);
        p.setGravity(Gravity.CENTER);

        Space s = new Space(this);
        s.setMinimumHeight(10);

        TextView e = new TextView(this);
        e.setText(examen);
        e.setGravity(Gravity.CENTER);

        layout.addView(p);
        layout.addView(s);
        layout.addView(e);

        grid.addView(layout);
    }
}