package au.com.icontacts.helpers;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;

import au.com.icontacts.models.Contact;

/**
 * Uses OrmLiteConfigUtil to create an ormlite_config.txt file containing the database setup config.
 * This prevents OrmLite from needing to run the slow and inefficient annotation methods on startup.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[] {
            Contact.class
    };

    public static void main(String[] args) throws Exception {
        writeConfigFile(new File("/Users/matt/Documents/workspace/iContactsProject/iContacts/src/main/res/raw/ormlite_config.txt"), classes);
    }
}
