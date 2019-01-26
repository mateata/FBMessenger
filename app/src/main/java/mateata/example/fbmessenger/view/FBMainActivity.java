package mateata.example.fbmessenger.view;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import mateata.example.fbmessenger.R;


public class FBMainActivity extends AppCompatActivity {

    TabLayout mTabLayout;

    FloatingActionButton mFab;

    ViewPager mViewPager;

    ViewPagerAdapter mPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbmain);
        mTabLayout.setupWithViewPager(mViewPager);
        setUpViewPager();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment currentFragment = mPageAdapter.getItem(mViewPager.getCurrentItem());
                if (currentFragment instanceof FriendFragment) {
                    ((FriendFragment) currentFragment).toggleSearchBar();
                }
            }
        });
    }


    private void setUpViewPager(){
        mPageAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPageAdapter.addFragment(new ChatFragment(), "채팅");
        mPageAdapter.addFragment(new FriendFragment(), "친구");
        mViewPager.setAdapter(mPageAdapter);
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

    }
}
