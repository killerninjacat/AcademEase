package com.example.studentcompanion.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.studentcompanion.dayfragments.FridayFragment;
import com.example.studentcompanion.dayfragments.MondayFragment;
import com.example.studentcompanion.dayfragments.SaturdayFragment;
import com.example.studentcompanion.dayfragments.SundayFragment;
import com.example.studentcompanion.dayfragments.ThursdayFragment;
import com.example.studentcompanion.dayfragments.TuesdayFragment;
import com.example.studentcompanion.dayfragments.WednesdayFragment;

public class TabPagerAdapter extends FragmentStateAdapter {

    public TabPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MondayFragment();
            case 1:
                return new TuesdayFragment();
            case 2:
                return new WednesdayFragment();
            case 3:
                return new ThursdayFragment();
            case 4:
                return new FridayFragment();
            case 5:
                return new SaturdayFragment();
            case 6:
                return new SundayFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}
