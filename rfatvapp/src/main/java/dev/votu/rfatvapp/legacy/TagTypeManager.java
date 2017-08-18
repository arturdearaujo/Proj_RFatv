package dev.votu.rfatvapp.legacy;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import dev.votu.rfatvapp.R;
import dev.votu.rfatvapp.activities.MainActivity;
import dev.votu.rfatvapp.legacy.TagTypeCheckBox;


public class TagTypeManager {
    private TagTypeCheckBox[] mCB = null;
    private boolean[] mAllowed = null;
    private boolean[] mChecked = null;
    private MainActivity mListener = null;

    static class TagType {
        private int mCheckBoxTitle;
        private String mTagTypeName;
        public TagType(int CheckBoxTitle, String TagTypeName) {
                mCheckBoxTitle = CheckBoxTitle;
                mTagTypeName = TagTypeName;
        }
        public int getTitle() { return mCheckBoxTitle; }
        public String getName() { return mTagTypeName; }
    }
    static TagType[] mTagType = { new TagType(R.string.ReadEPCC1G2, "EPCC1G2" ),
                                  new TagType(R.string.ReadISO6B,   "ISO6BG2" ),
                                  new TagType(R.string.ReadATA,     "ATA"     ),
                                };

    public TagTypeManager(MainActivity A)
    {
        mListener = A;

        mAllowed = new boolean[mTagType.length];
        mChecked = new boolean[mTagType.length];
        mCB = new TagTypeCheckBox[mTagType.length];

        for (int i = 0; i < mTagType.length; i++) {
            mAllowed[i] = true;
            mChecked[i] = i == 0 ? true : false;
        }
    }

    public String getTagTypeBRIString()
    {
        String TagType = new String("");
        for (int i = 0; i < mTagType.length; i++) {
            if (mAllowed[i] && mCB[i].isChecked()) {
                if (TagType.length() != 0) TagType += ",";
                TagType += mTagType[i].getName();
            }
        }
        if (TagType.length() == 0) TagType += mTagType[0].getName();
        return TagType;
    }

    public void setAllowed(String ReaderTagTypeCapabilities)
    {
        for (int i = 0; i < mTagType.length; i++) {
            if (ReaderTagTypeCapabilities.indexOf(mTagType[i].getName()) < 0) { mAllowed[i] = false; }
            if (!mAllowed[i]) mCB[i].setEnabled(false);
        }
    }

    public int getNumberOfTagTypes() { return mTagType.length; }

    public CheckBox getNewCheckBox(int i)
    {
        mCB[i] = new TagTypeCheckBox(mListener, i, mTagType[i].getTitle(), mChecked[i]);
        mCB[i].setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton CB, boolean Checked)
            {
                mChecked[((TagTypeCheckBox)CB).getIndex()] = Checked;
                mListener.onTagTypeChange();
            }
        });
        return mCB[i];
    }

    public interface OnTagTypeChangeListener {
        public void onTagTypeChange();
    }
}
