package dev.votu.rfatvapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import dev.votu.rfatvapp.activities.ExchangesFragment;
import dev.votu.rfatvapp.activities.ConnectReaderFragment;
import dev.votu.rfatvapp.activities.InventoryFragment;

/**
 * MyPagerAdapter class for operates with TabLayout
 * <p>
 * Created by DEV02 on 02/08/2017.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {

    /**
     * The number of tabs os the app.
     */
    final int PAGE_COUNT = 3;
    Context context;
    private String tabTitles[] = new String[]{"Leitores", "Inventários", "Comandos"};

    public MyPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ConnectReaderFragment();
        } else if (position == 1) {
            return new InventoryFragment();
        } else {
            return new ExchangesFragment();
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
