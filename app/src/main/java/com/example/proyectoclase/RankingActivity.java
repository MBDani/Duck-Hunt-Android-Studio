package com.example.proyectoclase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.proyectoclase.modelo.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RankingActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUser;
    private UserAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private TextView textViewTop, textViewPuesto, textViewNombre, textViewPuntuacion;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_activity);

        fab = findViewById(R.id.floatingActionButtonSalirRanking);

        textViewTop = findViewById(R.id.textViewTop);
        textViewPuesto = findViewById(R.id.textViewPuesto);
        textViewNombre = findViewById(R.id.textViewNombre);
        textViewPuntuacion = findViewById(R.id.textViewPuntuacion);

        recyclerViewUser = findViewById(R.id.recyclerUser);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(this));

        mFirestore = FirebaseFirestore.getInstance();

        //Hacemos una consulta en donde la marcamos que se muestren los 5 primeros de forma descendiente
        Query query = mFirestore.collection("users").orderBy("ducks", Query.Direction.DESCENDING)
                .limit(5);

        //Cargamos en el mAdapter y se ocurre cualquier cambio volvería a cargarse
        FirestoreRecyclerOptions<User> firestoreRecyclerOptions = new FirestoreRecyclerOptions.
                Builder<User>().setQuery(query, User.class).build();

        mAdapter = new UserAdapter(firestoreRecyclerOptions);
        mAdapter.notifyDataSetChanged();
        recyclerViewUser.setAdapter(mAdapter);





        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RankingActivity.this, LoginActivity.class));
            }
        });

        CambiarTipoDeFuente();

    }

    /**
     * Cambiamos el tipo de fuente
     */
    private void CambiarTipoDeFuente() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "pixel.ttf");
        textViewTop.setTypeface(typeface);
        textViewPuesto.setTypeface(typeface);
        textViewNombre.setTypeface(typeface);
        textViewPuntuacion.setTypeface(typeface);
    }

    /**
     * Nada mas entrar en el activity que nuestro adapter comience a escuchar
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }


    /**
     * Al cerrar la app que deje de escuchar nuestro adapter
     */
    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    /**
     * Inutilizamos el botón de ir hacia atrás
     */
    @Override
    public void onBackPressed() {

    }
}