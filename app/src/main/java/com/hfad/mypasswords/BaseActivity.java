package com.hfad.mypasswords;

import android.support.v7.app.AppCompatActivity;

import com.hfad.mypasswords.data.*;
import com.hfad.mypasswords.data.Item;;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by a602256 on 04/09/2017.
 */

public class BaseActivity extends AppCompatActivity {



    
    // Reference of DatabaseHelper class to access its DAOs and other components
    private DatabaseHelper databaseHelper = null;

    // This is how, DatabaseHelper can be initialized for future use
    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

		/*
		 * You'll need this in your class to release the helper when done.
		 */
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    protected QueryBuilder getQueryBuilder() throws SQLException{
        return getHelper().getItemDao().queryBuilder();
    }

    protected DeleteBuilder getDeleteBuilder()  throws SQLException{
        return getHelper().getItemDao().deleteBuilder();
    }

    protected  List<Item> query(PreparedQuery<Item> query) throws SQLException{
        return getHelper().getItemDao().query(query);
    }

}
