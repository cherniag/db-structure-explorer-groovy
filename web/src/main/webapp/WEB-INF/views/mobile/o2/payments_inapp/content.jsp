<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script type="text/javascript">

var videoCheckbox = null;

$(document).ready( function(){
	videoCheckbox = new CheckboxElement();
	videoCheckbox.init();
	videoCheckbox.updateOptions();
} );

var CheckboxElement = (function(){
	
	var checkboxSelected = false;
	var checkboxId = "videoCheckbox";
	var initialized = false;
	var checkboxElem = null;
	
	updateOptions = function(){
		if ( !initialized ) return;
		
		var paymentButtons = $("div.rel");
		
		paymentButtons.each(function(){
			var attrib = $(this).attr("data-hasvideo");
			if ( typeof attrib === 'undefined' || attrib === false ) {
				return;
			}
			var videoAttr = (attrib == "1");
			
			if ( videoAttr == checkboxSelected ) {
				$(this).show();
			} else {
				$(this).hide();
			}
		});
	}
	
	init = function() {
		checkboxElem = $("#" + checkboxId);
		if ( checkboxElem.length === 0 ) {
			return;
		}
		
		checkboxSelected = checkboxElem.hasClass("button-on");
		
		initialized = true;
		
		updateOptions();
	}
	
	switchState = function() {
		if ( !initialized ) return;
		
		checkboxSelected = !checkboxSelected;
		
		if ( checkboxSelected === true ) {
			checkboxElem.removeClass("button-off");
			checkboxElem.addClass("button-on");
		} else {
			checkboxElem.removeClass("button-on");
			checkboxElem.addClass("button-off");
		}
		
		updateOptions();
	}
	
	return {
		init: init,
		updateOptions: updateOptions,
		switchState: switchState
	};
});
</script>

<div class="header pie">
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png"/></span>
</div>

<div style="margin: 0 14px">

<%--
<div class="container" style="padding-top:6px;">

            <c:set var="accountBannerON"> <s:message code="pays.page.note.account.on"/> </c:set>
            <c:set var="error_code" value="payment.${paymentDetails.paymentType}.error.msg.${paymentDetails.errorCode}" />
            <s:message code='${error_code}' var="error_msg" />
            <c:if test="${error_msg != error_code && error_msg != ''}">
    			<div class="pane-red rel pie">
                	<img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/icon_banner_alert.png"/>
                    <span class="alert-text-close">${error_msg}</span>
                    <span class="alert-button-close"/>
                </div>
            </c:if>
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
</div>
 --%>

<img style="width:100%;display: block" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/img_header_payment.png" />

