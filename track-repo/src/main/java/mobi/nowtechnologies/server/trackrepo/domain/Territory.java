package mobi.nowtechnologies.server.trackrepo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Temporal;
import static javax.persistence.InheritanceType.JOINED;
import static javax.persistence.TemporalType.DATE;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
@Entity
@Inheritance(strategy = JOINED)
public class Territory extends AbstractEntity {

    public static final String WWW_TERRITORY = "worldwide";
    public static final String GB_TERRITORY = "gb";

    @Column(name = "Code", nullable = false)
    protected String code;

    @Column(name = "Distributor", nullable = false)
    protected String distributor;

    @Column(name = "Currency")
    protected String currency;

    @Column(name = "Price")
    protected Float price;

    @Column(name = "PriceCode")
    protected String priceCode;

    @Temporal(DATE)
    @Column(name = "StartDate")
    protected Date startDate;

    @Column(name = "ReportingId")
    protected String reportingId;

    @Column(name = "DealReference")
    protected String dealReference;

    @Column(name = "Label", nullable = false)
    protected String label;

    @Column(name = "Publisher", nullable = false)
    protected String publisher;

    @Column(name = "Deleted")
    protected boolean deleted;

    @Temporal(DATE)
    @Column(name = "DeleteDate")
    protected Date deleteDate;

    @Temporal(DATE)
    @Column(name = "CreateDate")
    protected Date createDate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("code", code).append("distributor", distributor).append("currency", currency).append("price", price)
                                                            .append("priceCode", priceCode).append("startDate", startDate).append("reportingId", reportingId).append("dealReference", dealReference)
                                                            .append("label", label).append("publisher", publisher).append("deleted", deleted).append("deleteDate", deleteDate)
                                                            .append("createDate", createDate).toString();
    }
}