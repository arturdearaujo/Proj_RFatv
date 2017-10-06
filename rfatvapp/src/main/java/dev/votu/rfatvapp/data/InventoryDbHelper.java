/*
 * Copyright (C) 2017 VOTU RFid Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.votu.rfatvapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DEV02 on 11/09/2017.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file.
     */
    public static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    public static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE TABLE inventory (id INTEGER PRIMARY KEY, name TEXT, weight INTEGER);
        // Create a String that contains the SQL statement to create the inventories table.
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + "(" +
                InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryContract.InventoryEntry.COLUMN_NAME + " TEXT, " +
                InventoryContract.InventoryEntry.COLUMN_WHEN + " INTEGER, " +
                InventoryContract.InventoryEntry.COLUMN_WHERE + " TEXT, " +
                InventoryContract.InventoryEntry.COLUMN_QTY_READ_TAG + " INTEGER NOT NULL, " +
                InventoryContract.InventoryEntry.COLUMN_QTY_EXPECTED_TAG + " INTEGER, " +
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_TYPE + " INTEGER NOT NULL DEFAULT 1, " +
                InventoryContract.InventoryEntry.COLUMN_FINISHED + " INTEGER, " +
                InventoryContract.InventoryEntry.COLUMN_SUBMITTED + " INTEGER, " +
                InventoryContract.InventoryEntry.COLUMN_WHO + " TEXT, " + ")";
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
