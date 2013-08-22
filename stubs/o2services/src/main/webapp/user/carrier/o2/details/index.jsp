<%
   response.setContentType("text/xml"); 
   
	String code=request.getParameter("otac_auth_code");
	if(code==null){
		code="";
	}
	System.err.println("validate OTAC "+code);

	String phone=code;
	o2stub.SubsData data=o2stub.PhoneNumberManager.getInstance().getData(phone);
	System.err.println("validate OTAC data "+data);
	String operator=data.isO2()?"o2":"non-o2";
	String contract=(!data.isPayAsYouGo())?"PAYM":"PAYG";
	
 %>
<user><network_operator><%=operator %></network_operator><tariff_type><%= contract %></tariff_type></user>