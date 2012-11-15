<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<div class="contentContainer">
	<div class="verticalSpace"></div>
	<div class="content rel oneWideColumn">
		<!-- top part of content -->
		<h1 class="azHeader"><s:message code="page.transaction_history.header.h1" /><span><s:message code="page.transaction_history.header.description" /></span></h1>
		<!-- tabs  -->
		<%@ include file="/WEB-INF/views/www/menu.jsp"%>
		<div class="widerContainer boxWithBorder lessBottomPad">
			<div class="wholePart">
				<div class="details noBg">
					<s:message code='transaction_history.details' />
					<!--pager-->
					<div class="pager1">
						<c:choose>
							<c:when test="${param.maxResults!=null}">
								<s:message code='transaction_history.historyTable.pager' />						
								<a href="payment_history.html"><s:message code='transaction_history.historyTable.pager.viewAll' /></a>
							</c:when>
							<c:otherwise>
								<a href="payment_history.html?maxResults=10"><s:message code='transaction_history.historyTable.pager' /></a>						
								<s:message code='transaction_history.historyTable.pager.viewAll' />
							</c:otherwise>
						</c:choose>
					</div>
					<!--end of pager-->
					<!--table with history of transactionss-->
					<table class="table">
						<thead>
							<tr>
								<th class="alignCenter">
									<s:message code='transaction_history.historyTable.header.id' />
								</th>
								<th class="alignCenter">
									<s:message code='transaction_history.historyTable.header.date' />
								</th>
								<th class="alignCenter">
									<s:message code='transaction_history.historyTable.header.description' />
								</th>
								<th class="alignCenter">
									<s:message code='transaction_history.historyTable.header.method' />
								</th>
								<th class="alignCenter">
									<s:message code='transaction_history.historyTable.header.amount' />
								</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="paymentHistoryItemDto" items="${paymentHistoryItemDtos}">
								<tr>
									<td class="alignCenter">
										${paymentHistoryItemDto.transactionId}
									</td>
									<td class="alignCenter">
										<fmt:formatDate value="${paymentHistoryItemDto.date}" pattern="${dateFormat}" />
									</td>
									<td>
										${paymentHistoryItemDto.description}
									</td>
									<td class="alignCenter">
										${paymentHistoryItemDto.paymentMethod}
									</td>
									<s:message code="transaction_history.historyTable.amount.formater" var="amount_formater"/>
									<td class="alignCenter">
										<fmt:formatNumber type="currency"  currencySymbol="&pound" pattern="${amount_formater}" value="${paymentHistoryItemDto.amount}"></fmt:formatNumber>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<!--end table with history of transactions-->
					<p class="tableNote">
						<s:message code='transaction_history.tableNote' />
					</p>
				</div>
			</div>
		</div>
	</div>
	<!--end of main account content area-->
	<!-- end  of two columns content -->
	<div class="clr verticalSpaceMiddle"></div>
</div>