package com.mercadopago.presenters;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.MvpPresenter;
import com.mercadopago.providers.AdditionalStepVaultProviderImpl;
import com.mercadopago.views.AdditionalStepVaultActivityView;

/**
 * Created by marlanti on 3/23/17.
 */

public class AdditionalStepVaultPresenter extends MvpPresenter<AdditionalStepVaultActivityView, AdditionalStepVaultProviderImpl> {


    private Site mSite;
    private PaymentMethod mPaymentMethod;
    private String mPublicKey;


    public void setSite(Site mSite) {
        this.mSite = mSite;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }

    public void setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
    }

    public void validateActivityParameters() throws IllegalStateException {
        if (mPaymentMethod == null) {
            getView().onInvalidStart("payment method is null");
        } else if (mPublicKey == null) {
            getView().onInvalidStart("public key not set");
        } else if (mSite == null) {
            getView().onInvalidStart("site not set");
        } else {
            getView().onValidStart();
        }
    }

    public void checkFlow() {

        //TODO agregar ifIdentificationRequired para MLU

        if (isEntityTypeStepRequired()) {
            getView().startIdentificationStep();

        } else if (isFinancialInstitutionsStepRequired()) {
            getView().startFinancialInstitutionsStep();

        }else{
            getView().onInvalidStart("No additional step found");
        }


    }

    public boolean isEntityTypeStepRequired() {
        return isEntityTypeRequired();
    }


    public boolean isFinancialInstitutionsStepRequired() {

        if (isPaymentMethodSelected()) {
            return mPaymentMethod.isFinancialInstitutionsRequired();
        }
        return false;
    }


    private boolean isEntityTypeRequired() {
        if (isPaymentMethodSelected()) {
            return mPaymentMethod.isEntityTypeRequired();
        }

        return false;
    }

    private boolean isPaymentMethodSelected() {
        return mPaymentMethod != null;
    }

    public Site getSite() {
        return mSite;
    }

    public PaymentMethod getmPaymentMethod() {
        return mPaymentMethod;
    }

    public String getmPublicKey() {
        return mPublicKey;
    }


    public void checkFlowWithIdentificationSelected() {

        if (isEntityTypeStepRequired()) {
            getView().startEntityTypeStep();
        }else{
            //TODO
            getView().finishWithResult();
        }
    }

    public void checkFlowWithEntityTypeSelected() {
        if (isFinancialInstitutionsStepRequired()) {
            getView().startFinancialInstitutionsStep();
        } else {
           //TODO
            getView().finishWithResult();
        }
    }

    public void checkFlowWithFinancialInstitutionSelected() {
        getView().finishWithResult();
    }
}
