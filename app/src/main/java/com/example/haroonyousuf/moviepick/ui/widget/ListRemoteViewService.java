package com.example.haroonyousuf.moviepick.ui.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ListRemoteViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new ListRemoteViewsFactory(getApplicationContext(),intent);
    }
}
