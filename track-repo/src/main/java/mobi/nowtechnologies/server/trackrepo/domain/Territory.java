package mobi.nowtechnologies.server.trackrepo.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Territory extends AbstractEntity {
	public static final String WWW_TERRITORY = "worldwide";
	public static final String GB_TERRITORY = "gb";

	@Basic(optional = false)
	@Column(name = "Code")
	protected String code;

	@Basic(optional = false)
	@Column(name = "Distributor")
	protected String distributor;

	@Basic(optional = true)
	@Column(name = "Currency")
	protected String currency;

	@Basic(optional = true)
	@Column(name = "Price")
	protected Float price;

	@Basic(optional = true)
	@Column(name = "PriceCode")
	protected String priceCode;

	@Temporal(TemporalType.DATE)
	@Basic(optional = true)
	@Column(name = "StartDate")
	protected Date startDate;

	@Basic(optional = true)
	@Column(name = "ReportingId")
	protected String reportingId;

	@Basic(optional = true)
	@Column(name = "DealReference")
	protected String dealReference;

	@Basic(optional = false)
	@Column(name = "Label")
	protected String label;

	@Basic(optional = false)
	@Column(name = "Publisher")
	protected String publisher;

	@Basic(optional = false)
	@Column(name = "Deleted")
	protected boolean deleted;

	@Temporal(TemporalType.DATE)
	@Basic(optional = true)
	@Column(name = "DeleteDate")
	protected Date deleteDate;

	@Temporal(TemporalType.DATE)
	@Basic(optional = true)
	@Column(name = "CreateDate")
	protected Date createDate;

	@ManyToOne
	@JoinColumn(name = "TrackId", insertable = false, updatable = false)
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
		return "Territory [code=" + code + ", ditributor=" + distributor + ", currency=" + currency + ", price=" + price
				+ ", priceCode=" + priceCode + ", startDate=" + startDate + ", reportingId=" + reportingId + ", dealReference=" + dealReference
				+ ", label=" + label + super.toString() + "]";
	}
}