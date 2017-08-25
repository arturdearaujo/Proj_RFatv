package dev.votu.rfatvapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import dev.votu.rfatvapp.activities.ConnectReaderFragment;
import dev.votu.rfatvapp.activities.ExchangesFragment;
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
    private final int PAGE_COUNT = 3;
    private Context context;

    private ConnectReaderFragment mConnectReaderFragment;
    private InventoryFragment mInventoryFragment;
    private ExchangesFragment mExchangesFragment;

    private String tabTitles[] = new String[]{"Conectar", "Leitura", "Inventários"};

    public MyPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        mConnectReaderFragment = new ConnectReaderFragment();
        mInventoryFragment = new InventoryFragment();
        mExchangesFragment = new ExchangesFragment();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return mConnectReaderFragment;
        } else if (position == 1) {
            return mInventoryFragment;
        } else {
            return mExchangesFragment;
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
