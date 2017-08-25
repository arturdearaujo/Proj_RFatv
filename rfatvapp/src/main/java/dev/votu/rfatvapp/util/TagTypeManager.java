package dev.votu.rfatvapp.util;

import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import dev.votu.rfatvapp.R;
import dev.votu.rfatvapp.activities.MainActivity;


public class TagTypeManager {

    static TagType[] mTagType = {
            new TagType(R.string.ReadEPCC1G2, "EPCC1G2"),
            new TagType(R.string.ReadISO6B, "ISO6BG2"),
            new TagType(R.string.ReadATA, "ATA"),
    };
    private TagTypeCheckBox[] mCB = null;
    private boolean[] mAllowed = null;
    private boolean[] mChecked = null;
    private MainActivity mListener = null;

    public TagTypeManager(MainActivity A) {
        mListener = A;

        mAllowed = new boolean[mTagType.length];
        mChecked = new boolean[mTagType.length];
        mCB = new TagTypeCheckBox[mTagType.length];

        for (int i = 0; i < mTagType.length; i++) {
            mAllowed[i] = true;
            mChecked[i] = i == 0;
        }
    }

    public String getTagTypeBRIString() {
        String TagType = "";
        for (int i = 0; i < mTagType.length; i++) {
            if (mAllowed[i] && mCB[i].isChecked()) {
                if (TagType.length() != 0) TagType += ",";
                TagType += mTagType[i].getName();
            }
        }
        if (TagType.length() == 0) TagType += mTagType[0].getName();
        return TagType;
    }

    public void setAllowed(String ReaderTagTypeCapabilities, Context context) {
        if (ReaderTagTypeCapabilities != null) {
            for (int i = 0; i < mTagType.length; i++) {
                if (!ReaderTagTypeCapabilities.contains(mTagType[i].getName())) {
                    mAllowed[i] = false;
                }
                if (!mAllowed[i]) mCB[i].setEnabled(false);
            }
        } else {
            Toast.makeText(context, "setAllowed returned null String", Toast.LENGTH_SHORT).show();
            Log.e("TagTypeManager", "setAllowed: returned null");
            mAllowed[0] = true;
            mAllowed[1] = true;
            mAllowed[2] = true;
        }
    }

    public int getNumberOfTagTypes() {
        return mTagType.length;
    }

    public CheckBox getNewCheckBox(int i) {
        mCB[i] = new TagTypeCheckBox(mListener, i, mTagType[i].getTitle(), mChecked[i]);
        mCB[i].setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton CB, boolean Checked) {
                mChecked[((TagTypeCheckBox) CB).getIndex()] = Checked;
                mListener.onTagTypeChange();
            }
        });
        return mCB[i];
    }

    public interface OnTagTypeChangeListener {
        void onTagTypeChange();
    }

    static class TagType {
        private int mCheckBoxTitle;
        private String mTagTypeName;

        public TagType(int CheckBoxTitle, String TagTypeName) {
            mCheckBoxTitle = CheckBoxTitle;
            mTagTypeName = TagTypeName;
        }

        public int getTitle() {
            return mCheckBoxTitle;
        }

        public String getName() {
            return mTagTypeName;
        }
    }
}