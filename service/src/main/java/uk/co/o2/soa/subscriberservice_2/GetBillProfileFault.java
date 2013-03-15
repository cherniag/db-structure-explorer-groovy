
package uk.co.o2.soa.subscriberservice_2;

import javax.xml.ws.WebFault;

import uk.co.o2.soa.coredata.SOAFaultType;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "getBillProfileFault", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
public class GetBillProfileFault
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private SOAFaultType faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public GetBillProfileFault(String message, SOAFaultType faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public GetBillProfileFault(String message, SOAFaultType faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: uk.co.o2.soa.coredata_1.SOAFaultType
     */
    public SOAFaultType getFaultInfo() {
        return faultInfo;
    }

}
