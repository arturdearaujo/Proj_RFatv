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

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dev.votu.rfatvapp.R;
import dev.votu.rfatvapp.util.TagPopulationManager;
import dev.votu.rfatvapp.util.TagTypeManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectReaderFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    /*****************************************************
     * CONSTANTS
     *****************************************************/
    static final int ACTIVITY_BLUETOOTH_ENABLED = 1;
    /*****************************************************
     * CUSTOM FIELDS
     ****************************************************/
    private TagTypeManager mTagTypeManager = null;
    private TagPopulationManager mTagPopulationManager = null;
    private OnSelectReaderFragmentInteractionListener mListener;
    /*****************************************************
     * UI FIELDS
     ****************************************************/
    private TextView mSelectDeviceText = null;
    private Spinner mBluetoothDeviceSpinner;
    private ToggleButton mConnectDisconnectButton;
    /*****************************************************
     * CONSTRUCTOR
     ****************************************************/
    public ConnectReaderFragment() {
        // Required empty public constructor
    }

    /*****************************************************
     * NATIVE METHODS
     ****************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_connect_reader, container, false);

        // Find the textView to control its state when the UI change. Just setting the enable flag.
        mSelectDeviceText = rootView.findViewById(R.id.SelectDeviceText);
        mBluetoothDeviceSpinner = rootView.findViewById(R.id.BluetoothDeviceSpinner);

        // Finds the default adapter to enable it and populate the spinner
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            populateDeviceList();
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ACTIVITY_BLUETOOTH_ENABLED);
        }

        mConnectDisconnectButton = rootView.findViewById(R.id.button_connect);
        mConnectDisconnectButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                    for (BluetoothDevice bt : pairedDevices) {
                        if (bt.getName().equals(mBluetoothDeviceSpinner.getSelectedItem().toString())) {
                            enableDeviceSelectionUI(false);
                            mListener.onConnectChanged(bt);
                            break;
                        }
                    }
                } else {
                    mListener.onConnectChanged(null);
                }
            }
        });

        mTagTypeManager = mListener.getTagTypeManager();
        mTagPopulationManager = new TagPopulationManager();

        Spinner spinner = rootView.findViewById(R.id.TagPopulationSpinner);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        for (int i = 0; i < mTagPopulationManager.getNumberOfSettings(); i++) {
            spinnerAdapter.add(getString(mTagPopulationManager.getName(i)));
        }
        spinnerAdapter.notifyDataSetChanged();

        CheckBox CB = rootView.findViewById(R.id.checkbox_report_option);
        CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton CB, boolean Checked) {
                mListener.onTagReportChange(Checked);
            }
        });

        LinearLayout TagTypeLL = rootView.findViewById(R.id.tag_type_linear_layout);
        for (int i = 0; i < mTagTypeManager.getNumberOfTagTypes(); i++)
            TagTypeLL.addView(mTagTypeManager.getNewCheckBox(i));
        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_BLUETOOTH_ENABLED) {
            if (resultCode == Activity.RESULT_OK) {
                populateDeviceList();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnSelectReaderFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnSelectTagsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String BRICommands[];
        BRICommands = mTagPopulationManager.getBRICommands(pos);
        mListener.onTagPopulationChange(BRICommands);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /*****************************************************
     * CUSTOM METHODS
     *****************************************************/

    private void populateDeviceList() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        List<String> pairedDeviceNames = new ArrayList<>();
        for (BluetoothDevice bt : pairedDevices) pairedDeviceNames.add(bt.getName());

        mBluetoothDeviceSpinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, pairedDeviceNames));
    }

    private void enableDeviceSelectionUI(boolean State) {
        mSelectDeviceText.setEnabled(State);
        mBluetoothDeviceSpinner.setEnabled(State);
    }

    public void setConnectionState(boolean State) {
        enableDeviceSelectionUI(!State);
        mConnectDisconnectButton.setChecked(State);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated to
     * the activity and potentially other fragments contained in that activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    interface OnSelectReaderFragmentInteractionListener {
        void onConnectChanged(BluetoothDevice bt);

        // Melhorada a forma de obter o TagTypeManager, agora através de uma interface.
        TagTypeManager getTagTypeManager();

        // Funcão copiada/transferida do fragment ReadSettingsFragment para ConnectReaderFragment. É preciso muita atenção aqui..
        void onTagPopulationChange(String[] BRICommands);

        void onTagReportChange(boolean ReportOnce);

    }
}
