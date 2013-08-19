<%
response.setContentType("text/xml"); 
String phoneNumber=request.getParameter("phone_number");
String phoneNumberWithCode=o2stub.PhoneNumberManager.getInstance().getNumberWithCode(phoneNumber);

System.err.println("responseToValidate"+request.getParameter("phone_number")+phoneNumberWithCode);


%>
<user><msisdn><%=phoneNumberWithCode%></msisdn></user>