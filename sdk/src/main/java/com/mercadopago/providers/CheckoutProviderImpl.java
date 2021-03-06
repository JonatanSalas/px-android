package com.mercadopago.providers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.constants.Sites;
import com.mercadopago.core.CustomServer;
import com.mercadopago.core.MercadoPagoServices;
import com.mercadopago.exceptions.CheckoutPreferenceException;
import com.mercadopago.exceptions.ExceptionHandler;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Campaign;
import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentBody;
import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.Site;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.PaymentPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.mercadopago.util.TextUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CheckoutProviderImpl implements CheckoutProvider {

    private final ServicePreference servicePreference;
    private final Context context;
    private final MercadoPagoServices mercadoPagoServices;
    private final String publicKey;

    public CheckoutProviderImpl(Context context, String publicKey, String privateKey, ServicePreference servicePreference) {
        if (TextUtils.isEmpty(publicKey) && TextUtils.isEmpty(privateKey)) {
            throw new IllegalStateException("Credentials not set");
        } else if (context == null) {
            throw new IllegalStateException("Context not context");
        }
        this.context = context;
        this.servicePreference = servicePreference;
        this.publicKey = publicKey;

        mercadoPagoServices = new MercadoPagoServices.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .setPrivateKey(privateKey)
                .setServicePreference(servicePreference)
                .build();
    }

    @Override
    public void getCheckoutPreference(String checkoutPreferenceId, final OnResourcesRetrievedCallback<CheckoutPreference> onResourcesRetrievedCallback) {
        mercadoPagoServices.getPreference(checkoutPreferenceId, new Callback<CheckoutPreference>() {
            @Override
            public void success(CheckoutPreference checkoutPreference) {
                onResourcesRetrievedCallback.onSuccess(checkoutPreference);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException));
            }
        });
    }

    @Override
    public void getDiscountCampaigns(final OnResourcesRetrievedCallback<List<Campaign>> callback) {
        mercadoPagoServices.getCampaigns(new Callback<List<Campaign>>() {
            @Override
            public void success(List<Campaign> campaigns) {
                callback.onSuccess(campaigns);
            }

            @Override
            public void failure(ApiException apiException) {
                callback.onFailure(new MercadoPagoError(apiException));
            }
        });
    }

    @Override
    public void getDirectDiscount(BigDecimal amount, String payerEmail, OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        if (servicePreference != null && servicePreference.hasGetDiscountURL()) {
            getDiscountFromMerchantServer(amount, payerEmail, onResourcesRetrievedCallback);
        } else {
            getDiscountFromMercadoPago(amount, payerEmail, onResourcesRetrievedCallback);
        }
    }

    @Override
    public void getPaymentMethodSearch(BigDecimal amount, final List<String> excludedPaymentTypes, final List<String> excludedPaymentMethods, Payer payer, Site site, final OnResourcesRetrievedCallback<PaymentMethodSearch> onPaymentMethodSearchRetrievedCallback, final OnResourcesRetrievedCallback<Customer> onCustomerRetrievedCallback) {

        Set<String> excludedPaymentTypesSet = new HashSet<>(excludedPaymentTypes);
        excludedPaymentTypesSet.addAll(getUnsupportedPaymentTypes(site));

        mercadoPagoServices.getPaymentMethodSearch(amount, new ArrayList<>(excludedPaymentTypesSet), excludedPaymentMethods, payer, site, new Callback<PaymentMethodSearch>() {
            @Override
            public void success(final PaymentMethodSearch paymentMethodSearch) {
                if (servicePreference != null && servicePreference.hasGetCustomerURL()) {
                    attachCustomerCardsFromMerchantServer(paymentMethodSearch, excludedPaymentTypes, excludedPaymentMethods, onPaymentMethodSearchRetrievedCallback, onCustomerRetrievedCallback);
                } else {
                    onPaymentMethodSearchRetrievedCallback.onSuccess(paymentMethodSearch);
                }
            }

            @Override
            public void failure(ApiException apiException) {
                onPaymentMethodSearchRetrievedCallback.onFailure(new MercadoPagoError(apiException));
            }
        });
    }

    private void attachCustomerCardsFromMerchantServer(final PaymentMethodSearch paymentMethodSearch, final List<String> excludedPaymentTypes, final List<String> excludedPaymentMethods, final OnResourcesRetrievedCallback<PaymentMethodSearch> onPaymentMethodSearchRetrievedCallback, final OnResourcesRetrievedCallback<Customer> onCustomerRetrievedCallback) {
        CustomServer.getCustomer(context, servicePreference.getGetCustomerURL(), servicePreference.getGetCustomerURI(), servicePreference.getGetCustomerAdditionalInfo(), new Callback<Customer>() {
            @Override
            public void success(Customer customer) {
                PaymentPreference paymentPreference = new PaymentPreference();
                paymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);
                paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethods);
                List<Card> savedCards = paymentPreference.getValidCards(customer.getCards());
                paymentMethodSearch.setCards(savedCards, context.getString(R.string.mpsdk_last_digits_label));

                onCustomerRetrievedCallback.onSuccess(customer);
                onPaymentMethodSearchRetrievedCallback.onSuccess(paymentMethodSearch);
            }

            @Override
            public void failure(ApiException apiException) {
                onCustomerRetrievedCallback.onFailure(new MercadoPagoError(apiException));

                //Return payment method search to avoid failure due to merchant server
                onPaymentMethodSearchRetrievedCallback.onSuccess(paymentMethodSearch);
            }
        });
    }

    private void getDiscountFromMerchantServer(BigDecimal amount, String payerEmail, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        Map<String, Object> discountInfoMap = new HashMap<>();
        discountInfoMap.putAll(servicePreference.getGetDiscountAdditionalInfo());

        CustomServer.getDirectDiscount(context, amount.toString(), payerEmail, servicePreference.getGetMerchantDiscountBaseURL(), servicePreference.getGetMerchantDiscountURI(), servicePreference.getGetDiscountAdditionalInfo(), new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                onResourcesRetrievedCallback.onSuccess(discount);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException));
            }
        });
    }

    private void getDiscountFromMercadoPago(BigDecimal amount, String payerEmail, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        mercadoPagoServices.getDirectDiscount(amount.toString(), payerEmail, new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                onResourcesRetrievedCallback.onSuccess(discount);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException));
            }
        });
    }

    @Override
    public String getCheckoutExceptionMessage(CheckoutPreferenceException exception) {
        return ExceptionHandler.getErrorMessage(context, exception);
    }

    @Override
    public String getCheckoutExceptionMessage(IllegalStateException exception) {
        return context.getString(R.string.mpsdk_standard_error_message);
    }

    @Override
    public void createPayment(String transactionId, CheckoutPreference checkoutPreference, PaymentData paymentData, Boolean binaryMode, String customerId, OnResourcesRetrievedCallback<Payment> onResourcesRetrievedCallback) {
        if (servicePreference != null && servicePreference.hasCreatePaymentURL()) {
            createPaymentInMerchantServer(transactionId, paymentData, onResourcesRetrievedCallback);
        } else {
            createPaymentInMercadoPago(transactionId, checkoutPreference, paymentData, binaryMode, customerId, onResourcesRetrievedCallback);
        }
    }

    private void createPaymentInMerchantServer(String transactionId, PaymentData paymentData, final OnResourcesRetrievedCallback<Payment> onResourcesRetrievedCallback) {
        Map<String, Object> paymentInfoMap = new HashMap<>();
        paymentInfoMap.putAll(servicePreference.getCreatePaymentAdditionalInfo());

        MerchantPayment merchantPayment = new MerchantPayment(paymentData);
        String payLoadJson = JsonUtil.getInstance().toJson(merchantPayment);

        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> paymentDataMap = new Gson().fromJson(payLoadJson, type);

        paymentInfoMap.putAll(paymentDataMap);

        CustomServer.createPayment(context, transactionId, servicePreference.getCreatePaymentURL(), servicePreference.getCreatePaymentURI(), paymentInfoMap, new HashMap<String, String>(), new Callback<Payment>() {
            @Override
            public void success(Payment payment) {
                onResourcesRetrievedCallback.onSuccess(payment);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException));
            }
        });
    }

    private void createPaymentInMercadoPago(String transactionId, CheckoutPreference checkoutPreference, PaymentData paymentData, Boolean binaryMode, String customerId, final OnResourcesRetrievedCallback<Payment> onResourcesRetrievedCallback) {
        PaymentBody paymentBody = createPaymentBody(transactionId, checkoutPreference, paymentData, binaryMode, customerId);
        mercadoPagoServices.createPayment(paymentBody, new Callback<Payment>() {
            @Override
            public void success(Payment payment) {
                onResourcesRetrievedCallback.onSuccess(payment);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException));
            }
        });
    }

    private PaymentBody createPaymentBody(String transactionId, CheckoutPreference checkoutPreference, PaymentData paymentData, Boolean binaryMode, String customerId) {
        PaymentBody paymentBody = new PaymentBody();
        paymentBody.setPrefId(checkoutPreference.getId());
        paymentBody.setPublicKey(publicKey);
        paymentBody.setPaymentMethodId(paymentData.getPaymentMethod().getId());
        paymentBody.setBinaryMode(binaryMode);
        Payer payer = checkoutPreference.getPayer();
        if (!TextUtils.isEmpty(customerId) && MercadoPagoUtil.isCard(paymentData.getPaymentMethod().getPaymentTypeId())) {
            payer.setId(customerId);
        }

        paymentBody.setPayer(payer);

        if (paymentData.getToken() != null) {
            paymentBody.setTokenId(paymentData.getToken().getId());
        }
        if (paymentData.getPayerCost() != null) {
            paymentBody.setInstallments(paymentData.getPayerCost().getInstallments());
        }
        if (paymentData.getIssuer() != null) {
            paymentBody.setIssuerId(paymentData.getIssuer().getId());
        }

        Discount discount = paymentData.getDiscount();
        if (discount != null) {
            paymentBody.setCampaignId(discount.getId().intValue());
            paymentBody.setCouponAmount(discount.getCouponAmount().floatValue());
            paymentBody.setCouponCode(paymentData.getDiscount().getCouponCode());
        }

        paymentBody.setTransactionId(transactionId);
        return paymentBody;
    }

    private List<String> getUnsupportedPaymentTypes(Site site) {

        List<String> unsupportedTypesForSite = new ArrayList<>();
        if (Sites.CHILE.getId().equals(site.getId())
                || Sites.VENEZUELA.getId().equals(site.getId())
                || Sites.COLOMBIA.getId().equals(site.getId())) {

            unsupportedTypesForSite.add(PaymentTypes.TICKET);
            unsupportedTypesForSite.add(PaymentTypes.ATM);
            unsupportedTypesForSite.add(PaymentTypes.BANK_TRANSFER);
        }
        return unsupportedTypesForSite;
    }
}