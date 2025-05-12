package com.jocata.loansystem.form;

public class ExternalServiceResponseForm<T> {

    private int statusCode;
    private String statusMessage;
    private int errorCode;
    private String errorDesc;
    private String txnId;
    private T data;

    public ExternalServiceResponseForm() {
    }

    public ExternalServiceResponseForm(int statusCode, String statusMessage, String txnId, T data) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.txnId = txnId;
        this.data = data;
    }

    public ExternalServiceResponseForm(int errorCode, String errorDesc, String txnId) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
        this.txnId = txnId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
