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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import dev.votu.rfatvapp.R;
import dev.votu.rfatvapp.adapters.TagListAdapter;
import dev.votu.rfatvapp.adapters.TagListEntry;


/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment {

    /*****************************************************
     * UI FIELDS
     ****************************************************/
    EditText inventoryNameEditText;
    EditText whenEditText;
    EditText whereEditText;
    TextView readTagsCountTextView;
    TextView expectedTagsCountTextView;
    Spinner inventoryTypeSpinner;
    CheckBox finishedStatusCheckBox;
    EditText authorEditText;
    Button completeInventory;
    ListView mTagWindow;
    private TagListAdapter mTagListAdapter;
    private OnReadTagsFragmentInteractionListener mListener;

    public InventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_inventory, container, false);

        inventoryNameEditText = rootView.findViewById(R.id.inventory_name_edit_text);
        whenEditText = rootView.findViewById(R.id.when_edit_text);
        whereEditText = rootView.findViewById(R.id.where_edit_text);
        readTagsCountTextView = rootView.findViewById(R.id.checkbox_report_option);
        expectedTagsCountTextView = rootView.findViewById(R.id.expected_tags_text_view);
        inventoryTypeSpinner = rootView.findViewById(R.id.inventory_type_spinner);
        finishedStatusCheckBox = rootView.findViewById(R.id.finished_inventory_text_view);
        authorEditText = rootView.findViewById(R.id.author_text_view);
        completeInventory = rootView.findViewById(R.id.complete_inventory_button);
        mTagWindow = rootView.findViewById(R.id.tags_list_view);

        mTagListAdapter = new TagListAdapter(getActivity(), new ArrayList<TagListEntry>());
        mTagWindow.setAdapter(mTagListAdapter);

        // Inflate the layout for this fragment
        return rootView;
    }

    private void enableDeviceSelectionUI(boolean state) {
        inventoryNameEditText.setEnabled(state);
        whenEditText.setEnabled(state);
        whereEditText.setEnabled(state);
        readTagsCountTextView.setEnabled(state);
        expectedTagsCountTextView.setEnabled(state);
        inventoryTypeSpinner.setEnabled(state);
        finishedStatusCheckBox.setEnabled(state);
        authorEditText.setEnabled(state);
        completeInventory.setEnabled(state);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnReadTagsFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnReadTagsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnReadTagsFragmentInteractionListener {
        void onRFPowerChange(int PowerSetting);

        void onLocateTagIDChange(String TagID);
    }
}
