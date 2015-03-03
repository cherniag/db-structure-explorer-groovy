package mobi.nowtechnologies.server.shared.dto.admin;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PromotionDto {

    private int i;

    private String description;

    private String type;

    private Date endDate;

    private byte freeWeeks;

    private boolean isActive;

    private int maxUsers;

    private int numUsers;

    private Date startDate;

    private byte subWeeks;

    private boolean showPromotion;

    private String label;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public byte getFreeWeeks() {
        return freeWeeks;
    }

    public void setFreeWeeks(byte freeWeeks) {
        this.freeWeeks = freeWeeks;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public byte getSubWeeks() {
        return subWeeks;
    }

    public void setSubWeeks(byte subWeeks) {
        this.subWeeks = subWeeks;
    }

    public boolean isShowPromotion() {
        return showPromotion;
    }

    public void setShowPromotion(boolean showPromotion) {
        this.showPromotion = showPromotion;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("i", i).append("description", description).append("type", type).append("endDate", endDate).append("freeWeeks", freeWeeks).append("isActive", isActive)
                                        .append("maxUsers", maxUsers).append("numUsers", numUsers).append("startDate", startDate).append("subWeeks", subWeeks).append("showPromotion", showPromotion)
                                        .append("label", label).toString();
    }
}
