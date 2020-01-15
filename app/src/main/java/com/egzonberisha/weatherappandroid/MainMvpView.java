package com.egzonberisha.weatherappandroid;

import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface MainMvpView extends MvpView {

    void showLoactionDenied();
    void setUpTabLayout();
}
