<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="contentContainer">
	<div class="verticalSpace"></div>
	<!-- top part of content -->
	<div class="content rel oneWideColumn rad7">	
							
		<!-- top part of content -->
		<h1 class="azHeader">Welcome To Your Account Zone
			<span>This is where you get the app. manage or upgrade your account and download any tracks that you have purchased. Enjoy! </span>
		</h1>				
		<!-- tabs  -->
		<%@ include file="/WEB-INF/views/www/default/menu.jsp"%>
		<!--end menu tabs for account navigation-->
		
		<div class="widerContainer boxWithBorder lessBottomPad">	
			<div class="wholePart">						
				<div class="details phoneRightBg">
					<form:form modelAttribute="creditCardDto" method="post">
							<input type="hidden" name="paymentPolicyId" value="${paymentPolicy.id}"/>
							<h2><s:message code="pay.cc.form.title" /></h2>
							<div class="holderDetails summary"> 
								<p class="narrow"><s:message code="pay.cc.preview.form.description" /></p>
								
								<h3 class="pink"><s:message code="pay.cc.preview.form.section.order.summary" /></h3>
								<!--promotion block of records-->
								<div class="oneLine">
									<div class="nameCell">1 Promo Subscription Pack:</div>
									<div class="valueCell">4 weeks subscription</div>							
								</div>								
								<div class="oneLine">
									<div class="nameCell">Total Cost:</div>
									<div class="valueCell">&pound;1.00</div>							
								</div>						
								<div class="clr"></div>
								<!--end promotion block of records-->
								
								<!-- Card details section -->
								<h3 class="pink"><s:message code="pay.cc.preview.form.section.card.details" /></h3>
								<div class="oneLine">
									<div class="nameCell"><s:message code="pay.cc.form.card.type" /></div>
									<div class="valueCell"><s:message code="${creditCardDto.cardType.code}"/></div>
									<form:hidden path="cardType" />
								</div>
								
								<div class="oneLine">
									<div class="nameCell"><s:message code="pay.cc.form.holder.info" /></div>
									<div class="valueCell">${creditCardDto.holderTitle.value} ${creditCardDto.holderFirstname} ${creditCardDto.holderLastname}</div>
								</div>
								
								<div class="oneLine">
									<div class="nameCell"><s:message code="pay.cc.form.card.number" /></div>
									<div class="valueCell">${creditCardDto.cardNumber}</div>
									<form:hidden path="cardNumber"/>
								</div>
								
								<div class="oneLine">
									<div class="nameCell"><s:message code="pay.cc.form.issue.number" /></div>
									<div class="valueCell">${creditCardDto.issueNumber}</div>
									<form:hidden path="issueNumber"/>
								</div>
								
								<div class="oneLine">
									<div class="nameCell"><s:message code="pay.cc.form.start.date" /></div>
									<div class="valueCell">${creditCardDto.startDateMonth}/${creditCardDto.startDateYear}</div>
									<form:hidden path="startDateMonth" />
									<form:hidden path="startDateYear" />
								</div>
								
								
								<div class="oneLine">
									<div class="nameCell"><s:message code="pay.cc.form.expire.date" /></div>
									<div class="valueCell">${creditCardDto.expireDateMonth}/${creditCardDto.expireDateYear}</div>
									<form:hidden path="expireDateMonth" />
									<form:hidden path="expireDateYear" />
								</div>
								
								<div class="oneLine">
									<div class="nameCell"><s:message code="pay.cc.form.security.number" /></div>
									<div class="valueCell">${creditCardDto.securityNumber}</div>
									<form:hidden path="securityNumber"/>
								</div>
								<div class="clr"></div>
								
								<!-- Billing Address block -->
								<h3 class="pink"><s:message code="pay.cc.preview.form.section.billing.address" /></h3>
								<div class="oneLine">
									<div class="nameCell"><s:message code="pay.cc.form.holder.firstname" /></div>
									<div class="valueCell">${creditCardDto.holderTitle.value} ${creditCardDto.holderFirstname} ${creditCardDto.holderLastname}</div>
									<form:hidden path="holderTitle" />
									<form:hidden path="holderFirstname" />
									<form:hidden path="holderLastname" />
								</div>
								
								<div class="oneLine">
									<div class="nameCell"><s:message code="pay.cc.form.holder.address" /></div>
									<div class="valueCell">${creditCardDto.holderAddress} ${creditCardDto.holderAddress2}</div>
									<form:hidden path="holderAddress" />
									<form:hidden path="holderAddress2" />
								</div>
								
								<div class="oneLine">
									<div class="nameCell"><s:message code="pay.cc.form.holder.city" /></div>
									<div class="valueCell">${creditCardDto.holderCity}</div>
									<form:hidden path="holderCity" />
								</div>
								
								<div class="oneLine">
									<div class="nameCell"><s:message code="pay.cc.form.holder.postcode" /></div>
									<div class="valueCell">${creditCardDto.holderPostcode}</div>
									<form:hidden path="holderPostcode" />
								</div>
								
								<div class="oneLine">
									<div class="nameCell"><s:message code="pay.cc.form.holder.country" /></div>
									<div class="valueCell">${creditCardDto.holderCountry}</div>
									<form:hidden path="holderCountry" />
								</div>									
								<form:hidden path="action" />
								<div class="clr"></div>
								<!-- End Billing Address block -->								
								
								<div id="errorBoxContainer" class="errorBoxContainer"></div>
								
								<div id="actionButtons" class="buttons rel">
									<!--one button-->
									<div class="buttonShadow rad4 shortBut abs">
										<div class="buttonBox rad4">
											<div class="buttonContent">
												<input class="button" type="submit" value="<s:message code='pay.cc.form.back' />" />
											</div>
										</div>
									</div>	
									<!--end of one button-->							
									<!--one button-->
									<div class="buttonShadow rad4 button190 abs">
										<div class="buttonBox button190 rad4">
											<div class="buttonContent">
												<input type="button" class="button" id="creditCardSubscribe" value="<s:message code='pay.cc.form.subscribe' />">
											</div>
										</div>
									</div>	
									<!--end of one button-->							
								</div>
								<div id="ajaxLoading" style="display:none">
									<div id="errorBoxContainer" class="errorBoxContainer"></div>
									<div class="oneLine">
										<img alt="" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />/imgs/ajax-loader.gif" />
									</div>
									<div class="clr"></div>
								</div>
							</div>
					</form:form>
				</div>
				<div class="securitySigns abs">
					<a href="" class="secureBox_1"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />/imgs/bgs/spacer.gif" alt="Secured by thawte" /></a>
					<a href="" class="secureBox_2"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />/imgs/bgs/spacer.gif" alt="Secured by Sage Pay" /></a>
				</div>
			</div>
		</div>
	</div>
	<div class="clr verticalSpace"></div>
</div>

<script type="text/javascript">
	$("#creditCardSubscribe").click(function() {
		$("#actionButtons").hide();
		$("#ajaxLoading").show();
		$("#errorBoxContainer").hide();
		$.ajax({
			url:"payments_inapp/creditcard_details.html",
			type:"post",
			data: $("form#creditCardDto").serialize(),
			success: function(data) {
				showModalDialog(data);
				$("#ajaxLoading").hide();
				$("#actionButtons").show();
			}
		}).fail(function(data,x,e) { 
			$("#errorBoxContainer").html(data.responseText);
			$("#ajaxLoading").hide();
			$("#actionButtons").show();
			$("#errorBoxContainer").show();
		});
	});
</script>