package com.hfad.mypasswords;

import android.os.Bundle;
import android.widget.EditText;

/**
 * Created by a602256 on 26/09/2017.
 */

public class UpdateCredentialActivity extends AddItemActivity {

    private int credentialId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String name = ((EditText)findViewById(R.id.add_name)).getText().toString();
        String login = ((EditText)findViewById(R.id.add_login)).getText().toString();
        String password = ((EditText)findViewById(R.id.add_password)).getText().toString();

    }


}
