package mobi.nowtechnologies.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Territory extends AbstractEntity {

	@Basic(optional = false)
	protected String Code;

	@Basic(optional = false)
	protected String Distributor;

	@Basic(optional = true)
	protected String Currency;

	@Basic(optional = true)
	protected Float Price;

	@Basic(optional = true)
	protected String PriceCode;

	@Temporal(TemporalType.DATE)
	@Basic(optional = true)
	protected Date StartDate;

	@Basic(optional = true)
	protected String ReportingId;

	@Basic(optional = true)
	protected String DealReference;

	@Basic(optional = false)
	protected String Label;
	
	@Basic(optional = false)
	protected String Publisher;
	
	@Basic(optional = false)
	protected boolean deleted;
	
	@Temporal(TemporalType.DATE)
	@Basic(optional = true)
	protected Date DeleteDate;

	@Temporal(TemporalType.DATE)
	@Basic(optional = true)
	protected Date CreateDate;



	@ManyToOne
	@JoinColumn(name = "TrackId", insertable = false, updatable = false)
	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getDistributor() {
		return Distributor;
	}

	public void setDistributor(String distributor) {
		Distributor = distributor;
	}

	public String getCurrency() {
		return Currency;
	}

	public void setCurrency(String currency) {
		Currency = currency;
	}

	public Float getPrice() {
		return Price;
	}

	public void setPrice(Float price) {
		Price = price;
	}

	public Date getStartDate() {
		return StartDate;
	}

	public void setStartDate(Date startDate) {
		StartDate = startDate;
	}

	public String getReportingId() {
		return ReportingId;
	}

	public void setReportingId(String reportingId) {
		ReportingId = reportingId;
	}

	public String getLabel() {
		return Label;
	}

	public void setLabel(String label) {
		Label = label;
	}

	public String getPriceCode() {
		return PriceCode;
	}

	public void setPriceCode(String priceCode) {
		PriceCode = priceCode;
	}

	public String getDealReference() {
		return DealReference;
	}

	public void setDealReference(String dealReference) {
		DealReference = dealReference;
	}

	public String getPublisher() {
		return Publisher;
	}

	public void setPublisher(String publisher) {
		Publisher = publisher;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getDeleteDate() {
		return DeleteDate;
	}

	public void setDeleteDate(Date deleteDate) {
		DeleteDate = deleteDate;
	}

	public Date getCreateDate() {
		return CreateDate;
	}

	public void setCreateDate(Date createDate) {
		CreateDate = createDate;
	}

}
