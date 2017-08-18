package dev.votu.rfatvapp.legacy;

import android.app.Activity;

public class TagTypeCheckBox extends android.support.v7.widget.AppCompatCheckBox {
    private int mIndex;

    public TagTypeCheckBox(Activity A, int Index, int StringID, boolean Checked)
    {
        super(A);
        mIndex = Index;
        setChecked(Checked);
        setText(StringID);
    }

    @Override
    public void setEnabled(boolean Enabled)
    {
        super.setEnabled(Enabled);
        if (!Enabled) setChecked(false);
    }

    public int getIndex()
    {
        return mIndex;
    }
}
