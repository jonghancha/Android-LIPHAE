<%@page import="java.sql.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
    request.setCharacterEncoding("utf-8");
String myEmail = request.getParameter("userEmail");

	String url_mysql = "jdbc:mysql://localhost/one?serverTimezone=Asia/Seoul&characterEncoding=utf8&useSSL=false";
 	String id_mysql = "root";
 	String pw_mysql = "qwer1234";
    String WhereDefault = "select userEmail, userPw, userName, userTel, userFilename, userGender, userColor from user";

    String Condition = " where userEmail = '" + myEmail + "'";
    int count = 0;
    
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn_mysql = DriverManager.getConnection(url_mysql, id_mysql, pw_mysql);
        Statement stmt_mysql = conn_mysql.createStatement();

        ResultSet rs = stmt_mysql.executeQuery(WhereDefault + Condition); // 
%>
		{ 
  			"user_info"  : [ 
<%
        while (rs.next()) {
            if (count == 0) {

            }else{
%>
            , 
<%
            }
%>            
			{
			"userEmail" : "<%=rs.getString(1) %>", 
			"userPw" : "<%=rs.getString(2) %>",   
			"userName" : "<%=rs.getString(3) %>",
			"userTel" : "<%=rs.getString(4) %>",  
			"userFilename" : "<%=rs.getString(5) %>",
			"userGender" : "<%=rs.getString(6) %>",
			"userColor" : "<%=rs.getString(7) %>"
			}

<%		
        count++;
        }
%>
		  ] 
		} 
<%		
        conn_mysql.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
	
%>
