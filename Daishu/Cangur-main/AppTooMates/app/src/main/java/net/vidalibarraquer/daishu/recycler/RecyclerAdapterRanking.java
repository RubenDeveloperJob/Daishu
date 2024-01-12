package net.vidalibarraquer.daishu.recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.vidalibarraquer.daishu.R;
import net.vidalibarraquer.daishu.objects.Ranking;
import net.vidalibarraquer.daishu.ui.MainActivity;

import java.util.List;

public class RecyclerAdapterRanking extends RecyclerView.Adapter<RecyclerAdapterRanking.ViewHolder>{

    //atributs
    private List<Ranking> mVehicles;

    //firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    GoogleSignInAccount acct = MainActivity.acct;
    private Ranking vehicle;

    public class ViewHolder extends RecyclerView.ViewHolder {

        //elements grafics
        public TextView name_txt_ranking ,prueba_txt_ranking,puntuacion_txt_ranking ;
        public ImageView profile_img_ranking;

        public ViewHolder(View itemView) {
            super(itemView);

            //ui elements
            name_txt_ranking = (TextView) itemView.findViewById(R.id.name_txt_ranking);
            puntuacion_txt_ranking = (TextView) itemView.findViewById(R.id.puntuacion_txt_ranking);
            profile_img_ranking = (ImageView) itemView.findViewById(R.id.profile_img_ranking);
        }
    }

    public RecyclerAdapterRanking(List<Ranking> vehicles) {
        mVehicles = vehicles;
    }

    @Override
    public RecyclerAdapterRanking.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_ranking, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //vars
        vehicle = mVehicles.get(position);

        TextView name_txt_ranking = holder.name_txt_ranking;
        TextView puntuacion_txt_ranking = holder.puntuacion_txt_ranking;
        ImageView profile_img_ranking = holder.profile_img_ranking;

        puntuacion_txt_ranking.setText(String.valueOf(vehicle.getPuntacion()));
        DocumentReference i = vehicle.getUsuario();

        db.collection("usuarios")
                .orderBy("puntuacion")
                .get()
                .addOnSuccessListener(task ->{
                    i.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                name_txt_ranking.setText(task.getResult().getString("nombre"));
                                Glide.with(holder.profile_img_ranking.getContext())
                                        .load(task.getResult().getString("imagen"))
                                        .into(profile_img_ranking);
                            }
                        }
                    });

                });





    }

    // retorna el nombre total d'elements
    @Override
    public int getItemCount() {
        return mVehicles.size();
    }
}
