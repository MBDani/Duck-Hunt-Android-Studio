package com.example.proyectoclase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoclase.modelo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistroActivity extends AppCompatActivity {

    private TextView edt_nombre, edt_contraseña, edt_email;
    private Button bt_registro, bt_inicio;
    private String nombre, contraseña, email;

    //Sonido
    private MediaPlayer mp_inicio, mp_boton;
    public boolean quiere_musica = true, quiere_audio = true;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);

        edt_nombre = findViewById(R.id.editTextNick);
        edt_contraseña = findViewById(R.id.editTextContraseña);
        edt_email = findViewById(R.id.editTextEmail);


        bt_registro = findViewById(R.id.buttonRegistrarse);
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
        edt_nombre.setTypeface(typeface);
        edt_contraseña.setTypeface(typeface);
        edt_email.setTypeface(typeface);
        bt_registro.setTypeface(typeface);
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

        bt_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (quiere_audio==true){
                    mp_boton.start();
                }

                nombre = edt_nombre.getText().toString();
                contraseña = edt_contraseña.getText().toString();
                email = edt_email.getText().toString();


                if (nombre.isEmpty() || email.isEmpty() || contraseña.isEmpty()) {
                    Toast.makeText(RegistroActivity.this, "Debes de rellenar todos los campos", Toast.LENGTH_SHORT).show();
                } else if (contraseña.length() < 6) {
                    edt_contraseña.setError("Mínimo de 6 caracteres");
                } else {
                    Registro();
                }
            }
        });

        bt_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (quiere_audio==true){
                    mp_boton.start();
                }

                Intent i = new Intent(RegistroActivity.this, LoginActivity.class);
                i.putExtra("quiere_musica", quiere_musica);
                i.putExtra("quiere_audio", quiere_audio);
                startActivity(i);
                finish();
            }
        });

    }


    /**
     * Registramos al usuario en la autentificación de firebase
     */
    private void Registro() {
        mAuth.createUserWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(RegistroActivity.this, "Se ha registro la cuenta: " + email, Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            CrearUsuario(user);

                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                //si coinciden usuarios
                                Toast.makeText(RegistroActivity.this, "El email ya está en uso", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegistroActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();

                            }
                        }


                    }
                });
    }


    /**
     * Guardamos los datos del usuario en la nube de firestore
     * @param user
     */
    private void CrearUsuario(FirebaseUser user) {
        User nuevoUsuario = new User(nombre, 0);

        db.collection("users").document(user.getUid()).set(nuevoUsuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RegistroActivity.this, "Datos almacenados en la nube", Toast.LENGTH_SHORT).show();


                Intent i = new Intent(RegistroActivity.this, LoginActivity.class);
                i.putExtra("quiere_musica", quiere_musica);
                i.putExtra("quiere_audio", quiere_audio);
                startActivity(i);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistroActivity.this, "No se han podido guardar sus datos", Toast.LENGTH_SHORT).show();
            }
        });
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