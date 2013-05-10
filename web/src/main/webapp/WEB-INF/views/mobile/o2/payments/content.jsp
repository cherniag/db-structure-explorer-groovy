<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="header pie">
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png"/></span>
    <a href="${pageContext.request.contextPath}/account.html" class="button-small button-right pie"><s:message code='m.page.main.menu.close' /></a>
</div>
<div class="container">

            <c:set var="accountBannerON"> <s:message code="pays.page.note.account.on"/> </c:set>
            <c:if test="${accountBannerON eq 'true'}">
                <c:choose>
                    <c:when test="${trialExpiredOrLimited}">
                        <div class="pane-red rel pie">
                            <img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icon_banner_alert.png"/>
                            <span class="alert-text">${paymentAccountNotes}</span>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="banner-pane pie">
                            <c:if test="${not empty paymentAccountBanner}">
                                <img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}'/>${paymentAccountBanner}" align="middle"/>
                            </c:if>
                            <span>${paymentAccountNotes}</span>
                        </div>
                    </c:otherwise>
                </c:choose>

            </c:if>
            <div class="content">

                <h1>${paymentPoliciesHeader}</h1>
                <p>${paymentPoliciesNote}</p>
                <hr />
                <div class="setOfButtons">
                	<c:set var="hasPaymentBaner" value="false" />
                    <c:forEach var="paymentPolicy" items="${paymentPolicies}">
                        <c:if test="${paymentPolicy.paymentType == 'creditCard'}">
                            <c:set var="method_name" value="creditcard" />
                            <c:set var="hasPaymentBaner" value="true" />
                            <s:message code='pays.select.payby.creditcard' var="payment_label" />
                        </c:if>
                        <c:if test="${paymentPolicy.paymentType == 'PAY_PAL'}">
                            <c:set var="method_name" value="paypal" />
                            <c:set var="hasPaymentBaner" value="true" />
                            <s:message code='pays.select.payby.paypal' var="payment_label" />
                        </c:if>
                        <c:if test="${paymentPolicy.paymentType == 'PSMS'}">
                            <c:set var="method_name" value="psms" />
                            <s:message code='pays.select.payby.psms' var="payment_label" />
                        </c:if>
                        <c:if test="${paymentPolicy.paymentType == 'o2Psms'}">
                            <c:set var="method_name" value="o2psms" />
                            <s:message code='pays.select.payby.o2psms.${paymentPolicy.subweeks}weeks.${paymentPolicy.subcost}subcost' var="payment_label" />
                        </c:if>
                        <c:if test="${paymentPolicy.paymentType == 'iTunesSubscription'}">
                            <c:set var="method_name" value="iTunesSubscription" />
                            <s:message code='pays.select.iTunesSubscription' var="payment_label" />
                        </c:if>

                        <div class="rel">
                            <c:choose>
                                <c:when test="${isIOSDevice && !isO2User}">
                                    <c:if test="${paymentPolicy.paymentType == 'iTunesSubscription'}">
                                        <input class="button-turquoise pie" title="${pageContext.request.contextPath}/payments/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button" onClick="location.href=this.title" value="<s:message code="${payment_label}" />" />
                                        <span class="button-arrow"/>
                                    </c:if>
                                </c:when>
                                <c:when test="${paymentPolicy.paymentType == 'o2Psms'
                        && paymentDetails != null
                        && activePolicy != null
                        && paymentDetails.activated
                        && activePolicy.subcost == paymentPolicy.subcost
                        && activePolicy.subweeks == paymentPolicy.subweeks }">
                                    <a class="button-disabled pie" disabled="true">
                                		${payment_label}
                            			<span class="button-on"/>
                            		</a>
                                </c:when>
                                <c:when test="${paymentPolicy.paymentType == 'o2Psms'}">
                                    <a class="button-turquoise pie" href="${pageContext.request.contextPath}/payments/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button">
                                		${payment_label}
                                		<span class="button-off"/>
                            		</a>
                                </c:when>
                                <c:otherwise>
                                    <c:if test="${paymentPolicy.paymentType != 'iTunesSubscription'}">
                                		<input class="button-turquoise pie" title="${pageContext.request.contextPath}/payments/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" onClick="location.href=this.title" type="button" value="<s:message code="${payment_label}" />" />
                                		<span class="button-arrow"/>
                            		</c:if>
                                </c:otherwise>
                            </c:choose>
                        </div>

                    </c:forEach>
                    		<c:if test="${hasPaymentBaner}">
                            	<img class="centered" style="width: 100px; height: 15px; margin-top: 15px; margin-bottom: 15px" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/image_secure_payment.png"/>
                            	<hr/>
                            	<img class="centered" style="width: 100%; margin-top: 10px" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/banner_payment.png"/>
                           		<hr/>
                    		</c:if>
                </div>
                <c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated)}">
                    <div class="rel" >
                        <div class="cross-text"><span>  <s:message code="pays.deactivate.header" />  </span>  </div>
                        <a class="button-grey pie" href="${pageContext.request.contextPath}/payments/unsubscribe.html" ><s:message code='pays.deactivate.submit' /></a>
                    </div>
                </c:if>

            </div>
</div>
