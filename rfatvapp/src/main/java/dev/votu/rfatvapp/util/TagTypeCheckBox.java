package dev.votu.rfatvapp.util;

import android.app.Activity;
import android.content.Context;

public class TagTypeCheckBox extends android.support.v7.widget.AppCompatCheckBox {
    private int mIndex;

    public TagTypeCheckBox(Context context, int mIndex, int StringID, boolean Checked) {
        super(context);
        this.mIndex = mIndex;
        setChecked(Checked);
        setText(StringID);
    }

    public TagTypeCheckBox(Activity activity, int mIndex, int stringId, boolean checked) {
        super(activity);
        this.mIndex = mIndex;
        setChecked(checked);
        setText(stringId);
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
