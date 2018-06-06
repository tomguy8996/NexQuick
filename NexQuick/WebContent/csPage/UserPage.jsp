<%@ page import = "java.util.*" %>
<%@ page import = "com.nexquick.model.vo.CallInfo" %>
<%@ page import = "com.nexquick.model.vo.OnDelivery" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<% List<CallInfo> list = (List<CallInfo>) request.getSession().getAttribute("callListByCsId"); %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script src="<%=request.getContextPath() %>/Table_Highlight_Vertical_Horizontal/vendor/jquery/jquery-3.2.1.min.js"></script>
<!--===============================================================================================-->	
	<link rel="icon" type="image/png" href="images/icons/favicon.ico"/>
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/Table_Fixed_Header/vendor/bootstrap/css/bootstrap.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/Table_Fixed_Header/fonts/font-awesome-4.7.0/css/font-awesome.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/Table_Fixed_Header/vendor/animate/animate.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/Table_Fixed_Header/vendor/select2/select2.min.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/Table_Fixed_Header/vendor/perfect-scrollbar/perfect-scrollbar.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/Table_Fixed_Header/css/util.css">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/Table_Fixed_Header/css/main.css">
<!--===============================================================================================-->
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/css/indexStyle.css">



<title>NexQuick :: 전체 신청목록</title>
</head>
<body>
<%@ include file = "../navigation.jsp" %>
	<div class="limiter">
		<div class="container-table100" style = "top:0em!important;">
			<div class="wrap-table100">
				<div class="table100 ver3 m-b-110">
					<table data-vertable="ver3">
						<thead>
							<tr class="row100 head">
								<th class="column100 column1" data-column="column1">콜 번호</th>
								<th class="column100 column2" data-column="column2">오더 번호</th>
								<th class="column100 column3" data-column="column3">발송지</th>
								<th class="column100 column4" data-column="column4">수령지</th>
								<th class="column100 column5" data-column="column5">요금</th>
								<th class="column100 column6" data-column="column6">배송상황</th>
								<th class="column100 column7" data-column="column7">배송시간</th>
								<th class="column100 column8" data-column="column8">평점</th>
							</tr>
						</thead>
						<tbody>
						<% if(list != null) { 
							int countNumber = 1;
							for(CallInfo ci : list) {
						%>
							<tr class="row100">
								<td class="column100 column1" data-column="column1"><%= ci.getCallNum() %></td>
								<td class="column100 column2" data-column="column2"><%= ci.getTotalPrice() %></td>
								<td class="column100 column3" data-column="column3"><%= ci.getCsId() %></td>
								<td class="column100 column4" data-column="column4"><%= ci.getDeliveryStatus() %></td>
								<td class="column100 column5" data-column="column5"><%= ci.getReserved() %></td>
								<td class="column100 column6" data-column="column6"><%= ci.getSenderName() %></td>
								<td class="column100 column7" data-column="column7"><%= ci.getSenderAddress() %></td>
								<td class="column100 column8" data-column="column8">100</td>
							</tr>
							<% countNumber++; } %>
						<% } %>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>

<!-- javascript가 여기에 들어가야 작동됨 -->
<!--===============================================================================================-->
	<script src="<%=request.getContextPath() %>/Table_Fixed_Header/vendor/bootstrap/js/popper.js"></script>
	<script src="<%=request.getContextPath() %>/Table_Fixed_Header/vendor/bootstrap/js/bootstrap.min.js"></script>
<!--===============================================================================================-->
	<script src="<%=request.getContextPath() %>/Table_Fixed_Header/vendor/select2/select2.min.js"></script>
<!--===============================================================================================-->
	<script src="<%=request.getContextPath() %>/Table_Fixed_Header/vendor/perfect-scrollbar/perfect-scrollbar.min.js"></script>
<!-- 왜인지는 모르겠음 -->
		<script>
		$('.js-pscroll').each(function(){
			var ps = new PerfectScrollbar(this);

			$(window).on('resize', function(){
				ps.update();
			})
		});
			
		
	</script>
<!--===============================================================================================-->
	<script src="<%=request.getContextPath() %>/Table_Fixed_Header/js/main.js"></script>
	
	
<%@ include file = "../footer.jsp" %>
	
</body>
</html>