package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.FinancialInstitutionsAdapter;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.controllers.CheckoutTimer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.listeners.RecyclerItemClickListener;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.FinancialInstitution;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.observers.TimerObserver;
import com.mercadopago.preferences.DecorationPreference;
import com.mercadopago.presenters.FinancialInstitutionsPresenter;
import com.mercadopago.uicontrollers.FontCache;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.ColorsUtil;
import com.mercadopago.util.ErrorUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.views.FinancialInstitutionsActivityView;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by marlanti on 3/13/17.
 */

public class FinancialInstitutionsActivity extends MercadoPagoBaseActivity implements FinancialInstitutionsActivityView, TimerObserver {

    private static final String DECORATION_PREFERENCE_BUNDLE = "mDecorationPreference";
    private static final String FINANCIAL_INSTITUTIONS_BUNDLE = "mFinancialInstitutions";
    private static final String PAYMENT_METHOD_BUNDLE = "mPaymentMethod";
    private static final String PUBLIC_KEY_BUNDLE = "mPublicKey";

    protected FinancialInstitutionsPresenter mPresenter;
    protected Activity mActivity;

    //View controls
    protected FinancialInstitutionsAdapter mFinancialInstitutionsAdapter;
    protected RecyclerView mFinancialInstitutionsRecyclerView;
    protected DecorationPreference mDecorationPreference;
    //Low Res View
    protected Toolbar mLowResToolbar;
    protected MPTextView mLowResTitleToolbar;
    protected MPTextView mTimerTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        if (savedInstanceState == null) {
            createPresenter();
            configurePresenter();
            getActivityParameters();
            setTheme();
            setContentView();
            mPresenter.validateActivityParameters();
        }

    }

    private void createPresenter() {
        if (mPresenter == null) {
            mPresenter = new FinancialInstitutionsPresenter(getBaseContext());
        }
    }

    private void configurePresenter() {
        mPresenter.setView(this);
    }

    private boolean isCustomColorSet() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    private void getActivityParameters() {

        String publicKey = getIntent().getStringExtra("merchantPublicKey");
        mDecorationPreference = JsonUtil.getInstance().fromJson(getIntent().getStringExtra("decorationPreference"), DecorationPreference.class);

        PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);

        List<FinancialInstitution> financialInstitutions = paymentMethod.getFinancialInstitutions();


        mPresenter.setPaymentMethod(paymentMethod);
        mPresenter.setPublicKey(publicKey);
        mPresenter.setFinancialInstitutions(financialInstitutions);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString(DECORATION_PREFERENCE_BUNDLE, JsonUtil.getInstance().toJson(mDecorationPreference));
        outState.putString(FINANCIAL_INSTITUTIONS_BUNDLE, JsonUtil.getInstance().toJson(mPresenter.getFinancialInstitutions()));
        outState.putString(PAYMENT_METHOD_BUNDLE, JsonUtil.getInstance().toJson(mPresenter.getPaymentMethod()));
        outState.putString(PUBLIC_KEY_BUNDLE, mPresenter.getPublicKey());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            createPresenter();
            configurePresenter();
            setContentView();
            mDecorationPreference = JsonUtil.getInstance().fromJson(savedInstanceState.getString(DECORATION_PREFERENCE_BUNDLE), DecorationPreference.class);
            mPresenter.setPaymentMethod(JsonUtil.getInstance().fromJson(savedInstanceState.getString(PAYMENT_METHOD_BUNDLE), PaymentMethod.class));
            mPresenter.setPublicKey(savedInstanceState.getString(PUBLIC_KEY_BUNDLE));

            List<FinancialInstitution> financialInstitutionList;
            try {
                Type listType = new TypeToken<List<FinancialInstitution>>() {
                }.getType();
                financialInstitutionList = JsonUtil.getInstance().getGson().fromJson(
                        savedInstanceState.getString(FINANCIAL_INSTITUTIONS_BUNDLE), listType);
            } catch (Exception ex) {
                financialInstitutionList = null;
            }

            mPresenter.setFinancialInstitutions(financialInstitutionList);
            setTheme();
            mPresenter.validateActivityParameters();
        }
    }

    private void setTheme(){
        if (isCustomColorSet()) {
            setTheme(R.style.Theme_MercadoPagoTheme_NoActionBar);
        }
    }


    public void setContentView() {
        setContentViewLowRes();
    }

    @Override
    public void onValidStart() {
        MPTracker.getInstance().trackScreen("FINANCIAL_INSTITUTIONS", "2", mPresenter.getPublicKey(),
                BuildConfig.VERSION_NAME, this);
        initializeViews();
        loadViews();
        hideHeader();
        decorate();
        showTimer();
        initializeAdapter();
        mPresenter.loadFinancialInstitutions();
    }

    private void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            CheckoutTimer.getInstance().addObserver(this);
            mTimerTextView.setVisibility(View.VISIBLE);
            mTimerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    @Override
    public void onInvalidStart(String message) {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private void setContentViewLowRes() {
        setContentView(R.layout.mpsdk_activity_financial_institutions_lowres);
    }


    private void initializeViews() {
        mFinancialInstitutionsRecyclerView = (RecyclerView) findViewById(R.id.mpsdkActivityFinancialInstitutionsView);
        mTimerTextView = (MPTextView) findViewById(R.id.mpsdkTimerTextView);

        mLowResToolbar = (Toolbar) findViewById(R.id.mpsdkRegularToolbar);

        mLowResToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLowResTitleToolbar = (MPTextView) findViewById(R.id.mpsdkTitle);

        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            Toolbar.LayoutParams marginParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginParams.setMargins(0, 0, 0, 0);
            mLowResTitleToolbar.setLayoutParams(marginParams);
            mLowResTitleToolbar.setTextSize(17);
            mTimerTextView.setTextSize(15);
        }

        mLowResToolbar.setVisibility(View.VISIBLE);

    }

    private void loadViews() {
        loadLowResViews();
    }

    private void initializeAdapter() {
        mFinancialInstitutionsAdapter = new FinancialInstitutionsAdapter(this, getDpadSelectionCallback());
        initializeAdapterListener(mFinancialInstitutionsAdapter, mFinancialInstitutionsRecyclerView);
    }

    protected OnSelectedCallback<Integer> getDpadSelectionCallback() {
        return new OnSelectedCallback<Integer>() {
            @Override
            public void onSelected(Integer position) {
                mPresenter.onItemSelected(position);
            }
        };
    }

    private void initializeAdapterListener(RecyclerView.Adapter adapter, RecyclerView view) {
        view.setAdapter(adapter);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mPresenter.onItemSelected(position);
                    }
                }));
    }

    @Override
    public void initializeFinancialInstitutions(List<FinancialInstitution> financialInstitutions) {
        mFinancialInstitutionsAdapter.addResults(financialInstitutions);
    }

    @Override
    public void showApiExceptionError(ApiException exception) {
        ApiUtil.showApiExceptionError(mActivity, exception);
    }

    @Override
    public void startErrorView(String message, String errorDetail) {
        ErrorUtil.startErrorActivity(mActivity, message, errorDetail, false);
    }

    private void loadLowResViews() {
        loadToolbarArrow(mLowResToolbar);
        mLowResTitleToolbar.setText(getString(R.string.mpsdk_financial_institutions_title));
        if (FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            mLowResTitleToolbar.setTypeface(FontCache.getTypeface(FontCache.CUSTOM_REGULAR_FONT));
        }
    }


    private void loadToolbarArrow(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });
        }
    }


    private void decorate() {
        if (isDecorationEnabled()) {
            decorateLowRes();
        }
    }

    private boolean isDecorationEnabled() {
        return mDecorationPreference != null && mDecorationPreference.hasColors();
    }

    private void decorateLowRes() {
        ColorsUtil.decorateLowResToolbar(mLowResToolbar, mLowResTitleToolbar, mDecorationPreference,
                getSupportActionBar(), this);
        if (mTimerTextView != null) {
            ColorsUtil.decorateTextView(mDecorationPreference, mTimerTextView, this);
        }
    }


    @Override
    public void showHeader() {
        mLowResToolbar.setVisibility(View.VISIBLE);
    }

    private void hideHeader() {
        mLowResToolbar.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingView() {
        mFinancialInstitutionsRecyclerView.setVisibility(View.GONE);
        LayoutUtil.showProgressLayout(this);
    }

    @Override
    public void stopLoadingView() {
        mFinancialInstitutionsRecyclerView.setVisibility(View.VISIBLE);
        LayoutUtil.showRegularLayout(this);
    }

    @Override
    public void finishWithResult(FinancialInstitution financialInstitution) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("financialInstitution", JsonUtil.getInstance().toJson(financialInstitution));
        setResult(RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.mpsdk_hold, R.anim.mpsdk_hold);
    }

    @Override
    public void onBackPressed() {
        MPTracker.getInstance().trackEvent("FINANCIAL_INSTITUTIONS", "BACK_PRESSED", "2", mPresenter.getPublicKey(),
                BuildConfig.VERSION_NAME, this);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mPresenter.recoverFromFailure();
            } else {
                setResult(resultCode, data);
                finish();
            }
        }
    }

    @Override
    public void onTimeChanged(String timeToShow) {
        mTimerTextView.setText(timeToShow);
    }

    @Override
    public void onFinish() {
        this.finish();
    }
}
