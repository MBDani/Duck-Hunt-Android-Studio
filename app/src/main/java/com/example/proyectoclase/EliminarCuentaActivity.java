package com.example.proyectoclase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EliminarCuentaActivity extends AppCompatActivity {

    private TextView edt_contraseña, edt_email;
    private Button bt_eliminarCuenta, bt_inicio;
    private String contraseña, email;

    //Sonido
    private MediaPlayer mp_inicio, mp_boton;
    public boolean quiere_musica = true, quiere_audio = true;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eliminar_cuenta_activity);

        edt_contraseña = findViewById(R.id.editTextContraseña);
        edt_email = findViewById(R.id.editTextEmail);


        bt_eliminarCuenta = findViewById(R.id.buttonEliminarCuenta);
        bt_inicio = findViewById(R.id.buttonVolverInicio);


        // Instanciar la conexión a Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        CambiarTipoFuente();
        InicializarSonido();
        Eventos();
    }

    /**
     * Cambiamos el tipo de fuente
     */
    private void CambiarTipoFuente() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "pixel.ttf");
        edt_contraseña.setTypeface(typeface);
        edt_email.setTypeface(typeface);
        bt_eliminarCuenta.setTypeface(typeface);
        bt_inicio.setTypeface(typeface);
    }

    /**
     * Inicializamos el multimedia
     */
    private void InicializarSonido() {
        //Recibimos los datos de la clase login
        Bundle datos = this.getIntent().getExtras();
        quiere_audio = datos.getBoolean("quiere_audio");
        quiere_musica = datos.getBoolean("quiere_musica");

        if (quiere_musica == true){
            mp_inicio = MediaPlayer.create(this, R.raw.inicio);
            mp_inicio.start();
        }

        if (quiere_audio == true){
            mp_boton = MediaPlayer.create(this, R.raw.boton);
        }
    }

    /**
     * Eventos de los botones
     */
    private void Eventos() {
        bt_eliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (quiere_audio==true){
                    mp_boton.start();
                }

                contraseña = edt_contraseña.getText().toString();
                email = edt_email.getText().toString();


                if (email.isEmpty() || contraseña.isEmpty()) {
                    Toast.makeText(EliminarCuentaActivity.this, "Debes de rellenar todos los campos", Toast.LENGTH_SHORT).show();
                } else if (contraseña.length() < 6) {
                    edt_contraseña.setError("Mínimo de 6 caracteres");
                } else {
                    IdentificacionUsuario();
                }
            }
        });


        bt_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (quiere_audio==true){
                    mp_boton.start();
                }

                Intent i = new Intent(getApplication(), LoginActivity.class);
                i.putExtra("quiere_musica", quiere_musica);
                i.putExtra("quiere_audio", quiere_audio);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Comprobamos la autentificación del usuario y si es correcta llamamos al método BorrarCuentaUsuario
     */
    private void IdentificacionUsuario() {
        mAuth.signInWithEmailAndPassword(email, contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EliminarCuentaActivity.this, "Sesión iniciada", Toast.LENGTH_SHORT).show();

                    firebaseUser = mAuth.getCurrentUser(); //obtenemos el usuario actual
                    id = firebaseUser.getUid(); //Obtenemos el id del usuario actual


                    firebaseUser.delete(); //con esto borramos email y contraseña
                    Toast.makeText(EliminarCuentaActivity.this, "Datos de la cuenta borrados", Toast.LENGTH_SHORT).show();


                    BorrarCuentaUsuario(id);


                } else {
                    Toast.makeText(EliminarCuentaActivity.this, "El email o contraseña no es correcto", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * Borramos la cuenta del usuario
     * @param id
     */
    private void BorrarCuentaUsuario(String id) {
        db.collection("users").document(id).delete();

        Toast.makeText(EliminarCuentaActivity.this, "Datos del usuario elminados", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(getApplication(), LoginActivity.class);
        i.putExtra("quiere_musica", quiere_musica);
        i.putExtra("quiere_audio", quiere_audio);
        startActivity(i);
        finish();


    }

    /**
     * Al parar el juego paramos la música
     */
    @Override
    public void onStop() {
        super.onStop();
        if (quiere_musica == true) {

            mp_inicio.pause();
        }
    }

    /**
     * Al reaunudar el juego reaunudamos la música
     */
    @Override
    public void onResume() {
        super.onResume();
        if (quiere_musica == true) {
            mp_inicio.start();
        }
    }

}
