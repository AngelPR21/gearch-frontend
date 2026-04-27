package com.example.gearch_frontend;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RadioGroup rgTipoUsuario = findViewById(R.id.rgTipoUsuario);
        LinearLayout layoutTaller = findViewById(R.id.layoutTaller);

        rgTipoUsuario.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPropietario) {
                layoutTaller.setVisibility(View.VISIBLE);
            } else {
                layoutTaller.setVisibility(View.GONE);
            }
        });
    }
}