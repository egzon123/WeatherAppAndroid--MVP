package com.egzonberisha.weatherappandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.egzonberisha.weatherappandroid.adapters.ViewPagerAdapter;
import com.egzonberisha.weatherappandroid.fragments.CityFragment;
import com.egzonberisha.weatherappandroid.fragments.ForecastFragment;
import com.egzonberisha.weatherappandroid.presenters.MainPresenter;
import com.egzonberisha.weatherappandroid.views.MainMvpView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MvpActivity<MainMvpView, MainPresenter> implements MainMvpView {
    @BindView(R.id.toolbar)
     Toolbar toolbar;
    @BindView(R.id.tabs)
     TabLayout tabLayout;
    @BindView(R.id.view_pager)
     ViewPager viewPager;
    @BindView(R.id.root_view)
     CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        presenter.loadCities(MainActivity.this);
        presenter.loadLocationPermissionDialog(MainActivity.this);
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    public void setUpTabLayout() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(TodayWeatherFragment.getInstance(), "Today");
        System.out.println("-------------- TTodayWeatherFragment--------------");
        adapter.addFragment(ForecastFragment.getInstance(), "5 DAYS");
        System.out.println("-------------- ForecastFragment--------------");
        adapter.addFragment(CityFragment.getInstance(), "Cities");
        System.out.println("-------------- CityFragment--------------");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void showLoactionDenied() {
        Snackbar.make(coordinatorLayout, "Permission Denied", Snackbar.LENGTH_LONG)
                .show();
    }
}
