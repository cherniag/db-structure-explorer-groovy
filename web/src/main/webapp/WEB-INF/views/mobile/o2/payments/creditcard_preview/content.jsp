<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div id="mainContent">
	<form:form modelAttribute="creditCardDto" method="post">
	<input type="hidden" name="paymentPolicyId" value="${paymentPolicy.id}"/>
	<div class="header">
        <a href="${pageContext.request.contextPath}/payments/creditcard.html?paymentPolicyId=${paymentPolicy.id}" class="button-small button-left"><s:message code='m.page.main.menu.back' /></a>
        <span class="logo"><img src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />imgs/logo.png" /></span>
       <a href="${pageContext.request.contextPath}/account.html" class="button-small button-right"><s:message code='m.page.main.menu.close' /></a>
	</div>
	<div class="container">
		<div class="content">
				<h1><s:message code="pay.cc.form.title" /></h1>
				<div class="cardDetails">
					<p><s:message code="pay.cc.preview.form.description" /></p>
					
					<h3><s:message code="pay.cc.preview.form.section.card.details" /></h3>
					<div class="oneLine">
						<div class="nameCell"><s:message code="pay.cc.form.card.type" /></div>
						<div class="valueCell"><s:message code="${creditCardDto.cardType.code}"/></div>
						<div class="helper"></div>	
						<form:hidden path="cardType" />
					</div>
					
					<div class="oneLine">
						<div class="nameCell"><s:message code="pay.cc.form.holder.info" /></div>
						<div class="valueCell">${creditCardDto.holderTitle.value} ${creditCardDto.holderFirstname} ${creditCardDto.holderLastname}</div>
						<div class="helper"></div>	
					</div>
					
					<div class="oneLine">
						<div class="nameCell"><s:message code="pay.cc.form.card.number" /></div>
						<div class="valueCell">${creditCardDto.cardNumber}</div>
						<div class="helper"></div>	
						<form:hidden path="cardNumber"/>
					</div>
					
					<div class="oneLine">
						<div class="nameCell"><s:message code="pay.cc.form.issue.number" /></div>
						<div class="valueCell">${creditCardDto.issueNumber}</div>
						<div class="helper"></div>	
						<form:hidden path="issueNumber"/>
					</div>
					
					<div class="oneLine">
						<div class="nameCell"><s:message code="pay.cc.form.start.date" /></div>
						<div class="valueCell">${creditCardDto.startDateMonth}/${creditCardDto.startDateYear}</div>
						<div class="helper"></div>	
						<form:hidden path="startDateMonth" />
						<form:hidden path="startDateYear" />
					</div>
					
					
					<div class="oneLine">
						<div class="nameCell"><s:message code="pay.cc.form.expire.date" /></div>
						<div class="valueCell">${creditCardDto.expireDateMonth}/${creditCardDto.expireDateYear}</div>
						<div class="helper"></div>	
						<form:hidden path="expireDateMonth" />
						<form:hidden path="expireDateYear" />
					</div>
					
					<div class="oneLine">
						<div class="nameCell"><s:message code="pay.cc.form.security.number" /></div>
						<div class="valueCell">${creditCardDto.securityNumber}</div>
						<div class="helper"></div>	
						<form:hidden path="securityNumber"/>
					</div>
					<div class="clr"></div>
					
					<!-- Billing Address block -->
					<h3><s:message code="pay.cc.preview.form.section.billing.address" /></h3>
					<div class="oneLine">
						<div class="nameCell"><s:message code="pay.cc.form.holder.firstname" /></div>
						<div class="valueCell">${creditCardDto.holderTitle.value} ${creditCardDto.holderFirstname} ${creditCardDto.holderLastname}</div>
						<div class="helper"></div>	
						<form:hidden path="holderTitle" />
						<form:hidden path="holderFirstname" />
						<form:hidden path="holderLastname" />
					</div>
					
					<div class="oneLine">
						<div class="nameCell"><s:message code="pay.cc.form.holder.address" /></div>
						<div class="valueCell">${creditCardDto.holderAddress} ${creditCardDto.holderAddress2}</div>
						<div class="helper"></div>	
						<form:hidden path="holderAddress" />
						<form:hidden path="holderAddress2" />
					</div>
					
					<div class="oneLine">
						<div class="nameCell"><s:message code="pay.cc.form.holder.city" /></div>
						<div class="valueCell">${creditCardDto.holderCity}</div>
						<div class="helper"></div>	
						<form:hidden path="holderCity" />
					</div>
					
					<div class="oneLine">
						<div class="nameCell"><s:message code="pay.cc.form.holder.postcode" /></div>
						<div class="valueCell">${creditCardDto.holderPostcode}</div>
						<div class="helper"></div>	
						<form:hidden path="holderPostcode" />
					</div>
					
					<div class="oneLine">
						<div class="nameCell"><s:message code="pay.cc.form.holder.country" /></div>
						<div class="valueCell">${creditCardDto.holderCountry}</div>
						<div class="helper"></div>	
						<form:hidden path="holderCountry" />
					</div>
					<form:hidden path="action" />
					<div class="clr"></div>
				</div>
				<div class="addSmallSpace"></div>
				
				<div class="contentButton formButton rad5 rel" id="actionButtons" >
					<input type="button" class="button-turquoise" id="creditCardSubscribe" value="<s:message code='pay.cc.form.subscribe' />">
                    <span class="button-arrow"/>
				</div>
			
				<div id="ajaxLoading" style="display:none">
					<div class="oneLine">
						<img alt="" src="<c:out value='${requestScope.assetsPathAccordingToCommunity}' />/imgs/ajax-loader.gif" />
					</div>
					<div class="clr"></div>
				</div>
				<div class="note" id="errorBoxContainer" style="display:none; margin: 20px; margin-top: 0px;"></div>
		</div>
	</div>
	
	</form:form>
	
</div>
<s:message code="pay.cc.form.error.nonetwork" var="noNetworkErrorMessage" />
<script type="text/javascript">
	$("#creditCardSubscribe").click(function() {
		$("#actionButtons").hide();
		$("#ajaxLoading").show();
		$("#errorBoxContainer").hide();
		$.ajax({
			url:"${pageContext.request.contextPath}/payments/creditcard_details.html",
			type:"post",
			data: $("form#creditCardDto").serialize(),
			success: function(data) {
				$("#ajaxLoading").hide();
				$("#actionButtons").show();
				$("#mainContent").html(data);
			}
		}).fail(function(data,x,e) {
			var errorText = data.responseText;
			if (0 == data.status) {
				errorText = "<c:out value='${noNetworkErrorMessage}' />";
			}
			$("#errorBoxContainer").html("<span class='validationNotes'>"+errorText+"</span>");
			$("#ajaxLoading").hide();
			$("#actionButtons").show();
			$("#errorBoxContainer").css("display","block");
		});
	});
	</script>
