package com.hfad.mypasswords;

import android.os.Bundle;

import android.view.MenuItem;
import android.widget.TextView;

import com.hfad.mypasswords.data.*;

import java.sql.SQLException;

public class CredentialDetailsActivity extends BaseActivity {






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Integer id = (Integer)getIntent().getExtras().get(Utils.ITEM_ID);
        Integer groupId = (Integer)getIntent().getExtras().get(Utils.GROUPID);
        Item item = null;
        try{
            item = getHelper().getItemDao().queryForId(id);
        }catch(SQLException e){
            e.printStackTrace();
        }

        TextView login = (TextView)findViewById(R.id.login);
        login.setText(item.getLogin());

        TextView password = (TextView)findViewById(R.id.password);
        password.setText(EncUtil.decrypt(item.password, EncUtil.getSecretKeySpec()));
        //password.setText(item.getPassword());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
