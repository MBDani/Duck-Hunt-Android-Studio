package com.example.proyectoclase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    //View
    private TextView tvCounterDucks, tvTimer, tvNick, tvContadorSuma;
    private ImageView ivDuck;

    //Variables
    private int counter = 0;
    private int anchoPantalla;
    private int altoPantalla;
    private Random aleatorio;
    private boolean gameOver = false, movimiento = true;
    private String id, nick;

    //Sonido
    private MediaPlayer mp_disparo, mp_reloj, mp_final;
    public boolean quiere_musica = true, quiere_audio = true;
    public boolean audio = true, musica = true;

    //Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    //ObjectAnimator
    private ObjectAnimator ejey, alpha;
    private long duracionAnimacion = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        tvCounterDucks = findViewById(R.id.textViewCounter);
        tvTimer = findViewById(R.id.textViewTimer);
        tvNick = findViewById(R.id.textViewNick);
        ivDuck = findViewById(R.id.imageViewDuck);
        tvContadorSuma = findViewById(R.id.textViewContador);


        initFirebase();
        initPantalla();
        initSonido();
        MoverPato();
        initCuentaAtras();
    }


    /**
     * Inicializamos las variables de firebase
     */
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = mAuth.getCurrentUser(); //obtenemos el usuario actual
        id = firebaseUser.getUid(); //Obtenemos el id del usuario actual
    }

    /**
     * Cogemos los datos de la pantalla e inicializamos sus elementos
     */
    private void initPantalla() {
        // 1. Obtener el tamaño de la pantalla del dispositivo
        // en el que estamos ejecutando la app
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        anchoPantalla = size.x;
        altoPantalla = size.y;

        // 2. Inicializamos el objeto para generar números aleatorios
        aleatorio = new Random();

        // 3. Recuperamos nombre:

        db.collection("users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                nick = documentSnapshot.get("nick").toString();
                tvNick.setText(nick);
            }
        });


        initTipoFuente();

    }

    /**
     * Inicializamos tipo de fuente
     */
    private void initTipoFuente() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "pixel.ttf");
        tvCounterDucks.setTypeface(typeface);
        tvTimer.setTypeface(typeface);
        tvNick.setTypeface(typeface);
        tvContadorSuma.setTypeface(typeface);
    }

    /**
     * Inicializamos el sonido
     */
    private void initSonido() {
        Bundle datos = this.getIntent().getExtras();
        quiere_audio = datos.getBoolean("quiere_audio");
        quiere_musica = datos.getBoolean("quiere_musica");


        if (quiere_musica == true) {
            mp_reloj = MediaPlayer.create(this, R.raw.reloj);
            mp_final = MediaPlayer.create(this, R.raw.finalizar);
        }

        if (quiere_audio == true) {
            mp_disparo = MediaPlayer.create(this, R.raw.disparo);
        }
    }

    /**
     * Llamamos al método AnimacionContador
     * Cambiamos foto del pato durante unos segundos
     * Sonará el sonido del disparo
     * Llamamos al método MoverPato
     *
     * @param v
     */
    public void CambiarFotoPato(View v) {
        if (!gameOver && movimiento) {

            AnimacionContador();

            ivDuck.setImageResource(R.drawable.duck_clicked);
            movimiento = false;

            if (quiere_audio == true) {
                mp_disparo.start();
            }


            //Le añadimos el delay para que se aprecie el cambio de imagen
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivDuck.setImageResource(R.drawable.duck);
                    MoverPato();
                    movimiento = true;
                }
            }, 500);
        }
    }

    /**
     * En este método sumamos 1 al contador e iniciliazamos su animación
     */
    private void AnimacionContador() {

        //Sumamos 1 al contador
        counter++;
        tvCounterDucks.setText(String.valueOf(counter));

        //Hacemos aparecer el +1 de al lado y que suba en su eje y
        tvContadorSuma.setVisibility(View.VISIBLE);
        ejey = ObjectAnimator.ofFloat(tvContadorSuma, "y", 32f, -150f);
        ejey.setDuration(duracionAnimacion + 100);

        //Hacemos que desaparezca
        alpha = ObjectAnimator.ofFloat(tvContadorSuma, View.ALPHA, 1.0f, 0.0f);
        alpha.setDuration(duracionAnimacion - 200);

        //Juntamos las animaciones y la ejecutamos
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ejey, alpha);
        animatorSet.start();
    }

    /**
     * Animación para mover el pato
     */
    private void MoverPato() {
        int maximoX = anchoPantalla - (ivDuck.getWidth() + (ivDuck.getWidth() / 2));
        int maximoY = altoPantalla - (ivDuck.getHeight() + (ivDuck.getHeight() / 2));

        // Generamos 2 números aleatorios, uno para la coordenada
        // x y otro para la coordenada Y.
        int randomX = aleatorio.nextInt(maximoX);
        int randomY = aleatorio.nextInt(maximoY);

        // Utilizamos los números aleatorios para mover el pato
        // a esa posición
        ivDuck.setX(randomX);
        ivDuck.setY(randomY);
    }


    /**
     * Inicialización del cronómetro
     * Añadimos sonido al llegar los 10 segundos restantes y sonido al finalizar
     * Llamamos al método mostrarDialogoGameOver
     */
    private void initCuentaAtras() {
        new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                long segundosRestantes = millisUntilFinished / 1000;
                tvTimer.setText(segundosRestantes + "s");

                if (segundosRestantes == 10) {

                    if (quiere_musica == true) {
                        mp_reloj.start();
                    }

                }
            }

            public void onFinish() {
                tvTimer.setText("0s");
                gameOver = true;

                if (quiere_musica == true) {
                    mp_final.start();
                }
                mostrarDialogoGameOver();
            }
        }.start();
    }

    /**
     * Mostramos la pantalla final del juego dando las opciones de reiniciar o ir a ver el ranking
     */
    private void mostrarDialogoGameOver() {
        // 1. Inicializamos la alerta de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Le asociamos al layout resource correspondiente
        View v = getLayoutInflater().inflate(R.layout.dialogo_game_over, null);

        // 3. Referenciamos los items que aparecen en nuestro layout de diálogo
        TextView txt_puntos = v.findViewById(R.id.textViewPuntosObtenidos);
        final TextView txt_record = v.findViewById(R.id.textViewRecord);
        LottieAnimationView animacionGameOver = v.findViewById(R.id.animationView); //OJO! importante poner el v.


        //Personalizamos el titulo
        builder.setCancelable(false); //evitamos que se pueda cancelar sin tocar los botones

        builder.setView(v); //le añadimos la vista


        //Personalizamos el contenido
        txt_puntos.setText("Puntuación: " + counter);

        db.collection("users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String patosRecord = documentSnapshot.get("ducks").toString();
                int record = Integer.parseInt(patosRecord);


                if (record > counter) {
                    txt_record.setText("Record: " + record);
                } else if (record < counter) {
                    txt_record.setText("¡Has batido tu anterior record de " + record + " patos cazados!");
                } else if (record == counter) {
                    txt_record.setText("¡Has empatado con tu record personal!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                txt_record.setText("Error al obtener tu record del servidor");
            }
        });


        // Añadimos los botones
        builder.setPositiveButton("Reiniciar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                counter = 0;
                tvCounterDucks.setText("0");
                gameOver = false;
                initCuentaAtras();
                MoverPato();
            }
        });
        builder.setNegativeButton("Ver ranking", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                startActivity(new Intent(GameActivity.this, RankingActivity.class));


            }
        });

        //Creamos la alerta y la mostramos
        AlertDialog dialog = builder.create();
        dialog.show();

        saveResultFirestore();
    }


    /**
     * Actualizamos los resultados en la base de datos de Firestore
     */
    private void saveResultFirestore() {


        db.collection("users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String recordString = documentSnapshot.get("ducks").toString();
                int record = Integer.parseInt(recordString);

                if (counter > record) {
                    db.collection("users")
                            .document(id)
                            .update("ducks", counter);
                }
            }
        });

    }

    /**
     * Inutilizamos el botón de ir hacia atrás
     */
    @Override
    public void onBackPressed() {

    }

    /**
     * Para que pare el juego junto con el multimedia
     */
    @Override
    public void onStop() {
        super.onStop();
        if (quiere_musica == true) {
            musica = false;
        }

        if (quiere_audio == true) {
            audio = false;
        }


    }


    /**
     * Para que se reaunude el juego junto con el multimedia
     */
    @Override
    public void onResume() {
        super.onResume();
        if (quiere_musica == true) {
            musica = true;
        }

        if (quiere_audio == true) {
            audio = true;
        }

    }

}