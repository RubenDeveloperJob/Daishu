package net.vidalibarraquer.daishu.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.slider.Slider;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.vidalibarraquer.daishu.R;
import net.vidalibarraquer.daishu.objects.Level;
import net.vidalibarraquer.daishu.objects.Ranking;
import net.vidalibarraquer.daishu.recycler.RecyclerAdapterRanking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RankingActivity extends AppCompatActivity {

    //firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    //recycler view
    RecyclerAdapterRanking adapter;
    ArrayList<Ranking> array = new ArrayList<Ranking>();

    //ui
    RecyclerView recyler;
    BottomNavigationView bottom_navigation;

    Dialog dialog;
    Spinner nivelSpinner;
    FloatingActionButton btn_listado_examen_filtrado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        //ui
        recyler = findViewById(R.id.RecycleViewRanking);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        btn_listado_examen_filtrado = findViewById(R.id.btn_listado_examen_filtrado);
        bottom_navigation.setSelectedItemId(R.id.ranking_nav);


        //menu inferior
        BottomNavigationItemView item_menu = findViewById(R.id.profile_nav);

        item_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottom_navigation.getMenu().getItem(1).setChecked(false);
                bottom_navigation.getMenu().getItem(0).setChecked(true);

                startActivity(new Intent(getBaseContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });

        //boton de filtrar
        btn_listado_examen_filtrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        //obtenemos el ranking
        getRanking();
    }

    private void getRanking(){
        //query: tabla ranking, 25 resultados, ordenados por puntuacion de mayor a menor
        db.collection("ranking")
                .orderBy("puntuacion", Query.Direction.DESCENDING) // ordenar por puntuación de manera descendente
                .limit(25)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) { //si esta vacio
                            Log.d("Firestore", "onSuccess: LIST EMPTY");
                            return;
                        } else { //si no esta vacio
                            array.clear();

                            //por cada registro
                            for (QueryDocumentSnapshot document: documentSnapshots){

                                Ranking objRanking= new Ranking ();
                                objRanking.setId(document.getId());
                                objRanking.setPrueba(document.getString("prueba"));
                                objRanking.setTiempo(document.getLong("tiempo"));
                                objRanking.setPuntacion(document.getLong("puntuacion"));
                                objRanking.setUsuario(document.getDocumentReference("usuario"));
                                objRanking.setNivel_examen(document.getDocumentReference("nivel_examen"));
                                array.add(objRanking);

                            }

                            Log.d("Firestore", "onSuccess: " + array);

                            //cargamos el array
                            adapter = new RecyclerAdapterRanking(array);
                            recyler.setAdapter(adapter);
                            recyler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                        }
                    }});
    }

    private void showBottomSheetDialog() {
        // boton filtrar
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottomsheetdialog_filtrar_ranking, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        //vars
        List<Level> niveles = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();

        //ui
        TextView institutoEditText = view.findViewById(R.id.school_edt_filtrar);

        ImageButton closeButton = view.findViewById(R.id.close_btn_filtrar);
        Button aplicarButton = view.findViewById(R.id.aplicar_btn_filtrar);
        nivelSpinner = view.findViewById(R.id.nivel_spn_filtrar);
        Slider sliderSelectNumPrueba = view.findViewById(R.id.sliderSelectNumPrueba);


        //cargamos los niveles de los examenes
        db.collection("nivel_examen").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) { //si esta vacio
                            Log.d("Firestore", "onSuccess: LIST EMPTY");
                            return;
                        } else { //si no esta vacio
                            //guardamos los niveles
                            for (QueryDocumentSnapshot document: documentSnapshots){
                                niveles.add(new Level(document.getReference(), document.getString("nombre")));
                            }

                            //cargamos los niveles
                            ArrayAdapter<Level> adapter = new ArrayAdapter<Level>(getBaseContext(), R.layout.item_level, niveles);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            nivelSpinner.setAdapter(adapter);
                        }
                    }});

        //cargamos las instituciones

        db.collection("ranking")
                .orderBy("prueba")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) { //si esta vacio
                            Log.d("Firestore", "onSuccess: LIST EMPTY");
                            return;
                        } else { //si no esta vacio
                            //convertimos todos los resultados en objetos de tipo School
                            //List<String> pruebas = new ArrayList<String>();
                            Set<String> pruebas = new HashSet<>();
                            for (  DocumentSnapshot d :documentSnapshots ) {
                                pruebas.add(d.getString("prueba"));
                            }

                            //borramos el array y añadimos todas las instituciones
                            arrayList.addAll(pruebas);
                        }
                    }
                });

        //boton cerrar
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        //boton aplicar
        aplicarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numprueba = (int) sliderSelectNumPrueba.getValue(); // cojer num desde buscadorspinner
                updateFilters((Level) nivelSpinner.getSelectedItem(), institutoEditText.getText().toString(),numprueba); //lamamos a la funcion para actualizar los datos
                bottomSheetDialog.dismiss();
            }
        });

        //busqueda de institución
        institutoEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogo
                dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.dialog_searchable_spinner);

                //ui
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                //inicializa arrayadapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);

                //click sobre un elemento
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    //when iteam seleted from list
                    // set selected item on text  view
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        institutoEditText.setText(adapter.getItem(position).toString()); //ponemos el valor en el campo
                        dialog.dismiss(); //cerramos el dialogo
                    }
                });

                //cuando el campo de texto cambia
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //filtramos
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                //configuración del dialogo
                dialog.getWindow().setLayout(800, 1000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
            mBehavior.setPeekHeight(900);
        }

        //mostramos los filtros
        bottomSheetDialog.show();
    }
    // Mostrar ranking por filtro de curso
    private void updateFilters(Level nivel, String prueba, int numPrueva){
        //query
        Query q = db.collection("ranking");
        if (!(prueba == null || prueba.equals(""))) {
            q = q.whereEqualTo("prueba",prueba);
            System.out.println( numPrueva +  "  aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa  ");
        } else {
            q = q.whereEqualTo("nivel", nivel.getId());

            System.out.println(numPrueva +  "  aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa  ");

        }
        q.limit(numPrueva).orderBy("puntuacion", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) { //si esta vacio
                            Log.d("Firestore", "onSuccess: LIST EMPTY");
                            return;
                        } else { //si no esta vacio
                            //borramos el array
                            array.clear();

                            //por cada resultado
                            for (QueryDocumentSnapshot document: documentSnapshots){

                                Ranking objRanking= new Ranking ();
                                objRanking.setId(document.getId());
                                objRanking.setPrueba(document.getString("prueba"));
                                objRanking.setTiempo(document.getLong("tiempo"));
                                objRanking.setPuntacion(document.getLong("puntuacion"));
                                objRanking.setUsuario(document.getDocumentReference("usuario"));
                                objRanking.setNivel_examen(document.getDocumentReference("nivel_examen"));
                                array.add(objRanking);

                            }

                            Log.d("Firestore", "onSuccess: " + array);

                            //aplicamos el array
                            adapter = new RecyclerAdapterRanking(array);
                            recyler.setAdapter(adapter);
                            recyler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                        }
                    }});
    }
}