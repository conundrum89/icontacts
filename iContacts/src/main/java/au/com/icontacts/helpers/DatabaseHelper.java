package au.com.icontacts.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import au.com.icontacts.models.Contact;

/**
 * Creates, opens and provides access to the database through OrmLite
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "icontacts.db";
    private static final int DATABASE_VERSION = 1;

    // The DAO objects we use to access various tables
    private RuntimeExceptionDao<Contact, Integer> contactsRuntimeDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); //, R.raw.ormlite_config);
    }

    /** Creates the underlying database with table name and column names. */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        Log.d("onCreate", "Creating Database");
        try {
            TableUtils.createTable(connectionSource, Contact.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for the Contact class. It will
     * create it or just give the cached value. RuntimeExceptionDao only throw RuntimeExceptions.
     */
    public RuntimeExceptionDao<Contact, Integer> getContactsDao() {
        if (contactsRuntimeDao == null) {
            contactsRuntimeDao = getRuntimeExceptionDao(Contact.class);
        }
        return contactsRuntimeDao;
    }

    /** Closes the database connection and clears any cached DAOs. */
    @Override
    public void close() {
        super.close();
        contactsRuntimeDao = null;
    }

    /** Upgrades the database in place when version number changes. */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // initial version, nothing to upgrade.
    }
}
