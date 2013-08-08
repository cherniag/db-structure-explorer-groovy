<%
response.setContentType("text/xml"); 
String phoneNumber=request.getParameter("phone_number");
System.err.println("responseToValidate"+request.getParameter("phone_number"));
%>
<user><msisdn><%=phoneNumber%></msisdn></user>