package com.example.proyectoclase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoclase.modelo.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class UserAdapter extends FirestoreRecyclerAdapter<User, UserAdapter.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    /**
     * Para sustituir el texto en nuestro datos_ranking
     * @param holder
     * @param i
     * @param user
     */
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull User user) {
        holder.textViewNombre.setText(user.getNick());
        holder.textViewPuntos.setText(String.valueOf(user.getDucks()));

        //Para añadir el número del puesto
        int pos = i+1;
        holder.textViewPuesto.setText(pos + "º");
    }

    /**
     * Indicamos la vista a la que nos dirigimos y relacionamos
     * @param viewGroup
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.datos_ranking, viewGroup, false);

        return new ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewNombre;
        TextView textViewPuntos;
        TextView textViewPuesto;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewPuntos = itemView.findViewById(R.id.textViewPuntos);
            textViewPuesto = itemView.findViewById(R.id.textViewPuesto);

        }
    }
}
