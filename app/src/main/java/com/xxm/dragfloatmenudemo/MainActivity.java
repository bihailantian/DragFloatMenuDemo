package com.xxm.dragfloatmenudemo;

import android.os.Bundle;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xxm.dragfloatmenudemo.view.DragFloatActionButton;

public class MainActivity extends AppCompatActivity implements DragFloatActionButton.OnMenuItemSelectListener {

    private DragFloatActionButton floatActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatActionButton = findViewById(R.id.fab);
        floatActionButton.setMenuResource(R.layout.onwer_report_menu);
        floatActionButton.setOnMenuItemSelectListener(this);
    }


    @Override
    public void OnMenuItemSelected(View v, PopupWindow popWindow) {
        Toast.makeText(MainActivity.this, ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (floatActionButton != null)
            floatActionButton.dismissMenu();
    }
}