<div class="container">
           
           <c:choose>
           <c:when test="${isIOSDevice eq true && isO2Consumer eq false}">
				<div style="padding-top: 16px; padding-left: 10px; padding-bottom: 20px">
					<c:set var="optionPrice" />
					<c:set var="iTunesUrl" />
					<c:forEach var="paymentPolicy" items="${paymentPolicies}">
						<c:if test="${isIOSDevice && !isO2User}">
							<c:set var="optionPrice" value="${paymentPolicy.subcost}" />
							<c:set var="iTunesUrl" value="${pageContext.request.contextPath}/payments_inapp/iTunesSubscription.html?paymentPolicyId=${paymentPolicy.id}" />
						</c:if>
					</c:forEach>
					
					<div style="font-family: frutigerRoman,Helvetica,Arial,sans-serif; font-size: 15px; color: #003399;">
							O2 Tracks Music - iTunes
					</div>
					
					<hr style="color: #ddd; margin: 10px 0 12px 0" />
					
					<div style="font-family: frutigerLight,Helvetica,Arial,sans-serif; color: #003399; font-size: 11px; line-height: 16px; margin-bottom: 17px;">The biggest hits. Brand new music. Exclusive playlists.<br />
					Celebrity gossip. Don't miss a beat.</div>
					
					<input class="button-turquoise no-margin pie" title="${iTunesUrl}" type="button" onClick="location.href=this.title" value="&#163;${optionPrice}/month" />
				</div>
           </c:when>
           <c:when test="${isO2User eq true and isBussinesUser eq false}">
				<div style="padding-top: 16px; padding-left: 10px;">
					<div style="font-family: frutigerRoman,Helvetica,Arial,sans-serif; font-size: 15px; color: #003399;">
						O2 Tracks Music  &#163;1.00 <span style="font-size: 12px">/ week</span>
					</div>
				</div>

				<c:if test="${userCanGetVideo eq true}">
	                <c:choose>
			        <%--we have 2 cases:
			        	(1) user is 4G and opted-in (we display the video options)
			        	(2) user is 4g and not opted-in (we display a "link" for the user to opt-in) --%>
			        <c:when test="${userIsOptedInToVideo eq true}">
			        
			        <div class="rel tapArea" style="margin-top: 20px">
			        	<a class="subscription-selector" href="javascript: void(0)" onclick="videoCheckbox.switchState()" type="button" style="height: 105px;padding: 12px 6px 6px 10px">
                          		<div>
                           		<img style="width:34px; height:32px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_video.png" />
                                <div class="rel" style="padding-top: 1px;">
                                	<div>
                                    	<div style="font-family: frutigerRoman,Helvetica,Arial,sans-serif; font-size: 15px; color: #003399;margin-bottom: 7px"><s:message code='pays.page.note.account.videotitle' /></div>
                                    	<div style="font-family: frutigerRoman,Helvetica,Arial,sans-serif; font-size: 15px; color: #3399cc;"><s:message code='pays.page.note.account.videoprice' /></div>
                                    </div>
                                </div>
                                <div style="clear: both">&nbsp;</div>
                               </div>
                               <div style="font-family: frutigerLight,Helvetica,Arial,sans-serif; font-size: 11px; line-height: 16px; color: #003399; margin-right: 70px;">For all the best music videos straight to your phone everyday. Only with O2. Only on 4G.</div>
                               
                               <c:set var="buttonClass" value="button-off" />
					        <c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated) && (paymentDetails.paymentPolicy.videoAndAudio4GSubscription==true)}">
					        	<c:set var="buttonClass" value="button-on" />
					        	<%-- Activate the video checkbox if the user has a video subscription --%>
					        </c:if>
					        <c:if test="${(paymentDetails==null) || (false==paymentDetails.activated)}">
			        			<c:set var="buttonClass" value="button-on" />
			        			<%--Activate the video checkbox if the user has no subscription --%>
			        		</c:if>
			                <span class="${buttonClass}" id="videoCheckbox"></span>
                      	</a>
		            </div>
			        </c:when>
			        <c:when test="${userIsOptedInToVideo eq false}">
			        	<div class="rel tapArea" style="margin-top: 20px">
			        		<div class="subscription-selector" style="height: 135px;padding: 12px 6px 6px 10px">
				           <!-- <a class="subscription-selector" href="videotrial.html?return_url=payments_inapp.html" type="button" style="height: 105px;padding: 12px 6px 6px 10px"> -->
				                <%-- <div class="rel" style="padding-top: 8px; text-align: center;">
				                	<span class="title"><s:message code='pays.select.payby.o2psms.videoOptIn' /></span>
				                </div> --%>
				                
				                <div>
                           		<img style="width:34px; height:32px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_video.png" />
                                <div class="rel" style="padding-top: 1px;">
                                	<div>
                                    	<div style="font-family: frutigerBold,Helvetica,Arial,sans-serif; font-size: 15px; color: #003399;">Congratulations</div>
                                    	<div style="font-family: frutigerBold,Helvetica,Arial,sans-serif; font-size: 15px; color: #003399;">You qualify for O2 Tracks Video</div>
                                    </div>
                                </div>
                                <div style="clear: both">&nbsp;</div>
                               </div>
                               <div style="font-family: frutigerLight,Helvetica,Arial,sans-serif; font-size: 11px; line-height: 16px; color: #003399; margin-right: 70px; margin-bottom: 10px">Why not upgrade now and get yourself a free trial<br />Tap the button bellow</div>
                               <input class="button-turquoise no-margin pie" title="${pageContext.request.contextPath}/videotrial.html?return_url=payments_inapp.html" type="button" onClick="location.href=this.title" value="Yes, I want a free trial" />
				           <!-- </a> -->
				           </div>
			            </div>
			        </c:when>
			        </c:choose>
			    </c:if>
			    
				<div style="padding-left: 10px; padding-bottom: 20px">
					<hr style="color: #ddd; margin: 10px 0 10px 0" />
					
					<div style="font-family: frutigerRoman,Helvetica,Arial,sans-serif; color: #003399; font-size: 15px; line-height: 16px">Billing options</div>
					<div style="font-family: frutigerLight,Helvetica,Arial,sans-serif; color: #003399; font-size: 11px; margin-top: 7px;">Pick a payment cycle to get started</div>
				</div>
           </c:when>
           <c:when test="${isBussinesOrNonO2User eq true}">
				<%--for business/non-o2 users --%>
				<c:set var="optionPrice" />
				<c:forEach var="paymentPolicy" items="${paymentPolicies}">
				<c:set var="optionPrice" value="${paymentPolicy.subcost}" />
				</c:forEach>
				<div style="padding-top: 16px; padding-left: 10px; padding-bottom: 20px">
					<div style="font-family: frutigerRoman,Helvetica,Arial,sans-serif; font-size: 15px; color: #003399;">
							O2 Tracks Music for &#163;<fmt:formatNumber pattern="0.00" value="${optionPrice}" /> <span style="font-size: 12px">/ month</span>
					</div>
					
					<hr style="color: #ddd; margin: 7px 0 9px 0" />
					
					<div style="font-family: frutigerLight,Helvetica,Arial,sans-serif; color: #003399; font-size: 11px; line-height: 16px">The biggest hits. Brand new music. Exclusive playlists.<br />
					Celebrity gossip. Don't miss a beat.</div>
				</div>
           </c:when>
           </c:choose>
           
				<c:if test="${not (isIOSDevice eq true && isO2Consumer eq false)}">
            	<c:set var="hasPaymentBaner" value="false" />
                <c:forEach var="paymentPolicy" items="${paymentPolicies}">
                    <c:if test="${paymentPolicy.paymentType == 'creditCard'}">
                        <c:set var="method_name" value="creditcard" />
                        <s:message code='pays.select.payby.creditcard' var="payment_label" />
                    </c:if>
                    <c:if test="${paymentPolicy.paymentType == 'PAY_PAL'}">
                        <c:set var="method_name" value="paypal" />
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

                    <c:choose>
                    	<c:when test="${paymentPolicy.subweeks == 3}">
                            <c:set var="paymentPolicyOptionNo" value="4" />
                        </c:when>
                        <c:when test="${paymentPolicy.subweeks == 1}">
                            <c:set var="paymentPolicyOptionNo" value="3" />
                        </c:when>
                        <c:when test="${paymentPolicy.subweeks == 2}">
                            <c:set var="paymentPolicyOptionNo" value="2" />
                        </c:when>
                        <c:when test="${paymentPolicy.subweeks == 5}">
                            <c:set var="paymentPolicyOptionNo" value="1" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="paymentPolicyOptionNo" value="0" />
                        </c:otherwise>
                    </c:choose>
                                        
                    <div class="rel tapArea" data-hasvideo="${paymentPolicy.videoAndAudio4GSubscription ? '1' : '0'}">
                        <c:choose>
                            <c:when test="${isIOSDevice && !isO2User}">
                            	<%--we do not need iTunes button here - this button will be at the top --%>
                                <%-- <c:if test="${paymentPolicy.paymentType == 'iTunesSubscription'}">
                                    <div class="subscription-container">
                                        <div class="subscription-selector option-3">
                                            <img style="width:66px; height:66px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_apple_in_app_payment.png" />
                                            <div class="rel" style="padding-top: 8px;">
                                                <span class="title">Payment via iTunes</span><br />
                                                <span class="price">&#163;<fmt:formatNumber pattern="0.00" value="${paymentPolicy.subcost}" /></span>per month
                                            </div>
                                        </div>
                                        
                                        <div class="rel" style="margin:0 6px; padding:8px 0; border-top: 1px solid #a0a0a0">
                                            <input class="button-turquoise no-margin pie" title="${pageContext.request.contextPath}/payments_inapp/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button" onClick="location.href=this.title" value="Subscribe via iTunes" />
                                        </div>
                                    </div>
                                </c:if> --%>
                            </c:when>
                            <c:when test="${paymentPolicy.paymentType == 'o2Psms'
                    && paymentDetails != null
                    && activePolicy != null
                    && paymentDetails.activated
                    && activePolicy.subcost == paymentPolicy.subcost
                    && activePolicy.subweeks == paymentPolicy.subweeks }">
                                <a class="subscription-selector option-${paymentPolicyOptionNo}" disabled="true">
                                	
                                	<c:set var="imageWeeks" value="${paymentPolicy.subweeks}" />
                                	<c:if test="${paymentPolicy.subweeks == 3}">
                                		<c:set var="imageWeeks" value="5" />
                                	</c:if>
                                	
                            		<img style="width:51px; height:51px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_option_${imageWeeks}.png" />
                                    <div class="rel" style="padding-top: 8px;">
                                        <span class="title"><s:message code='pays.select.payby.o2psms.option${paymentPolicyOptionNo}.title' /></span><br />
                                        <span class="price">&#163;<fmt:formatNumber pattern="0.00" value="${paymentPolicy.subcost}" /></span> <s:message code='pays.select.payby.o2psms.option${paymentPolicyOptionNo}.weeks' />
                                    </div>
                                    <span class="button-on"></span>
                        		</a>
                            </c:when>
                            <c:when test="${paymentPolicy.paymentType == 'o2Psms'}">
                            
                            	<c:set var="imageWeeks" value="${paymentPolicy.subweeks}" />
                               	<c:if test="${paymentPolicy.subweeks == 3}">
                               		<c:set var="imageWeeks" value="5" />
                               	</c:if>
                               	
                                <a class="subscription-selector option-${paymentPolicyOptionNo}" href="${pageContext.request.contextPath}/payments_inapp/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button">
                            		<img style="width:51px; height:51px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_option_${imageWeeks}.png" />
                                    <div class="rel" style="padding-top: 8px;">
                                        <div class="title"><s:message code='pays.select.payby.o2psms.option${paymentPolicyOptionNo}.title' /></div>
                                        <span class="price">&#163;<fmt:formatNumber pattern="0.00" value="${paymentPolicy.subcost}" /></span> <s:message code='pays.select.payby.o2psms.option${paymentPolicyOptionNo}.weeks' />
                                    </div>
                                    <span class="button-off"></span>
                        		</a>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${paymentPolicy.paymentType != 'iTunesSubscription'}">

                                <div class="subscription-container" style="margin-bottom: 5px;">
                                    <a class="subscription-selector option-3" style="margin-bottom: 0px;" href="${pageContext.request.contextPath}/payments_inapp/${method_name}.html?paymentPolicyId=${paymentPolicy.id}" type="button">
                                		<img style="width:51px; height:51px;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/ic_option_other.png" />
                                        <div class="rel" style="padding-top: 20px;">
                                            <span style="font-family: frutigerRoman,Helvetica,Arial,sans-serif; font-size: 15px"><s:message code="${payment_label}" /></span>
                                            <%-- <span class="price">&#163;<fmt:formatNumber pattern="0.00" value="${paymentPolicy.subcost}" /></span> 
                                            <c:choose>
                                                <c:when test="${isBussinesUser eq 'true'}">
                                                    <s:message code='pays.select.payby.creditcard.business.subterm' />
                                                </c:when>
                                                <c:otherwise>
                                                    <s:message code='pays.select.payby.creditcard.consumer.subterm' />
                                                </c:otherwise>
                                            </c:choose> --%>

                                            
                                        </div>
                                        <c:choose>
                                            <c:when test="${paymentDetailsType != null && paymentDetailsType == method_name && paymentDetails.activated eq 'true'}">
                                                <span class="button-on"></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="button-off"></span>
                                            </c:otherwise>
                                        </c:choose>
                            		</a>

                                    <div class="rel" style="margin:0 6px; padding-top:3px; border-top: 1px solid #a0a0a0; text-align: center;">
                                        <img style="height:13px;" src="${requestScope.assetsPathAccordingToCommunity}imgs/ic_${method_name}.png" />
                                    </div>
                                </div>
                                
                        		</c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>

                </c:forEach>
                <div style="height: 5px">&nbsp;</div><%--clearfix --%>
                </c:if>
            </div>

            <div class="content no-bg">

                <c:if test="${(paymentDetails!=null) && (true==paymentDetails.activated)}">
                    <div class="rel" style="margin-top: 5px;">
                        <a class="button-grey no-margin pie" href="${pageContext.request.contextPath}/payments_inapp/unsubscribe.html" ><s:message code='pays.deactivate.submit' /></a>
                    </div>
                </c:if>
                
                <div class="rel" style="text-align: center; margin-top: 10px;">
                    <img style="width:33%;" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/label_secure_payment.png"/>
                </div>

            </div>
</div>
