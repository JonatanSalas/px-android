package com.mercadopago.views;

import com.mercadopago.model.ApiException;
import com.mercadopago.model.EntityType;
import com.mercadopago.mvp.MvpView;

import java.util.List;

/**
 * Created by marlanti on 3/3/17.
 */

public interface EntityTypeActivityView extends MvpView {
    void onValidStart();
    void onInvalidStart(String message);
    void initializeEntityTypes(List<EntityType> entityTypesList);
    void showApiExceptionError(ApiException exception);
    void startErrorView(String message, String errorDetail);
    void showHeader();
    void showLoadingView();
    void stopLoadingView();
    void finishWithResult(EntityType entityType);

}
