package com.example.proyectoclase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText edt_email, edt_contraseña;
    private Button btnStart, btnRegistro, btnEliminar;
    private String email, contraseña;

    private ImageView bt_audio_si, bt_musica_si;


    //Sonido
    private MediaPlayer mp_inicio, mp_boton;
    public boolean quiere_musica = true, quiere_audio = true;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //SharedPreferences
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        edt_email = findViewById(R.id.editTextEmail);
        edt_contraseña = findViewById(R.id.editTextContraseña);

        btnStart = findViewById(R.id.buttonStart);
        btnRegistro = findViewById(R.id.buttonRegistrarse);
        btnEliminar = findViewById(R.id.buttonEliminar);


        bt_audio_si = findViewById(R.id.imageButtonAudioSi);
        bt_musica_si = findViewById(R.id.imageButtonMusicaSi);


        //SharedPreferences
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);

        // Instanciar la conexión a Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        CambiarTipoDeFuente();
        IncializarSonido();
        Eventos();
        RellenarCampos();
    }


    /**
     * Cambiamos el tipo de fuente
     */
    private void CambiarTipoDeFuente() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "pixel.ttf");
        edt_email.setTypeface(typeface);
        edt_contraseña.setTypeface(typeface);
        btnStart.setTypeface(typeface);
        btnRegistro.setTypeface(typeface);
        btnEliminar.setTypeface(typeface);
    }

    /**
     * Inicializamos el multimedia
     */
    private void IncializarSonido() {
        //MediaPlayer
        mp_inicio = MediaPlayer.create(this, R.raw.inicio);
        mp_boton = MediaPlayer.create(this, R.raw.boton);

        //Recogemos datos que nos puedan llegar de otros activitys
        Bundle datos = this.getIntent().getExtras();

        if (datos != null) {
            quiere_audio = datos.getBoolean("quiere_audio");
            quiere_musica = datos.getBoolean("quiere_musica");
        }


        if (quiere_musica == false) {
            bt_musica_si.setImageResource(R.drawable.musica_no);
        } else if (quiere_musica == true) {
            bt_musica_si.setImageResource(R.drawable.musica_si);
            mp_inicio.start();

        }


    }

    /**
     * Eventos de los botones
     */
    private void Eventos() {

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (quiere_audio == true) {
                    mp_boton.start();
                }

                email = edt_email.getText().toString();
                contraseña = edt_contraseña.getText().toString();


                if (!email.isEmpty() && !contraseña.isEmpty()) {
                    LoginUsuario();

                } else {
                    Toast.makeText(LoginActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quiere_audio == true) {
                    mp_boton.start();
                }
                Intent i = new Intent(LoginActivity.this, RegistroActivity.class);
                i.putExtra("quiere_musica", quiere_musica);
                i.putExtra("quiere_audio", quiere_audio);
                startActivity(i);
            }
        });


        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quiere_audio == true) {
                    mp_boton.start();
                }
                Intent i = new Intent(LoginActivity.this, EliminarCuentaActivity.class);
                i.putExtra("quiere_musica", quiere_musica);
                i.putExtra("quiere_audio", quiere_audio);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Comprobamos si está registrado el usuario e inicializamos el GameActivity
     */
    private void LoginUsuario() {
        mAuth.signInWithEmailAndPassword(email, contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Intent i = new Intent(LoginActivity.this, GameActivity.class);
                    i.putExtra("quiere_musica", quiere_musica);
                    i.putExtra("quiere_audio", quiere_audio);
                    startActivity(i);
                    finish();

                    //Guardamos los datos de inicio de sesión
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("usuario_email", email);
                    editor.putString("usuario_contraseña", contraseña);
                    editor.commit();


                } else {
                    Toast.makeText(LoginActivity.this, "El email o contraseña es incorrecto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Si ya se ha iniciado sesión antes en la app rellenamos automáticamente el formulario
     */
    private void RellenarCampos() {
        String usuario_email = preferences.getString("usuario_email", null);
        String usuario_contraseña = preferences.getString("usuario_contraseña", null);

        if (usuario_email != null && usuario_contraseña != null) {
            edt_email.setText(usuario_email);
            edt_contraseña.setText(usuario_contraseña);
        }
    }


    /**
     * Controlamos el funcionamiento del botón del audio y la imagen que se muestra
     * @param v
     */
    public void Audio(View v) {
        if (quiere_audio == true) {
            bt_audio_si.setImageResource(R.drawable.audio_no);
            quiere_audio = false;

        } else if (quiere_audio == false) {
            bt_audio_si.setImageResource(R.drawable.audio_si);
            quiere_audio = true;
        }
    }

    /**
     * Controlamos el funcionamiento del botón de la música y la imagen que se muestra
     * @param v
     */
    public void Musica(View v) {
        if (quiere_musica == true) {
            bt_musica_si.setImageResource(R.drawable.musica_no);
            quiere_musica = false;
            mp_inicio.pause();
        } else if (quiere_musica == false) {
            bt_musica_si.setImageResource(R.drawable.musica_si);
            quiere_musica = true;
            mp_inicio.start();

        }
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