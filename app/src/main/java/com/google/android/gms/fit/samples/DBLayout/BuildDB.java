package com.google.android.gms.fit.samples.DBLayout;

import android.content.Context;

/**
 * Created by smukhopadhyay on 4/14/16.
 */
public class BuildDB extends ProxyDB implements CreateDBInterface, DeleteDBInterface, ReadDBInterface, UpdateDBInterface {

    public BuildDB(Context context) {
        super(context);
    }

}
