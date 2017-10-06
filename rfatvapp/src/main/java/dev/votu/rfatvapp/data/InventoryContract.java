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

import android.provider.BaseColumns;

public final class InventoryContract {

    private InventoryContract() {
    }

    public static final class InventoryEntry {

        public static final String TABLE_NAME = "inventory";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "tag";
        public static final String COLUMN_WHEN = "when";
        public static final String COLUMN_WHERE = "where";
        public static final String COLUMN_QTY_READ_TAG = "quantity1";
        public static final String COLUMN_QTY_EXPECTED_TAG = "quantity2";
        public static final String COLUMN_INVENTORY_TYPE = "type";
        public static final String COLUMN_FINISHED = "finished";
        public static final String COLUMN_SUBMITTED = "submitted";
        public static final String COLUMN_WHO = "who";

        public static final int STATUS_1 = 1;
        public static final int STATUS_2 = 2;
        public static final int STATUS_3 = 3;
    }
}
