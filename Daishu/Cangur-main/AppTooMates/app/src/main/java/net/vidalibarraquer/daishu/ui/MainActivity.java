package net.vidalibarraquer.daishu.ui;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import net.vidalibarraquer.daishu.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static GoogleSignInAccount acct;
    //firebase
    public static FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //ui
    SignInButton signInButton;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        //ui
        signInButton = findViewById(R.id.bt_sign_in);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    signInButton.setVisibility(View.GONE);
                    updateUI(mAuth.getCurrentUser());
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //si el codigo es 2
        if (resultCode == Activity.RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class); //Obtenim el compte

                if (account != null) { //Si el compte no està buit
                    firebaseAuthWithGoogle(account);
                } else { //Si el compte està buit
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }

            } catch (ApiException e) { //Possible error
                System.out.println(e);
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        /*
        ToDo: Metodo para utenticarse en Firebase usando Google
        */

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signInButton.setVisibility(View.GONE); //ocultamos el botón
                            FirebaseUser user = mAuth.getCurrentUser(); //obtenemos el usuario

                            updateUI(user); //Llancem el nou activity
                        } else {
                            Toast.makeText(MainActivity.this, "You are not Authenticated", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        /*
        ToDo: Metodo para iniciar un nuevo Activity. Además la variable acct permite obtener los datos del usuario
        */

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel("General", "General", IMPORTANCE_DEFAULT));

        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            //Subscribim l'usuari al canal General
            FirebaseMessaging.getInstance().subscribeToTopic("General")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Log.d("[FirebaseMessaging]", "Could not subscribe the user.");
                            } else {
                                Log.d("[FirebaseMessaging]", "The user is now subscribed.");
                            }
                        }
                    });

            //Comprovem si hi ha algun usuari amb aquest id
            db.collection("usuarios").document(String.valueOf(mAuth.getCurrentUser().getUid())).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) { //si la tarea se ha completado
                                if (task.getResult().exists()) { //si el usuario existe
                                    Log.d("Firestore", "El usuario existe");

                                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                                        if (!TextUtils.isEmpty(token)) {
                                            db.collection("usuarios").document(String.valueOf(mAuth.getCurrentUser().getUid())).update("token_mensajes", token);
                                        }
                                    });

                                    //Actualitzem el token i l'última connexió
                                    db.collection("usuarios").document(String.valueOf(mAuth.getCurrentUser().getUid())).update("ultima_conexion", new Date());

                                    //Actualitzem el nom
                                    db.collection("usuarios").document(String.valueOf(mAuth.getCurrentUser().getUid())).update("nombre", String.valueOf(acct.getDisplayName()));

                                    //Establim l'idioma de preferencia de l'usuari
                                    Locale locale = new Locale(task.getResult().getString("idioma"));
                                    Locale.setDefault(locale);

                                    Configuration config = getBaseContext().getResources().getConfiguration();
                                    config.locale = locale;

                                    //Iniciem l'activity següent
                                    Intent i = new Intent(getBaseContext(), ProfileActivity.class);
                                    startActivity(i);
                                    finish();

                                } else { //Si l'usuari no existeix
                                    Log.d("Firestore", "El usuario no existe");

                                    //Definim l'usuari
                                    Map<String, Object> user = new HashMap<>();

                                    //Actualitzem el token i l'última connexió
                                    user.put("ultima_conexion", new Date());
                                    user.put("nombre", String.valueOf(acct.getDisplayName()));
                                    user.put("imagen", String.valueOf(acct.getPhotoUrl()));
                                    user.put("idioma", "en");

                                    //Afegim l'usuari
                                    db.collection("usuarios").document(String.valueOf(mAuth.getCurrentUser().getUid())).set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) { //si el usuario se ha añadido de forma correcta
                                                    Log.d("Firestore", "Usuario añadido correctamente");

                                                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                                                        if (!TextUtils.isEmpty(token)) {
                                                            db.collection("usuarios").document(String.valueOf(mAuth.getCurrentUser().getUid())).update("token_mensajes", token);
                                                        }
                                                    });
                                                }
                                            });

                                    //Iniciem l'activiy següent
                                    Intent i = new Intent(getBaseContext(), LanguageActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            } else { //Si la tasca no s'ha completat
                                Log.d("Firestore", "Error en la tarea", task.getException());
                            }
                        }
                    });
        }
    }
}