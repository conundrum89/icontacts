package au.com.icontacts.models;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Stores the basic information about a Contact.
 */
@DatabaseTable(tableName = "contacts")
public class Contact {
    public static final String ID_FIELD_NAME = "_id";
    public static final String FIRST_NAME_FIELD_NAME = "first_name";
    public static final String MIDDLE_NAME_FIELD_NAME = "middle_name";
    public static final String LAST_NAME_FIELD_NAME = "last_name";
    public static final String EMAIL_FIELD_NAME = "email";
    public static final String IS_LANDLORD_FIELD_NAME = "is_landlord";
    public static final String IS_VENDOR_FIELD_NAME = "is_vendor";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    @Expose @SerializedName("id")
    private int id;

    @DatabaseField(columnName = FIRST_NAME_FIELD_NAME)
    @Expose @SerializedName(FIRST_NAME_FIELD_NAME)
    private String firstName;

    @DatabaseField(columnName = MIDDLE_NAME_FIELD_NAME)
    @Expose @SerializedName(MIDDLE_NAME_FIELD_NAME)
    private String middleName;

    @DatabaseField(canBeNull = false, columnName = LAST_NAME_FIELD_NAME)
    @Expose @SerializedName(LAST_NAME_FIELD_NAME)
    private String lastName;

    @DatabaseField(columnName = EMAIL_FIELD_NAME)
    @Expose
    private String email;

    @DatabaseField(columnName = IS_LANDLORD_FIELD_NAME)
    public boolean isLandlord;

    @DatabaseField(columnName = IS_VENDOR_FIELD_NAME)
    public boolean isVendor;

    public Contact() {
        // For ORMLite
    }

    public int getId() {
        return this.id;
    }

    public String getFirstName() {
        return firstName == null ? "" : firstName;
    }

    public String getMiddleName() {
        return middleName == null ? "" : middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return TextUtils.join(" ", new String[] { getFirstName(), getMiddleName(), getLastName() }).replaceAll("\\s+", " ");
    }

    public String getEmail() {
        return email;
    }
}
