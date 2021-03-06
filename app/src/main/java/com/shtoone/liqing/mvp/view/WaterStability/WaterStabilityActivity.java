package com.shtoone.liqing.mvp.view.WaterStability;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.shtoone.liqing.BaseApplication;
import com.shtoone.liqing.R;
import com.shtoone.liqing.event.EventData;
import com.shtoone.liqing.mvp.contract.base.BaseContract;
import com.shtoone.liqing.mvp.model.bean.DepartmentBean;
import com.shtoone.liqing.mvp.model.bean.ParametersData;
import com.shtoone.liqing.mvp.view.base.BaseActivity;
import com.shtoone.liqing.mvp.view.others.DetailActivity;
import com.shtoone.liqing.mvp.view.others.DrawerActivity;
import com.socks.library.KLog;

import java.util.ArrayList;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by Administrator on 2016/11/24.
 */
public class WaterStabilityActivity extends BaseActivity {


    private AHBottomNavigation bottomNavigation;
    private SupportFragment[] mFragments = new SupportFragment[5];
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private int bottomNavigationPreposition = 0;
    private FrameLayout fl_container;

    private static PopupWindow mPopupWindow;
    public FrameLayout fl_popupwindow;
    private DepartmentBean departmentBean ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waterstability);
        Intent intent =getIntent();
        Bundle bundle=intent.getBundleExtra("departmentData");
        departmentBean= (DepartmentBean) bundle.getSerializable("departmentData");
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation_waterstability);
        fl_container = (FrameLayout) findViewById(R.id.fl_container_waterstability);
        if (savedInstanceState == null) {
            mFragments[0] = WaterStabilityProductionQueryFragemnt.newInstance(departmentBean);
            mFragments[1] = WaterStabilityOverProofFragment.newInstance(departmentBean);
            mFragments[2] = MaterialStatisticsFragment.newInstance(departmentBean);
            loadMultipleRootFragment(R.id.fl_container_waterstability, 0, mFragments[0], mFragments[1], mFragments[2]);
        } else {
            mFragments[0] = findFragment(WaterStabilityProductionQueryFragemnt.class);
            mFragments[1] = findFragment(WaterStabilityOverProofFragment.class);
            mFragments[2] = findFragment(WaterStabilityStatisticsFragment.class);

        }
        initData();
    }

    @Override
    protected BaseContract.Presenter createPresenter() {
        return null;
    }


    public void initData() {

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.HistoryData, R.drawable.ic_search_white_18dp, R.color.white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.PendingTreatment, R.drawable.ic_friends, R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.matrial_Stastics, R.drawable.ic_nearby, R.color.white);

        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item2);
        bottomNavigationItems.add(item3);
        bottomNavigation.addItems(bottomNavigationItems);
        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.white));
        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setColored(true);
        bottomNavigation.setForceTint(false);
        bottomNavigation.setAccentColor(getResources().getColor(R.color.base_color));
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.gray));
        bottomNavigation.setForceTitlesDisplay(true);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, final boolean wasSelected) {
                showHideFragment(mFragments[position], mFragments[bottomNavigationPreposition]);
                bottomNavigationPreposition = position;
                if (wasSelected) {
                    BaseApplication.bus.post(new EventData(position));
                    KLog.e("position:" + position);
                }

                fl_container.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!wasSelected && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            int cx = (fl_container.getLeft() + fl_container.getRight()) / 2;
                            int cy = fl_container.getBottom();
                            int radius = Math.max(fl_container.getWidth(), fl_container.getHeight());
                            Animator mAnimator = ViewAnimationUtils.createCircularReveal(fl_container, cx, cy, 0, radius);
                            mAnimator.setDuration(300);
                            mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                            mAnimator.start();
                        }
                    }
                });
            }
        });
        int currentItem = 0;
        bottomNavigation.setCurrentItem(currentItem);
    }

    private void initPopuwindow(PopupWindow mPopupWindow) {
        mPopupWindow.setHeight(getWindowManager().getDefaultDisplay().getHeight());
        mPopupWindow.setWidth(getWindowManager().getDefaultDisplay().getWidth() * 2 / 3);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.material_grey_1100));
        mPopupWindow.setBackgroundDrawable(dw);


    }

    public void showPopuwindow(View parent, View aidView) {
        KLog.e("---showPopuwindow---");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupwindow = inflater.inflate(R.layout.popupwindow, null);
        mPopupWindow = new PopupWindow(popupwindow);
        initPopuwindow(mPopupWindow);
        mPopupWindow.setContentView(aidView);
        fl_popupwindow = (FrameLayout) popupwindow.findViewById(R.id.fl_popupwindow);
        KLog.e("---fl_popupwindow---");
//        fl_popupwindow.addView(aidView);
        KLog.e("---addView---");
        mPopupWindow.showAtLocation(parent, Gravity.RIGHT, 0, 0);
    }

    public void startDrawerActivity(@Nullable ParametersData mParametersData, @Nullable DepartmentBean departmentData) {
        Intent intent = new Intent(this, DrawerActivity.class);
        Bundle bundle = new Bundle();
        if (mParametersData != null && departmentData == null) {
            bundle.putSerializable("mparametersData", mParametersData);
        } else if (mParametersData == null && departmentData != null) {
            bundle.putSerializable("departmentData", departmentData);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }



    public void startDetailActivity(@Nullable DepartmentBean departmentData) {
        Intent intent = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("departmentData", departmentData);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
