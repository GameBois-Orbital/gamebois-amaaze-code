package com.gamebois.amaaze.view.viewmaze;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.gamebois.amaaze.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ViewMazeActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 2;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maze);
        //Set the toolbar and enable up navigation
        mToolbar = findViewById(R.id.view_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //Instantiate the ViewPager2 and PagerAdapter for scrolling fragments
        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ViewMazePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        //Sync the tabs with scrolling fragments
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, new MazeTabConfiguration()).attach();
    }

    @Override
    public void onBackPressed() {
        int currPage = viewPager.getCurrentItem();
        if (currPage == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(currPage - 1);
        }
    }

    private static class MazeTabConfiguration implements TabLayoutMediator.TabConfigurationStrategy {

        @Override
        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
            if (position == 1) {
                tab.setText(R.string.title_private_tab);
            } else if (position == 0) {
                tab.setText(R.string.title_public_tab);
            }
        }
    }

    private static class ViewMazePagerAdapter extends FragmentStateAdapter {
        public ViewMazePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 1) {
                return ViewMazeFragment.newInstance(false);
            } else {
                return ViewMazeFragment.newInstance(true);
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

}