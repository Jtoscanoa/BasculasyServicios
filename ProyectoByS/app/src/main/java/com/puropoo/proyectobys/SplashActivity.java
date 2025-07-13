package com.puropoo.proyectobys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    ImageView ivLogo;
    TextView tvSplashMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivLogo = findViewById(R.id.ivLogo);
        tvSplashMessage = findViewById(R.id.tvSplashMessage);

        // Cargar animaciones
        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_zoom_fade);
        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.text_slide_up_bounce);

        // Ejecutar animaciones
        ivLogo.startAnimation(logoAnim);
        tvSplashMessage.setVisibility(View.VISIBLE);
        tvSplashMessage.startAnimation(textAnim);

        // Transición al menú principal
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        }, 2500);
    }
}
