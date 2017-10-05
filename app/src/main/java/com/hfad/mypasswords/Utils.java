package com.hfad.mypasswords;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class Utils {


    public static final String GROUPID = "GroupId";
    public static final String ITEM_ID = "itemId";
    public static final String GROUP_NAME = "groupName";

    public static final String MODE = "mode";
    public static final String CREDENTIAL = "Credential";
    public static final String GROUPITEM_COLUMN = "group_item";
    public static final String ISGROUP_COLUMN = "isGroup";
    public static final String NOINPUT = "No inputs";
    public static final String STARS = "**********";
    public static final String EMPTY = "";
    public static final String RUNNING = "running";
    public static final String ISBACKUP = "isBackup";

    public static final String NAME = "name";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String ERROR = "error";



    public static String validateInputs(AppCompatActivity activity,String name, String login, String password){
        if(!Utils.hasText(name) && !Utils.hasText(login)
                &&  !Utils.hasText(password) ){
            return Utils.NOINPUT;
        }else if (!Utils.hasText(name)){
            return activity.getString(R.string.mandat_name_msg);
        }else if (!Utils.hasText(login) &&  !Utils.hasText(password)){
            return activity.getString(R.string.login_pwd_msg);
        }
        return null;
    }


    public static AlertDialog getAlertDialog(int layoutId, AppCompatActivity activity,
                                             DialogInterface.OnClickListener okListener,
                                             DialogInterface.OnClickListener cancelListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(layoutId, null));
        builder.setCancelable(true);
        builder.setPositiveButton(activity.getString(R.string.ok), okListener);
        if(cancelListener != null){
            builder.setNegativeButton(activity.getString(R.string.cancel_label), cancelListener);
        }
        return builder.create();
    }




    public static void setEditTextValue(AppCompatActivity activity, int id, String value){
        ((EditText)activity.findViewById(id)).setText(value, TextView.BufferType.EDITABLE);
    }

    public static String getEditTextValue(AppCompatActivity activity,int id){
        return ((EditText)activity.findViewById(id)).getText().toString();
    }

    public static String getTextViewValue(AppCompatActivity activity,int id){
        CharSequence charSequence = ((TextView)activity.findViewById(id)).getText();
        if(charSequence != null){
            return charSequence.toString();
        }
        return null;
    }

    public static void setTextViewValue(AppCompatActivity activity,int id, String value){
        ((TextView) activity.findViewById(id)).setText(value);
    }

    public static void setTextViewVisibility(AppCompatActivity activity,boolean visible){
        if(visible){
            ((TextView) activity.findViewById(R.id.error_msg)).setVisibility(View.VISIBLE);
        }else{
            ((TextView) activity.findViewById(R.id.error_msg)).setVisibility(View.INVISIBLE);
        }
    }

    public static void setEditTextVisibility(AppCompatActivity activity,boolean visible, int id){
        if(visible){
            ((EditText) activity.findViewById(id)).setVisibility(View.VISIBLE);
        }else{
            ((EditText) activity.findViewById(id)).setVisibility(View.INVISIBLE);
        }
    }

    public static boolean hasText(String value){
        if(value != null && !value.isEmpty()){
            return true;
        }
        return false;
    }

    public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        str = str.trim();
        return new StringBuffer(strLen)
             .append(Character.toTitleCase(str.charAt(0))).append(str.substring(1))
             .toString();
    }

}
