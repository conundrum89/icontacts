package au.com.icontacts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.com.icontacts.models.Contact;

/**
 * Extends BaseAdapter for Contacts.
 */
public class ContactAdapter extends CursorAdapter {
    private int mLayout;
    private LayoutInflater mInflater;
    private int mRowIdColumn;
    private String mConstraint;

    private int[] mTo;

    public ContactAdapter(Context context, int layout, Cursor cursor, int[] to) {
        super(context, cursor, false);
        init(context, layout, cursor, to);
    }

    private void init(Context context, int layout, Cursor cursor, int[] to) {
        mLayout = layout;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRowIdColumn = cursor.getColumnIndexOrThrow("_id");
        mTo = to;
        mConstraint = "";
    }

    public void setConstraint(String constraint) {
        mConstraint = constraint.toLowerCase();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mInflater.inflate(mLayout, parent, false);
        return generateViewHolder(v);
    }

    private View generateViewHolder(View v) {
        final int count = mTo.length;
        final TextView[] holder = new TextView[count];

        for (int i = 0; i < count; i++) {
            holder[i] = (TextView) v.findViewById(mTo[i]);
        }
        v.setTag(holder);

        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final TextView[] holder = (TextView[]) view.getTag();
        final int count = mTo.length;
        final String[] mFrom = getContactFields(cursor);
        // set span highlight colour

        for (int i = 0; i <= count; i++) {
            final TextView v = holder[i];
            if (v != null) {
                String val = mFrom[i];
                if (val == null || val.equals("")) {
                    v.setVisibility(View.GONE);
                } else {
                    v.setVisibility(View.VISIBLE);
                    v.setText(val);
                }
                // TODO: All the logic for setting highlights, etc.
            }
        }
    }

    private String[] getContactFields(Cursor cursor) {
        // TODO: Use constraint
        String[] fields = new String[mTo.length];
        Contact contact = new Contact(mContext, cursor.getInt(mRowIdColumn));
        fields[0] = contact.getFullName();
        // fields[1] = contact.getPhone(mConstraint);
        // fields[2] = contact.getAddress(mConstraint);

        return fields;
    }
}
