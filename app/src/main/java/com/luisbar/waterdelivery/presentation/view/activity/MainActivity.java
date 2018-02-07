package com.luisbar.waterdelivery.presentation.view.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.luisbar.waterdelivery.R;
import com.luisbar.waterdelivery.common.eventbus.EventBusMask;
import com.luisbar.waterdelivery.common.eventbus.LogOutFailedV;
import com.luisbar.waterdelivery.common.eventbus.LogOutSucceededV;
import com.luisbar.waterdelivery.presentation.presenter.HomeLeftPresenter;
import com.luisbar.waterdelivery.presentation.view.fragment.HomeLeftFragment;
import com.luisbar.waterdelivery.presentation.view.service.TrackingService;
import com.luisbar.waterdelivery.presentation.view.adapter.HomeViewPagerAdapter;
import com.luisbar.waterdelivery.presentation.view.fragment.ClientFragment;
import com.luisbar.waterdelivery.presentation.view.fragment.HomeRightFragment;
import com.luisbar.waterdelivery.presentation.view.fragment.InvoiceFragment;
import com.luisbar.waterdelivery.presentation.view.fragment.LoginFragment;
import com.luisbar.waterdelivery.presentation.view.fragment.OrderFragment;
import com.luisbar.waterdelivery.presentation.view.listener.CustomOnBackPressed;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements LoginFragment.LoginFragmentNestedI,
        OrderFragment.OrderFragmentI, ClientFragment.ClientFragmentI, InvoiceFragment.InvoiceFragmentI,
        MainActivityI, FragmentManager.OnBackStackChangedListener {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private HomeLeftPresenter mHomeLeftPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Subscribe the listener
     */
    @Override
    protected void onStart() {
        super.onStart();
        EventBusMask.subscribe(this);
    }

    /**
     * Unsubscribe the listener
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusMask.unsubscribe(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                mHomeLeftPresenter.logOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            int stackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

            if (stackEntryCount == 0) {
                super.onBackPressed();
            } else {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();

                for (int i = fragments.size() - 1; i >= 0; i--) {
                    Fragment fragment = fragments.get(i);

                    if (fragment != null && !(fragment instanceof HomeRightFragment) &&
                            !(fragment instanceof HomeLeftFragment)) {
                        CustomOnBackPressed customOnBackPressed = (CustomOnBackPressed) fragment;
                        customOnBackPressed.onBackPressed();
                        break;
                    }
                }
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Set the ViewPager when It is called,
     * this method is in a nested interface in LoginFragment
     */
    @Override
    public void setViewPager(String name) {
        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(homeViewPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        appBar.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.txt_7));
        getSupportActionBar().setSubtitle(name);
    }

    /**
     * Set a new toolbar for the HomeLeftFragment and HomeRightFragment,
     * and show the AppBar when ClientFragment and OrderFragment are destroyed
     * (Without it, the menu toolbar is modified,
     * this method is in a nested interface in OrderFragment and ClientFragment)
     */
    @Override
    public void setToolbar() {
        setSupportActionBar(toolbar);
        appBar.setVisibility(View.VISIBLE);
    }

    /**
      *Hide the AppBar for theOrderFragment and ClientFragment,
     * this method is in a nested interfacein  OrderFragment and ClientFragmentt
     */
    @Override
    public void hideAppBar() {
        appBar.setVisibility(View.GONE);
    }

    /**
     * It Initialize butterknife, load the LoginFragment, initialize the global vars
     * and init the service for getting the location
     */
    @Override
    public void configActivity() {
        ButterKnife.bind(this);
        loadFragment(R.id.fragment_container, new LoginFragment());
        mHomeLeftPresenter = new HomeLeftPresenter();
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (!isMyServiceRunning(TrackingService.class)) {
            Intent intent = new Intent(this, TrackingService.class);
            startService(intent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Listener when logOut method in HomeLefPresenter has succeeded
     */
    @Subscribe
    @Override
    public void logOutSucceded(LogOutSucceededV logOutSucceededV) {
        hideAppBar();

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null)
                removeCurrentFragment(fragment);
        }

        LoginFragment loginFragment = new LoginFragment();
        loadFragment(R.id.fragment_container, loginFragment);
    }

    /**
     * Listener when logOut method in HomeLefPresenter has failed
     */
    @Subscribe
    @Override
    public void logOutFailed(LogOutFailedV logOutFailedV) {
        showSnackBar(getString(R.string.txt_34));
    }

    /**
     * This listener is used for showing the backstack about fragments
     */
    @Override
    public void onBackStackChanged() {
        Log.e("onBackStackChanged: ", getSupportFragmentManager().getFragments().toString() + "|" + getSupportFragmentManager().getBackStackEntryCount());
    }
}
