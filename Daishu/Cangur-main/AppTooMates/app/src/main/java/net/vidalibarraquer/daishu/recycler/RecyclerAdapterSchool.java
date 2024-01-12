package net.vidalibarraquer.daishu.recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import net.vidalibarraquer.daishu.R;
import net.vidalibarraquer.daishu.objects.School;
import net.vidalibarraquer.daishu.ui.MainActivity;
import net.vidalibarraquer.daishu.ui.ProfileActivity;

import java.util.List;

public class RecyclerAdapterSchool extends RecyclerView.Adapter<RecyclerAdapterSchool.ViewHolder>{

    //atributs
    private List<School> mVehicles;

    //firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = MainActivity.mAuth;

    public class ViewHolder extends RecyclerView.ViewHolder {

        //elements grafics
        public TextView nameText;

        public ViewHolder(View itemView) {
            super(itemView);

            //ui elements
            nameText = (TextView) itemView.findViewById(R.id.name_txt_item);
        }
    }

    public RecyclerAdapterSchool(List<School> vehicles) {
        mVehicles = vehicles;
    }

    @Override
    public RecyclerAdapterSchool.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_school, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterSchool.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //vars
        School vehicle = mVehicles.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference institucion =  db.collection("instiucion").document(String.valueOf(vehicle.getId()));
                db.collection("usuarios").document(mAuth.getUid()).update("institucion", institucion);

                Intent i = new Intent(holder.itemView.getContext(), ProfileActivity.class);
                holder.itemView.getContext().startActivity(i);
            }
        });

        //ui elements
        TextView textView2 = holder.nameText;

        textView2.setText(vehicle.getValor());
    }

    // retorna el nombre total d'elements
    @Override
    public int getItemCount() {
        return mVehicles.size();
    }
}
