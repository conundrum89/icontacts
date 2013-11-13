package au.com.icontacts.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FilterQueryProvider;
import android.widget.ListView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;

import au.com.icontacts.R;
import au.com.icontacts.adapters.ContactAdapter;
import au.com.icontacts.models.Contact;

/**
 * Displays a list of Contacts from an adapter of some kind.
 */
public class ContactListFragment extends OrmLiteFragment
        implements FilterQueryProvider {
    private ContactAdapter mAdapter;
    private String mContactType;
    private ListView mContactList;

    public ContactListFragment() {
        this(null);
    }

    public ContactListFragment(String contactType) {
        super();
        mContactType = contactType;
    }

    @Override
    public Cursor runQuery(CharSequence constraint) {
        mAdapter.setConstraint(constraint.toString());
        return getContactCursor(constraint, mContactType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor cursor = getContactCursor(null, mContactType);

        int[] to = new int[] { R.id.name, R.id.phone, R.id.address };
        mAdapter = new ContactAdapter(getActivity(), R.layout.list_item_contact, cursor, to);
        mAdapter.setFilterQueryProvider(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContactList = (ListView) inflater.inflate(R.layout.fragment_contact_list, container, false);
        return mContactList;
    }

    private Cursor getContactCursor(CharSequence constraint, String type) {
        RuntimeExceptionDao<Contact, Integer> contactsDao = getHelper().getContactsDao();

        QueryBuilder<Contact, Integer> contactQb = contactsDao.queryBuilder();

        try {
            if (constraint != null && constraint.length() > 0) {
                // TODO: Put into a separate method
                String[] searchTerms = constraint.toString().split("\\s|,\\s");
                Where contactWhere = contactQb.where();

                for (String term : searchTerms) {
                    contactWhere.or(
                            contactWhere.like("first_name", "%"+term+"%"),
                            contactWhere.like("middle_name", "%"+term+"%"),
                            contactWhere.like("last_name", "%"+term+"%")
                    );
                    // TODO: Address matches
                }

                contactWhere.and(searchTerms.length);
                // TODO: Phone match
                // TODO: joinOr contact with address and phone

                // TODO: Type matching ( need to convert to a queryString :( )
                contactQb.distinct();

            } else {
                contactQb.distinct();
                // TODO: Type matching ( need to convert to a queryString :( )
            }

            return getHelper().getWritableDatabase().rawQuery(contactQb.prepareStatementString(), null);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}