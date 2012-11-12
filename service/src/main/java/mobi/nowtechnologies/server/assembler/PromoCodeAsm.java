package mobi.nowtechnologies.server.assembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.shared.dto.admin.PromoCodeDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class PromoCodeAsm {
	private static final Logger LOGGER = LoggerFactory.getLogger(PromoCodeAsm.class);

	@SuppressWarnings("unchecked")
	public static List<PromoCodeDto> toPromoCodes(Collection<PromoCode> promoCodes) {
		LOGGER.debug("input parameters promoCodes: [{}]", promoCodes);

		final List<PromoCodeDto> promoCodeDtos;
		if (promoCodes.isEmpty()) {
			promoCodeDtos = Collections.EMPTY_LIST;
		} else {
			promoCodeDtos = new ArrayList<PromoCodeDto>(promoCodes.size());
			for (PromoCode promoCode : promoCodes) {
				promoCodeDtos.add(toPromoCode(promoCode));
			}
		}

		LOGGER.info("Output parameter promoCodeDtos=[{}]", promoCodeDtos);
		return promoCodeDtos;
	}

	private static PromoCodeDto toPromoCode(PromoCode promoCode) {
		LOGGER.debug("input parameters promoCode: [{}]", promoCode);
		
		PromoCodeDto promoCodeDto = new PromoCodeDto();
		
		promoCodeDto.setCode(promoCode.getCode());
		promoCodeDto.setId(promoCode.getId());
		promoCodeDto.setPromotionDto(PromotionAsm.toPromotionDto(promoCode.getPromotion()));

		LOGGER.info("Output parameter promoCodeDto=[{}]", promoCodeDto);
		return promoCodeDto;
	}

}
