package com.hfad.mypasswords;

import android.app.*;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hfad.mypasswords.data.*;

import java.sql.SQLException;

public class CredentialDetailsActivity extends BaseActivity implements DialogFragment.DialogListener{


    private Item itemObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Integer id = (Integer)getIntent().getExtras().get(Utils.ITEM_ID);
        Integer groupId = (Integer)getIntent().getExtras().get(Utils.GROUPID);
        try{
            itemObject = getHelper().getItemDao().queryForId(id);
        }catch(SQLException e){
            e.printStackTrace();
        }

        TextView login = (TextView)findViewById(R.id.login);
        login.setText(itemObject.getLogin());

        TextView password = (TextView)findViewById(R.id.password);
        password.setText("******");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_show_pwd:
                DialogFragment dialogFragment = new DialogFragment();
                dialogFragment.setListener(this);
                dialogFragment.show(getFragmentManager(), "DialogFragment");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onDialogPositiveClick(DialogFragment dialog){
        String appPass = ((EditText)dialog.getDialog().findViewById(R.id.dialog_password)).getText().toString();
        try{
            Password object = (Password) getPasswordQueryBuilder().queryForFirst();
            if(EncUtil.decryptData(object.getPassword()).equals(appPass)){
                TextView password = (TextView)findViewById(R.id.password);
                password.setText(EncUtil.decryptData(itemObject.getPassword()));
            }
        }catch(Exception e){
            e.printStackTrace();
        }



    }

    public void onDialogNegativeClick(DialogFragment dialog){
        dialog.dismiss();
    }

}
