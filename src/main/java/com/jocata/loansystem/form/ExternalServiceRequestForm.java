package com.jocata.loansystem.form;

public class ExternalServiceRequestForm {
    private String txnId;
    private PanPayLoad panPayload;
    private AadharPayLoad aadharPayload;
    private CibilPayLoad cibilPayload;

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public PanPayLoad getPanPayload() {
        return panPayload;
    }

    public void setPanPayload(PanPayLoad panPayload) {
        this.panPayload = panPayload;
    }

    public AadharPayLoad getAadharPayload() {
        return aadharPayload;
    }

    public void setAadharPayload(AadharPayLoad aadharPayload) {
        this.aadharPayload = aadharPayload;
    }

    public CibilPayLoad getCibilPayload() {
        return cibilPayload;
    }

    public void setCibilPayload(CibilPayLoad cibilPayload) {
        this.cibilPayload = cibilPayload;
    }
}
