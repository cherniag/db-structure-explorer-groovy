<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="header">
    <a href="payments_inapp.html" class="button-small button-left"><s:message code='m.page.main.menu.back' /></a>
    <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" /></span>
</div>
<div class="container">
	<div class="content">
		<form:form modelAttribute="creditCardDto" method="post">
			<input type="hidden" name="offerId" value="${offerId}"/>
			<input type="hidden" name="paymentPolicyId" value="${paymentPolicy.id}"/>
			<h1><s:message code="pay.cc.form.title" /></h1>
			<div class="payDetails">
				<p><s:message code="pay.cc.form.description"
							arguments="${paymentPolicy.subweeks};${paymentPolicy.subcost}"
       						htmlEscape="false"
       						argumentSeparator=";"/>
				</p>
				<div class="oneInputLine">
					<div class="nameCell"><form:label path="cardType"><s:message code="pay.cc.form.card.type" /></form:label></div>
					<div class="valueCell">
						<form:select path="cardType" cssClass="month">
							<option value=""><s:message code="pay.cc.form.card.type.select" /></option>
							<c:forEach items="${cardTypes}" var="cardType">
								<form:option value="${cardType}"><s:message code="${cardType.code}"/></form:option>
							</c:forEach>
						</form:select>
						<s:hasBindErrors name="creditCardDto">
							<div class="note" id="note">
								<form:errors path="cardType" />
							</div>
						</s:hasBindErrors>
					</div>
					<div class="helper"></div>
				</div>
				<div class="clr"></div>
				
				<div class="oneInputLine">
					<div class="nameCell"><form:label path="cardNumber"><s:message code="pay.cc.form.card.number" /></form:label></div>
					<div class="valueCell">
						<form:input path="cardNumber"/>
						<s:hasBindErrors name="creditCardDto">
							<div class="note" id="note">
								<form:errors path="cardNumber" />
							</div>
						</s:hasBindErrors>	
					</div>
					
					<div class="helper"></div>
				</div>
									
				<div class="clr"></div>
				
				<div class="oneInputLine">
					<div class="nameCell doubleLines"><form:label path="issueNumber"><s:message code="pay.cc.form.issue.number" /></form:label></div>
					<div class="valueCell shortInput">
						<form:input path="issueNumber"/>
					</div>
					<div class="helper"></div>
				</div>					
				<div class="clr"></div>
				
				<div class="oneInputLine">
					<div class="nameCell"><form:label path="startDateMonth"><s:message code="pay.cc.form.start.date" /></form:label></div>
					<div class="valueCell">
						<form:select path="startDateMonth" items="${selectDates}" cssClass="month" />
						<form:select path="startDateYear" items="${selectYears}" cssClass="year" />
					</div>
					<div class="helper"></div>
				</div>
				
				<div class="clr"></div>
				
				<div class="oneInputLine">
					<div class="nameCell"><form:label path="expireDateMonth"><s:message code="pay.cc.form.expire.date" /></form:label></div>
					<div class="valueCell">
						<form:select path="expireDateMonth" items="${selectDates}"  cssClass="month" />
						<form:select path="expireDateYear" items="${selectExpireYears}" cssClass="year" />
						<s:hasBindErrors name="creditCardDto">
							<div class="note" id="note">
								<form:errors path="startDateMonth" />
							</div>
						</s:hasBindErrors>
					</div>
					<div class="helper"></div>
				</div>
						
				<div class="clr"></div>	
				
				<div class="oneInputLine">
					<div class="nameCell"><form:label path="securityNumber"><s:message code="pay.cc.form.security.number" /></form:label></div>
					<div class="valueCell shortInput">
						<form:input path="securityNumber"/>
						<s:hasBindErrors name="creditCardDto">
							<div class="note" id="note">
								<form:errors path="securityNumber" />
							</div>
						</s:hasBindErrors>
					</div>
					<div class="helper"></div>
				</div>
				
				<div class="clr"></div>
				<!--end one record in profile-->			
			</div>
			<div class="payDetails holderDetails">
				<h3 class="pink"><s:message code="pay.cc.form.section.holder" /></h3>
				
				<div class="oneInputLine">
					<div class="nameCell"><form:label path="holderTitle"><s:message code="pay.cc.form.holder.title" /></form:label></div>
					<div class="valueCell">
						<form:select path="holderTitle" items="${titles}" itemLabel="value" cssClass="month" />
					</div>
					<div class="helper"></div>
				</div>
				<div class="clr"></div>
				
				<div class="oneInputLine">
					<div class="nameCell"><form:label path="holderFirstname"><s:message code="pay.cc.form.holder.firstname" /></form:label></div>
					<div class="valueCell">
						<form:input path="holderFirstname" />
						<s:hasBindErrors name="creditCardDto">
							<div class="note" id="note">
								<form:errors path="holderFirstname" />
							</div>
						</s:hasBindErrors>	
					</div>
					<div class="helper"></div>
				</div>
				<div class="clr"></div>
				
				<div class="oneInputLine">
					<div class="nameCell"><form:label path="holderLastname"><s:message code="pay.cc.form.holder.lastname" /></form:label></div>
					<div class="valueCell">
						<form:input path="holderLastname" />
						<s:hasBindErrors name="creditCardDto">
							<div class="note" id="note">
								<form:errors path="holderLastname" />
							</div>
						</s:hasBindErrors>
					</div>
					<div class="helper"></div>
				</div>
				<div class="clr"></div>
				
				<div class="oneInputLine">
					<div class="nameCell"><form:label path="holderAddress"><s:message code="pay.cc.form.holder.address" /></form:label></div>
					<div class="valueCell">
						<form:input path="holderAddress" />
						<s:hasBindErrors name="creditCardDto">
							<div class="note" id="note">
								<form:errors path="holderAddress" />
							</div>
						</s:hasBindErrors>
					</div>
					<div class="helper"></div>
				</div>
				<div class="clr"></div>
				<div class="oneInputLine">
					<div class="nameCell">&nbsp;</div>
					<div class="valueCell">
						<form:input path="holderAddress2" />
					</div>
					<div class="helper"></div>
				</div>
				<div class="clr"></div>
				
				<div class="oneInputLine">
					<div class="nameCell"><form:label path="holderCity"><s:message code="pay.cc.form.holder.city" /></form:label></div>
					<div class="valueCell">
						<form:input path="holderCity" />
						<s:hasBindErrors name="creditCardDto">
							<div class="note" id="note">
								<form:errors path="holderCity" />
							</div>
						</s:hasBindErrors>
					</div>
					<div class="helper"></div>
				</div>
				<div class="clr"></div>								
				
				<div class="oneInputLine">
					<div class="nameCell"><form:label path="holderPostcode"><s:message code="pay.cc.form.holder.postcode" /></form:label></div>
					<div class="valueCell">
						<form:input path="holderPostcode" />
						<s:hasBindErrors name="creditCardDto">
							<div class="note" id="note">
								<form:errors path="holderPostcode" />
							</div>
						</s:hasBindErrors>
					</div>
					<div class="helper"></div>
				</div>
				<div class="clr"></div>
				
				<div class="oneInputLine">
					<div class="nameCell"><form:label path="holderCountry"><s:message code="pay.cc.form.holder.country" /></form:label></div>
					<div class="valueCell">
						<form:select path="holderCountry">
							<option value=""><s:message code="pay.cc.form.holder.country.select" /></option>
							<form:options items="${countries}" itemValue="name" itemLabel="fullName" />
						</form:select>
						<s:hasBindErrors name="creditCardDto">
							<div class="note" id="note">
								<form:errors path="holderCountry" />
							</div>
						</s:hasBindErrors>	
					</div>
					<div class="helper"></div>
				</div>
				<form:hidden path="action" />
				<div class="clr"></div>
				<!--end one record in profile-->										
			</div>
			<!--button-->
			<div class="rel" >
				<input type="submit" class="button-turquoise" value="<s:message code='pay.cc.form.submit.next' />" />
                <span class="button-arrow"/>
			</div>
		</form:form>
	</div>
</div>
