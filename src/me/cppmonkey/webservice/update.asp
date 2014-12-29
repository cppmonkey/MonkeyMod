<%
dim action
action = Request.QueryString("action")

If action <> "" Then
	response.write("Hello world<br />")
	response.write("Processing action")
Else
	response.write("error, no action provided")
End If
%>