package com.mercadopago.px_tracking.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 6/5/17.
 */

public class ErrorEvent extends Event {

    private String errorClass;
    private String errorMessage;
    @SerializedName("stacktrace")
    private List<StackTraceInfo> stackTraceList;

    public ErrorEvent() {
        super();
        setType(TYPE_ERROR);
        setTimestamp(new Timestamp(System.currentTimeMillis()));
        stackTraceList = new ArrayList<>();
    }

    public String getErrorClass() {
        return errorClass;
    }

    public void setErrorClass(String errorClass) {
        this.errorClass = errorClass;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<StackTraceInfo> getStackTraceList() {
        return stackTraceList;
    }

    public void setStackTraceList(List<StackTraceInfo> stackTraceList) {
        this.stackTraceList = stackTraceList;
    }
}
