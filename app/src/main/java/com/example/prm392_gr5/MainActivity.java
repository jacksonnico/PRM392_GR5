package com.example.prm392_gr5;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.user.PitchDetailActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        new DatabaseHelper (this).getWritableDatabase();
        Intent intent = new Intent(this, PitchDetailActivity.class);
        intent.putExtra("pitchId", 1);
        startActivity(intent);
        finish();

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    view.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                }
        );
    }
}
