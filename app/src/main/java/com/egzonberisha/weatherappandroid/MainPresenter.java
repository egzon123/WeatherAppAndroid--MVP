package com.egzonberisha.weatherappandroid;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

public class MainPresenter extends MvpBasePresenter<MainMvpView> {

    private LoadCitiesTask loadCitiesTask;

    public void loadCities(){
        loadCitiesTask.execute();
    }

    @SuppressWarnings("deprecation")
    private void cancelLoadCitiesTaskIfRunning(){
        if(loadCitiesTask != null){
            loadCitiesTask.cancel();


        }
    }


    @SuppressWarnings("deprecation")
    // Called when Activity gets destroyed, so cancel running background task
    public void detachView(boolean retainPresenterInstance){
        super.detachView(retainPresenterInstance);
        if (!retainPresenterInstance){
            cancelLoadCitiesTaskIfRunning();
        }
    }


}
