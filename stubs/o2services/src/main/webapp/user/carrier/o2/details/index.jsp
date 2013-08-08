<%
   response.setContentType("text/xml"); 
   
	String code=request.getParameter("otac_auth_code");
	if(code==null){
		code="";
	}
	System.err.println("validate OTAC "+code);
	
	//Assert.assertEquals("o2|PAYM", validateOtac("000001"));
	//Assert.assertEquals("o2|PAYG", validateOtac("000002"));
	//Assert.assertEquals("non-o2|PAYG", validateOtac("000003"));
	boolean o2=!code.endsWith("3");
	boolean contractMonthly=code.endsWith("1");
	
	String operator=o2?"o2":"non-o2";
	String contract=contractMonthly?"PAYM":"PAYG";
	
 %>
<user><network_operator><%=operator %></network_operator><tariff_type><%= contract %></tariff_type></user>