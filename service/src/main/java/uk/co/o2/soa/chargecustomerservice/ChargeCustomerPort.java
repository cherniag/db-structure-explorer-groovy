
package uk.co.o2.soa.chargecustomerservice;

import java.math.BigInteger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import uk.co.o2.soa.chargecustomerdata.ServiceResult;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "ChargeCustomerPort", targetNamespace = "http://soa.o2.co.uk/chargecustomerservice_1")
@XmlSeeAlso({
    uk.co.o2.soa.chargecustomerdata.ObjectFactory.class,
    uk.co.o2.soa.coredata.ObjectFactory.class
})
public interface ChargeCustomerPort {


    /**
     * 
     * @param contentDescription
     * @param priceNet
     * @param subMerchantId
     * @param promotionCode
     * @param debitCredit
     * @param msisdn
     * @param smsNotify
     * @param contentCategory
     * @param contentType
     * @param smsMessage
     * @param applicationReference
     * @param priceGross
     * @return
     *     returns uk.co.o2.soa.chargecustomerdata_1.ServiceResult
     * @throws BillSubscriberFault
     */
    @WebMethod
    @WebResult(name = "result", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
    @RequestWrapper(localName = "billSubscriber", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1", className = "uk.co.o2.soa.chargecustomerdata.BillSubscriber")
    @ResponseWrapper(localName = "billSubscriberResponse", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1", className = "uk.co.o2.soa.chargecustomerdata.BillSubscriberResponse")
    public ServiceResult billSubscriber(
        @WebParam(name = "msisdn", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
        String msisdn,
        @WebParam(name = "subMerchantId", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
        String subMerchantId,
        @WebParam(name = "priceGross", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
        BigInteger priceGross,
        @WebParam(name = "priceNet", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
        BigInteger priceNet,
        @WebParam(name = "debitCredit", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
        String debitCredit,
        @WebParam(name = "contentCategory", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
        String contentCategory,
        @WebParam(name = "contentType", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
        String contentType,
        @WebParam(name = "contentDescription", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
        String contentDescription,
        @WebParam(name = "applicationReference", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
        String applicationReference,
        @WebParam(name = "smsNotify", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
        boolean smsNotify,
        @WebParam(name = "smsMessage", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
        String smsMessage,
        @WebParam(name = "promotionCode", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
        String promotionCode)
        throws BillSubscriberFault
    ;

}
