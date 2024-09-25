package com.kernal.demo.common.realm;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by huy  on 2022/12/21.
 */
public class MyMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 1 && newVersion == 2) {
            schema.get("Street").addField("prepayDuration", Double.class).setRequired("prepayDuration", true);
            oldVersion++;
        }
    }
}
