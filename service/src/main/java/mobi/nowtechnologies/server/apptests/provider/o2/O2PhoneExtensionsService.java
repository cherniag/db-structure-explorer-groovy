package mobi.nowtechnologies.server.apptests.provider.o2;

import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ContractChannel;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.InitializingBean;

/**
 * Author: Gennadii Cherniaiev Date: 7/3/2014
 */
public class O2PhoneExtensionsService implements InitializingBean {

    private Map<PhoneDataKey, Integer> phoneExtensions = new HashMap<PhoneDataKey, Integer>();

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("valid,exception,provider,segment,contract,tariff,channel=number");
        logger.info("---------------------------------------------------------------");

        final int startExt = 10;

        int i = startExt;
        for (ProviderType providerType : new ProviderType[] {ProviderType.O2, ProviderType.NON_O2}) {
            for (SegmentType segmentType : SegmentType.values()) {
                for (Contract contract : Contract.values()) {
                    for (Tariff tariff : Tariff.values()) {
                        for (ContractChannel contractChannel : ContractChannel.values()) {
                            final PhoneDataKey key = new PhoneDataKey(true, false, providerType, segmentType, contract, tariff, contractChannel);
                            phoneExtensions.put(key, i);
                            logger.info(key.toString() + "=" + i);
                            i++;
                        }
                    }
                }
            }
        }
    }

    public Integer getPhoneNumberSuffix(ProviderType providerType, SegmentType segmentType, Contract contract, Tariff tariff, ContractChannel contractChannel) {
        return phoneExtensions.get(new PhoneDataKey(true, false, providerType, segmentType, contract, tariff, contractChannel));
    }

    public PhoneDataKey getDataBySuffix(int suffix) {
        for (Map.Entry<PhoneDataKey, Integer> phoneDataKeyIntegerEntry : phoneExtensions.entrySet()) {
            if (phoneDataKeyIntegerEntry.getValue().equals(suffix)) {
                return phoneDataKeyIntegerEntry.getKey();
            }
        }
        throw new RuntimeException("There is no PhoneDataKey for suffix " + suffix);
    }


    public static class PhoneDataKey {

        boolean valid;
        boolean exception;
        ProviderType providerType;
        SegmentType segmentType;
        Contract contract;
        Tariff tariff;
        ContractChannel contractChannel;

        private PhoneDataKey(boolean valid, boolean exception, ProviderType providerType, SegmentType segmentType, Contract contract, Tariff tariff, ContractChannel contractChannel) {
            this.valid = valid;
            this.exception = exception;
            this.providerType = providerType;
            this.segmentType = segmentType;
            this.contract = contract;
            this.tariff = tariff;
            this.contractChannel = contractChannel;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            PhoneDataKey that = (PhoneDataKey) o;

            if (exception != that.exception) {
                return false;
            }
            if (valid != that.valid) {
                return false;
            }
            if (contract != that.contract) {
                return false;
            }
            if (contractChannel != that.contractChannel) {
                return false;
            }
            if (providerType != that.providerType) {
                return false;
            }
            if (segmentType != that.segmentType) {
                return false;
            }
            if (tariff != that.tariff) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = providerType.hashCode();
            result = 31 * result + segmentType.hashCode();
            result = 31 * result + contract.hashCode();
            result = 31 * result + tariff.hashCode();
            result = 31 * result + contractChannel.hashCode();
            result = 31 * result + (valid ?
                                    1 :
                                    0);
            result = 31 * result + (exception ?
                                    1 :
                                    0);
            return result;
        }

        @Override
        public String toString() {
            return "[" + valid + "," + exception + "," + providerType + "," + segmentType + "," + contract + "," + tariff + "," + contractChannel + ']';
        }
    }

}
