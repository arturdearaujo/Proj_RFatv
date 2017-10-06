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

package dev.votu.rfatvapp.activities;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import dev.votu.rfatvapp.R;
import dev.votu.rfatvapp.adapters.MyPagerAdapter;
import dev.votu.rfatvapp.bluetooth.BluetoothConnection;
import dev.votu.rfatvapp.util.TagTypeManager;

/**
 * Projeto RF Atv app.
 * <p>
 * VOTU RFID Solutions.
 * <p>
 * autor: Artur de Araujo   e-mail: artur.at4@gmail.com
 * 18/08/2017
 */
public class MainActivity extends AppCompatActivity implements
        ConnectReaderFragment.OnSelectReaderFragmentInteractionListener,
        InventoryFragment.OnReadTagsFragmentInteractionListener,
        TagTypeManager.OnTagTypeChangeListener {

    /*****************************************************
     * CONSTANTS
     *****************************************************/
    private final int BRI_SHORT_TIMEOUT_MILLISECONDS = 500;
    private final int BRI_LONG_TIMEOUT_MILLISECONDS = 5000;
    public TagTypeManager mTagTypeManager;
    /*****************************************************
     * UI FIELDS
     *****************************************************/
    ConnectReaderFragment mConnectReaderFragment;
    ExchangesFragment mExchangesFragment;
    InventoryFragment mInventoryFragment;
    Toolbar toolbar;
    ViewPager viewPager;
    MyPagerAdapter adapter;
    TabLayout tabLayout;
    LinearLayout mConnectedSettingsLinearLayout;
    MenuItem mActionConnectStatus = null;
    /*****************************************************
     * CUSTOM FIELDS
     *****************************************************/
    private BluetoothConnection mBluetoothConnection = null;
    private BluetoothDevice mBluetoothDevice;
    private BlockingQueue<String> mBluetoothMessageQueue;
    private Thread mBRIMessageHandler;
    private boolean mTagReportOnce = false;
    private boolean mReadInProgress = false;
    private boolean mWasConnectedWhenPaused = false;
    private String[] mPopulationCommands = null;
    private String mTagToLocate = null;
    private Integer mPowerSetting = 30;
    /*****************************************************
     * NATIVE METHODS
     ****************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the toolbar.
        toolbar = (Toolbar) findViewById(R.id.main_tool_bar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        // Find the view pager that will allow the user to swipe between fragments
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        adapter = new MyPagerAdapter(getSupportFragmentManager(), this);

        // Get the three fragments used in the application
        mConnectReaderFragment = (ConnectReaderFragment) adapter.getItem(0);
        mInventoryFragment = (InventoryFragment) adapter.getItem(1);
        mExchangesFragment = (ExchangesFragment) adapter.getItem(2);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);


        // Find the tab layout that shows the tabs
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Connect the tab layout with the view pager. This will
        //   1. Update the tab layout when the view pager is swiped
        //   2. Update the view pager when a tab is selected
        //   3. Set the tab layout's tab names with the view pager's adapter's titles
        //      by calling onPageTitle()
        tabLayout.setupWithViewPager(viewPager);

        mTagTypeManager = new TagTypeManager(this);
        mBluetoothMessageQueue = new ArrayBlockingQueue<>(100);
        mBRIMessageHandler = new Thread(new BRIEventMessageHandler());
        mBRIMessageHandler.start();

        mConnectedSettingsLinearLayout = (LinearLayout) findViewById(R.id.connected_settings_linear_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        boolean result = super.onCreateOptionsMenu(menu);
        mActionConnectStatus = menu.findItem(R.id.action_connect_status);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        mWasConnectedWhenPaused = mBluetoothConnection != null;
        StopRead();
        CleanUp();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWasConnectedWhenPaused) {
            mConnectReaderFragment.setConnectionState(true);
        }
    }

    /*****************************************************
     * CUSTOM METHODS
     *****************************************************/
    private void MyToast(int StringID) {
        Toast.makeText(this, StringID, Toast.LENGTH_SHORT).show();
    }

    private void ReaderDisconnectedToast() {
        MyToast(R.string.reader_disconnected_toast);
    }

    private String BRIWhereClause(String TagID) {
        return "where hex(1:4," + String.valueOf(TagID.length() / 2) + ")=H" + TagID + " ";
    }

    private void SendInitializationCommands(String[] SArray) {
        for (String S : SArray) {
            if (SendBRICommand(S, BRI_SHORT_TIMEOUT_MILLISECONDS, R.string.initialization_failed_toast) == null)
                break;
        }
    }

    @Nullable
    private String SendBRICommand(String S, int TimeOutMilliseconds, int FailToastStringID) {
        String Result;
        if (mBluetoothConnection == null) return null;
        if ((Result = mBluetoothConnection.SendBRICommand(S, TimeOutMilliseconds)) == null) {
            MyToast(FailToastStringID);
            CleanUp();
            return null;
        }
        return Result;
    }

    private void ConnectBluetooth() {
        try {
            mBluetoothConnection = new BluetoothConnection(mBluetoothDevice, mBluetoothMessageQueue);
            mBluetoothConnection.start();
            MyToast(R.string.connected_toast);

            // Set the connected_status icon
            mActionConnectStatus.setIcon(R.drawable.ic_action_connect_status);

            // Create the initialization attributes in a array
            String[] AttributeSettings = {
                    "ATTRIB CHKSUM=OFFC9",
                    "ATTRIB ECHO=OFF",
                    "ATTRIB TTY=OFF",
                    "READ STOP",
                    "ATTRIB SESSION=0",
                    "ATTRIB SCHEDOPT=1",
                    "ATTRIB DRM=ON",
                    "ATTRIB IDTRIES=1",
                    "ATTRIB ANTTRIES=1",
                    "ATTRIB IDTIMETOUT=0",
                    "ATTRIB ANTTIMEOUT=0",
                    "ATTRIB INITIALQ=4",
                    "ATTRIB IDREPORT=1",
                    "ATTRIB NOTAGRPT=1",
            };
            SendInitializationCommands(AttributeSettings);

            // This way to implement setAllowed() grants the String tagTypeCapabilities will never be null.
            // Remember to check if this uncontrolled loop is affecting the UX.
            String tagTypeCapabilities;
            //TODO: verificaro impacto dessa cláusula if abaixo implementada para que o aplicativo rode em versões android antigas.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                do {
                    tagTypeCapabilities = SendBRICommand("cap tagtype", BRI_SHORT_TIMEOUT_MILLISECONDS, R.string.read_tag_type_failed_toast);
                } while (Objects.equals(tagTypeCapabilities, ""));
                mTagTypeManager.setAllowed(tagTypeCapabilities, getApplicationContext());
            }

            onRFPowerChange(mPowerSetting);
            onTagTypeChange();
            //TODO: O problema aqui é que este comando só pode ser executado se mPopulationCommands != null
            onTagPopulationChange(mPopulationCommands);

            for (String S : AttributeSettings) {
                if (SendBRICommand(S, BRI_SHORT_TIMEOUT_MILLISECONDS, R.string.initialization_failed_toast) == null)
                    break;
            }

            mConnectedSettingsLinearLayout.setVisibility(View.VISIBLE);

        } catch (IOException e) {
            mConnectReaderFragment.setConnectionState(false);
            MyToast(R.string.connect_failed_toast);
        }
    }

    @Override
    public void onConnectChanged(BluetoothDevice bt) {
        if (bt != null) {
            if (mBluetoothConnection == null) {
                mBluetoothDevice = bt;
                ConnectBluetooth();
            }
        } else
            CleanUp();
    }

    @Override
    public TagTypeManager getTagTypeManager() {
        return mTagTypeManager;
    }

    public void onRFPowerChange(int powerSetting) {
        mPowerSetting = powerSetting;
        if (mBluetoothConnection != null) {
            boolean ReadInProgress = mReadInProgress;
            if (ReadInProgress) StopRead();
            if (SendBRICommand("attrib fs=" + mPowerSetting.toString() + "db", BRI_SHORT_TIMEOUT_MILLISECONDS, R.string.set_power_failed_toast) != null && ReadInProgress)
                StartRead();
        }
    }

    public void onTagPopulationChange(String[] BRICommands) {
        mPopulationCommands = BRICommands;
        if (mBluetoothConnection != null && mPopulationCommands.length >= 0)
            SendInitializationCommands(mPopulationCommands);
        else
            Toast.makeText(this, "mPopulationCommands == null || mBluetoothConnection == null", Toast.LENGTH_SHORT).show();
    }

    public void onTagReportChange(boolean ReportOnce) {
        mTagReportOnce = ReportOnce;
    }

    public void onTagTypeChange() {
        if (mBluetoothConnection != null)
            SendBRICommand("attrib tagtype=" + mTagTypeManager.getTagTypeBRIString(), BRI_SHORT_TIMEOUT_MILLISECONDS, R.string.set_tagtype_failed_toast);
    }

    public void onLocateTagIDChange(String TagID) {
        mTagToLocate = TagID;
    }


    private void StartRead() {
        if (mBluetoothConnection != null) {

/*          TODO: 18/08/2017 Estudar nova abordagem para refatorar esse trecho de códico.
            Encontrar a nova forma de puxar o fragment em uso para frente do usuário.
            // switch to the Read Tags tab
            getActionBar().setSelectedNavigationItem(READ_TAGS_TAB);
*/

/*          TODO: 18/08/2017 Comentado o código que aparentemente leva a informação para o fragment de exibição das etiquetas.
            Setado com valor nulo "null" limpa a lista de tags já presente.
            // clear the tag list
            mReadTagsFragment.WriteTag(null);
*/

            // build the read command string
            String ReadCommand = "read ";
            if (mTagToLocate != null) ReadCommand += BRIWhereClause(mTagToLocate);
            ReadCommand += "report=event";
            if (!mTagReportOnce) ReadCommand += "all";

            // start the read
            if (SendBRICommand(ReadCommand, BRI_SHORT_TIMEOUT_MILLISECONDS, R.string.read_failed_toast) != null)
                mReadInProgress = true;
        } else {
            ReaderDisconnectedToast();
        }
    }

    private void StopRead() {
        if (mBluetoothConnection != null) {
            SendBRICommand("read stop", BRI_LONG_TIMEOUT_MILLISECONDS, R.string.read_stop_failed_toast);
        } else {
            ReaderDisconnectedToast();
        }
        mReadInProgress = false;
    }

    private void CleanUp() {
        mActionConnectStatus.setIcon(R.drawable.ic_action_disconnect_status);
        mConnectedSettingsLinearLayout = (LinearLayout) findViewById(R.id.connected_settings_linear_layout);
        mConnectedSettingsLinearLayout.setVisibility(View.GONE);

        mConnectReaderFragment.setConnectionState(false);
        if (mBluetoothConnection != null) {
            mBluetoothConnection.cancel();
            mBluetoothConnection = null;
        }
        mReadInProgress = false;
    }

    /*****************************************************
     * INNER CLASSES
     *****************************************************/
    private class UIEventHandler implements Runnable {
        String mEvtMsg;

        UIEventHandler(String EvtMsg) {
            mEvtMsg = EvtMsg;
        }

        @Override
        public void run() {
//            if (mReadTagsFragment != null) {
            if (mEvtMsg.startsWith("EVT:TAG")) {

/*
                    // switch to the Read Tags tab
                    getActionBar().setSelectedNavigationItem(READ_TAGS_TAB);
                    mReadTagsFragment.WriteTag(mEvtMsg.substring(9));
*/

            } else if (mEvtMsg.startsWith("EVT:TRIGGER")) {
                if (mEvtMsg.contains("TRIGPULL")) {

  /*
                        // switch to the Read Tags tab
                        getActionBar().setSelectedNavigationItem(READ_TAGS_TAB);
*/

                    StartRead();
                } else if (mEvtMsg.contains("TRIGRELEASE")) {
                    StopRead();
                }
//                } else if (mEvtMsg.startsWith("EVT:THERMAL")) {
//                } else if (mEvtMsg.startsWith("EVT:BATTERY")) {
//                } else if (mEvtMsg.startsWith("EVT:ANTENNA FAULT")) {
            }
//            }
        }
    }

    private class BRIEventMessageHandler implements Runnable {
        public void run() {
            for (; ; ) {
                try {
                    runOnUiThread(new UIEventHandler(mBluetoothMessageQueue.take()));
                } catch (InterruptedException E) {
                }
            }
        }
    }
}
