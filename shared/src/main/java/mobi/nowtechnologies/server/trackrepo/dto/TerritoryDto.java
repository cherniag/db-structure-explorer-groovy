package mobi.nowtechnologies.server.trackrepo.dto;


import java.util.Date;

public class TerritoryDto {

    protected String code;
    protected String distributor;
    protected String currency;
    protected Float price;
    protected String priceCode;
    protected java.util.Date startDate;
    protected String reportingId;
    protected String dealReference;
    protected String label;
    protected String publisher;
    protected boolean deleted;
    protected java.util.Date deleteDate;
    protected java.util.Date createDate;

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getPriceCode() {
        return priceCode;
    }

    public void setPriceCode(String priceCode) {
        this.priceCode = priceCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getReportingId() {
        return reportingId;
    }

    public void setReportingId(String reportingId) {
        this.reportingId = reportingId;
    }

    public String getDealReference() {
        return dealReference;
    }

    public void setDealReference(String dealReference) {
        this.dealReference = dealReference;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCode() {

        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "TerritoryDto{" +
                "code='" + code + '\'' +
                ", distributor='" + distributor + '\'' +
                ", currency='" + currency + '\'' +
                ", price=" + price +
                ", priceCode='" + priceCode + '\'' +
                ", startDate=" + startDate +
                ", reportingId='" + reportingId + '\'' +
                ", dealReference='" + dealReference + '\'' +
                ", label='" + label + '\'' +
                ", publisher='" + publisher + '\'' +
                ", deleted=" + deleted +
                ", deleteDate=" + deleteDate +
                ", createDate=" + createDate +
                "} " + super.toString();
    }
}
