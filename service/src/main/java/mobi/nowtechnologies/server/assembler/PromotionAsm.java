package mobi.nowtechnologies.server.assembler;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.admin.PromotionDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PromotionAsm {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionAsm.class);


    @SuppressWarnings("unchecked")
    public static List<PromotionDto> toPromotionDtos(Collection<Promotion> promotions) {
        LOGGER.debug("input parameters promotions: [{}]", promotions);

        final List<PromotionDto> promotionDtos;
        if (promotions.isEmpty()) {
            promotionDtos = Collections.EMPTY_LIST;
        } else {
            promotionDtos = new ArrayList<PromotionDto>(promotions.size());
            for (Promotion promotion : promotions) {
                promotionDtos.add(toPromotionDto(promotion));
            }
        }

        LOGGER.info("Output parameter promotionDtos=[{}]", promotionDtos);
        return promotionDtos;

    }


    public static PromotionDto toPromotionDto(Promotion promotion) {
        LOGGER.debug("input parameters promotion: [{}]", promotion);

        PromotionDto promotionDto = new PromotionDto();

        promotionDto.setActive(promotion.getIsActive());
        promotionDto.setDescription(promotion.getDescription());
        promotionDto.setEndDate(Utils.getDateFromInt(promotion.getEndDate()));
        promotionDto.setFreeWeeks(promotion.getFreeWeeks());
        promotionDto.setI(promotion.getI());
        promotionDto.setLabel(promotion.getLabel());
        promotionDto.setMaxUsers(promotion.getMaxUsers());
        promotionDto.setNumUsers(promotion.getNumUsers());
        promotionDto.setShowPromotion(promotion.getShowPromotion());
        promotionDto.setStartDate(Utils.getDateFromInt(promotion.getStartDate()));
        promotionDto.setSubWeeks(promotion.getSubWeeks());
        promotionDto.setType(promotion.getType());

        LOGGER.info("Output parameter promotionDto=[{}]", promotionDto);
        return promotionDto;
    }
}
