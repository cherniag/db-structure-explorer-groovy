<%

    java.util.List<o2stub.SubsData> list=o2stub.PhoneNumberManager.getInstance().list();

	

	for(o2stub.SubsData e: list){
%>	
	<br/> <%=e.getPhoneNumberWithCode() %> provider o2 ?<%=e.isO2() %>

<%			
		
	}

%>