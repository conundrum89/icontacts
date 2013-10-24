package au.com.icontacts.adapters;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A custom ViewPager implementation which allows for the paging to be disabled.
 */
public class LockableViewPager extends ViewPager {
    private boolean locked;

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.locked = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!locked) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!locked) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    public void setPagingLocked(boolean locked) {
        this.locked = locked;
    }
}
