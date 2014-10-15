package mobi.nowtechnologies.server.dto;


import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Titov Mykhaylo (titov)
 * 27.09.13 12:57
 */
public class ProviderUserDetails {
    public String operator;
    public String contract;

    public ProviderUserDetails withOperator(String operator){
        this.operator = operator;
        return this;
    }

    public ProviderUserDetails withContract(String contract){
        this.contract = contract;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("operator", operator)
                .append("contract", contract)
                .toString();
    }
}
