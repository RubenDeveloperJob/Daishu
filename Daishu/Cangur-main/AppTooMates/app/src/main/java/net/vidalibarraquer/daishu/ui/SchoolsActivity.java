package net.vidalibarraquer.daishu.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import net.vidalibarraquer.daishu.R;
import net.vidalibarraquer.daishu.objects.School;
import net.vidalibarraquer.daishu.recycler.RecyclerAdapterSchool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class SchoolsActivity extends AppCompatActivity implements View.OnClickListener {
    //firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    //recycler view
    RecyclerAdapterSchool adapter;
    ArrayList<School> array = new ArrayList<School>();

    //ui
    RecyclerView recyler;
    ImageButton searchButton, closeButton;
    EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schools);

        //ui
        recyler = findViewById(R.id.recycler_schools);
        searchButton = findViewById(R.id.search_btn_schools);
        searchEditText = findViewById(R.id.text_search_edt_schools);
        closeButton = findViewById(R.id.close_btn_schools);

        searchButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);

        getSchools();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_btn_schools:
                if (searchEditText.getText().toString().matches("")){
                    adapter = new RecyclerAdapterSchool(array);
                } else {
                    ArrayList<School> array_copy = new ArrayList<School>();
                    array_copy = (ArrayList<School>) array.clone();

                    Iterator<School> iter = array_copy.iterator();

                    while (iter.hasNext()){
                        if (! iter.next().getValor().toLowerCase(Locale.ROOT).contains(searchEditText.getText().toString().toLowerCase(Locale.ROOT))){
                            iter.remove();
                        }
                    }

                    adapter = new RecyclerAdapterSchool(array_copy);
                }
                recyler.setAdapter(adapter);
                recyler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                break;

            case R.id.close_btn_schools:
                Intent i = new Intent(this, ProfileActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    private void getSchools(){
        //Carreguem les institucions
        db.collection("institucion").whereEqualTo("visibilidad", true).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) { //si esta vacio
                            Log.d("Firestore", "onSuccess: LIST EMPTY");
                            return;
                        } else { //si no esta vacio
                            //borrem l'array i afegim totes les institucions
                            array.clear();

                            for(DocumentSnapshot d : documentSnapshots){
                                array.add(new School(Integer.valueOf(d.getId()), d.getString("valor"), d.getBoolean("visibilidad")));
                            }

                            Log.d("Firestore", "onSuccess: " + array);

                            //passem l'array al RecyclerView
                            adapter = new RecyclerAdapterSchool(array);
                            recyler.setAdapter(adapter);
                            recyler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        }
                    }});
    }
}