package net.vidalibarraquer.daishu.ui;

import static com.google.android.gms.common.util.CollectionUtils.mapOf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import net.vidalibarraquer.daishu.R;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity implements View.OnClickListener {
    //firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = MainActivity.mAuth;

    //ui
    ImageButton englishButton, spanishButton, catalanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        //ui
        englishButton = findViewById(R.id.english_btn_languages);
        spanishButton = findViewById(R.id.spanish_btn_languages);
        catalanButton = findViewById(R.id.catalan_btn_languages);

        englishButton.setOnClickListener(this);
        spanishButton.setOnClickListener(this);
        catalanButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //variables
        Locale locale;

        switch (v.getId()) {
            case R.id.spanish_btn_languages:
                db.collection("usuarios").document(mAuth.getUid()).update("idioma", "es");
                locale = new Locale("es");
                break;

            case R.id.catalan_btn_languages:
                db.collection("usuarios").document(mAuth.getUid()).update("idioma", "ca");
                locale = new Locale("ca");
                break;

            default:
                db.collection("usuarios").document(mAuth.getUid()).update("idioma", "en");
                locale = new Locale("en");
                break;
        }

        //Establim l'idioma de preferencia de l'usuari
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.setLocale(locale);

        Intent i = new Intent(this, SchoolsActivity.class);
        startActivity(i);
        finish();
    }
}