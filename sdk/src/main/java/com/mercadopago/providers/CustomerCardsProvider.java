package com.mercadopago.providers;

import com.mercadopago.model.Customer;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.mvp.ResourcesProvider;

/**
 * Created by mromar on 4/11/17.
 */

public interface CustomerCardsProvider extends ResourcesProvider {

    void getCustomer(OnResourcesRetrievedCallback<Customer> onResourcesRetrievedCallback);

    String getLastDigitsLabel();

    String getConfirmPromptYes();

    String getConfirmPromptNo();

    int getIconDialogAlert();
}
