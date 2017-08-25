package dev.votu.rfatvapp.util;


import android.support.annotation.NonNull;

import dev.votu.rfatvapp.R;

public class TagPopulationManager {

    static TagPopulation[] mTagPopulation = {
            new TagPopulation(R.string.TagPopulationLessThan80, new String[]{"attrib initq=4", "attrib idtimeout=100"}),
            new TagPopulation(R.string.TagPopulationMoreThan80, new String[]{"attrib initq=4", "attrib idtimeout=1000"}),
            new TagPopulation(R.string.TagPopulationOnly1, new String[]{"attrib initq=0", "attrib idtimeout=100"})};

    public int getNumberOfSettings() {
        return mTagPopulation.length;
    }

    public int getName(int i) {
        return mTagPopulation[i].mName;
    }

    @NonNull
    public String[] getBRICommands(int i) {
        return mTagPopulation[i].mBRICommands;
    }

    static class TagPopulation {
        int mName;
        String[] mBRICommands;

        public TagPopulation(int Name, String[] BRICommands) {
            mName = Name;
            mBRICommands = BRICommands;
        }

        public int getName() { return mName; }
        public String[] getBRICommands() { return mBRICommands; }
    }
}