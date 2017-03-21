package com.commontime.plugin;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

class PicklistContentObserver extends ContentObserver
{
    public PicklistContentObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        // do s.th.
        // depending on the handler you might be on the UI
        // thread, so be cautious!
    }
}
