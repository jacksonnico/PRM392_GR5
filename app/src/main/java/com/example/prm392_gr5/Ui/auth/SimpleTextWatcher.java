package com.example.prm392_gr5.Ui.auth;

import android.text.Editable;
import android.text.TextWatcher;

public class SimpleTextWatcher implements TextWatcher {

    private Runnable afterTextChanged;

    public SimpleTextWatcher(Runnable afterTextChanged) {
        this.afterTextChanged = afterTextChanged;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        afterTextChanged.run();
    }
}
