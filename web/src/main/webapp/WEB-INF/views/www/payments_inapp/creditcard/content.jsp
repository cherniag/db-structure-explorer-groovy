<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="contentContainer">
	<div class="verticalSpace"></div>
	<!-- top part of content -->
	<div class="content rel oneWideColumn">	
							
		<h1 class="azHeader"><s:message code="page.cc.header.h1" /><span><s:message code="page.cc.header.description" /></span></h1>
		<%@ include file="/WEB-INF/views/www/menu.jsp"%>
		
		<div class="widerContainer boxWithBorder lessBottomPad">
			<div class="wholePart">						
				<div class="details phoneRightBg">
					<h2><s:message code="pay.cc.form.title" /></h2>
					<form:form modelAttribute="creditCardDto" method="post">
					<input type="hidden" name="paymentPolicyId" value="${paymentPolicy.id}"/>
					<div class="payDetails">
						<h3><s:message code="pay.cc.form.description" 
						    	arguments="${paymentPolicy.subweeks};${paymentPolicy.subcost}"
       							htmlEscape="false"
       							argumentSeparator=";"/>
						</h3>
						<div class="oneInputLine">
							<div class="nameCell"><form:label path="cardType"><s:message code="pay.cc.form.card.type" /></form:label></div>
							<div class="valueCell">
								<form:select path="cardType" cssClass="month">
									<option value=""><s:message code="pay.cc.form.card.type.select" /></option>
									<c:forEach items="${cardTypes}" var="cardType">
										<form:option value="${cardType}"><s:message code="${cardType.code}"/></form:option>
									</c:forEach>
								</form:select>
								<form:errors path="cardType" cssClass="errorSign" />
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell"><form:label path="cardNumber"><s:message code="pay.cc.form.card.number" /></form:label></div>
							<div class="valueCell">
								<form:input path="cardNumber"/>
								<form:errors cssClass="errorSign" path="cardNumber" delimiter=" "  />						
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell doubleLines"><form:label path="issueNumber"><s:message code="pay.cc.form.issue.number" /></form:label></div>
							<div class="valueCell shortInput">
								<form:input path="issueNumber"/>
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell"><form:label path="startDateMonth"><s:message code="pay.cc.form.start.date" /></form:label></div>
							<div class="valueCell">
								<form:select path="startDateMonth" items="${selectDates}" cssClass="month" />
								<form:select path="startDateYear" items="${selectYears}" cssClass="year" />
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell"><form:label path="expireDateMonth"><s:message code="pay.cc.form.expire.date" /></form:label></div>
							<div class="valueCell">
								<form:select path="expireDateMonth" items="${selectDates}"  cssClass="month" />
								<form:select path="expireDateYear" items="${selectExpireYears}" cssClass="year" />
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell"><form:label path="securityNumber"><s:message code="pay.cc.form.security.number" /></form:label></div>
							<div class="valueCell shortInput">
								<form:input path="securityNumber"/>
								<a href="#" id="securityNumberInfo"><img alt="Info" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />/imgs/icons/info.png"></a>
							</div>
						</div>
						<div class="clr"></div>
					</div>
					
					<div class="payDetails holderDetails">
						<h3 class="pink"><s:message code="pay.cc.form.section.holder" /></h3>
						
						<div class="oneInputLine">
							<div class="nameCell"><form:label path="holderTitle"><s:message code="pay.cc.form.holder.title" /></form:label></div>
							<div class="valueCell">
								<form:select path="holderTitle" items="${titles}" itemLabel="value" cssClass="month" />
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell"><form:label path="holderFirstname"><s:message code="pay.cc.form.holder.firstname" /></form:label></div>
							<div class="valueCell">
								<form:input path="holderFirstname" />
								<form:errors cssClass="errorSign" path="holderFirstname"  />
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell"><form:label path="holderLastname"><s:message code="pay.cc.form.holder.lastname" /></form:label></div>
							<div class="valueCell">
								<form:input path="holderLastname" />
								<form:errors cssClass="errorSign" path="holderLastname"  />
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell"><form:label path="holderAddress"><s:message code="pay.cc.form.holder.address" /></form:label></div>
							<div class="valueCell">
								<form:input path="holderAddress" />
								<form:errors cssClass="errorSign" path="holderAddress"  />
							</div>
						</div>
						<div class="clr"></div>
						<div class="oneInputLine">
							<div class="nameCell">&nbsp;</div>
							<div class="valueCell">
								<form:input path="holderAddress2" />
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell"><form:label path="holderCity"><s:message code="pay.cc.form.holder.city" /></form:label></div>
							<div class="valueCell">
								<form:input path="holderCity" />
								<form:errors cssClass="errorSign" path="holderCity"  />
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell"><form:label path="holderPostcode"><s:message code="pay.cc.form.holder.postcode" /></form:label></div>
							<div class="valueCell">
								<form:input path="holderPostcode" />
								<form:errors cssClass="errorSign" path="holderPostcode"  />
							</div>
						</div>
						<div class="clr"></div>
						
						<div class="oneInputLine">
							<div class="nameCell"><form:label path="holderCountry"><s:message code="pay.cc.form.holder.country" /></form:label></div>
							<div class="valueCell">
								<form:select path="holderCountry">
									<option value=""><s:message code="pay.cc.form.holder.country.select" /></option>
									<form:options items="${countries}" itemValue="name" itemLabel="fullName" />
								</form:select>
								<form:errors cssClass="errorSign" path="holderCountry"  />
							</div>
						</div>
						<form:hidden path="action" />
						<div class="clr"></div>
						
						
						<s:hasBindErrors name="creditCardDto">
							<div class="errorBoxContainer">
								<div class="errorBox rad7">
									<form:errors path="*" />
								</div>
							</div>
						</s:hasBindErrors>
						
						<div class="oneInputLine buttonNext">
							<div class="nameCell">&nbsp;</div>
							<div class="valueCell">
								<!--one button-->
								<div class="buttonShadow rad4 middleBut">
									<div class="buttonBox rad4">
										<div class="buttonContent">
											<input type="submit" class="button" value="<s:message code='pay.cc.form.submit.next' />" />
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="clr"></div>
					</div>
					</form:form>
				</div>
				<div class="securitySigns abs">
					<a href="" class="secureBox_1"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />/imgs/bgs/spacer.gif" alt="" /></a>
					<a href="" class="secureBox_2"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />/imgs/bgs/spacer.gif" alt="" /></a>
				</div>
			</div>
		</div>
	</div>
	<div class="clr verticalSpace"></div>
</div>
<script type="text/javascript">
	$("#securityNumberInfo").click(function(e){
		showAsDialog("#popupSecurityNumberInfo");
		e.preventDefault();
	});
</script>
<div id="popupSecurityNumberInfo" class="pageWindow simplePopup">
	<div class="pageWindowContent simpleTextPopup">
		<div class="innerBox">
			<h2><s:message code='pay.cc.popup.security.number.title' /></h2>
			<s:message code='pay.cc.popup.security.number.body' />
		</div>
	</div>
</div>