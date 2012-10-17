package mobi.nowtechnologies.server.shared.dto.admin;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PromotionPaymentPolicyDto {
	
	private Long id;
	
	private BigDecimal subcost;
	
	private Integer subweeks;
	
	private PromotionDto promotionDto;
	
	private List<PaymentPolicyDto> paymentPoliciesDtos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getSubcost() {
		return subcost;
	}

	public void setSubcost(BigDecimal subcost) {
		this.subcost = subcost;
	}

	public Integer getSubweeks() {
		return subweeks;
	}

	public void setSubweeks(Integer subweeks) {
		this.subweeks = subweeks;
	}

	public PromotionDto getPromotionDto() {
		return promotionDto;
	}

	public void setPromotionDto(PromotionDto promotionDto) {
		this.promotionDto = promotionDto;
	}

	public List<PaymentPolicyDto> getPaymentPoliciesDtos() {
		return paymentPoliciesDtos;
	}

	public void setPaymentPoliciesDtos(List<PaymentPolicyDto> paymentPoliciesDtos) {
		this.paymentPoliciesDtos = paymentPoliciesDtos;
	}

	@Override
	public String toString() {
		return "PromotionPaymentPolicyDto [id=" + id + ", paymentPoliciesDtos=" + paymentPoliciesDtos + ", promotionDto=" + promotionDto + ", subcost="
				+ subcost + ", subweeks=" + subweeks + "]";
	}
}
