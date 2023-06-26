<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>



<div class="container text-center">
	<br>
	
	<h1>Captcha Image를 이용한 인증</h1>
	
	<img src="/captchaImage"><br><br>
	
	<form id="frm" action="/captchaCheck" method="post">
		<input type="text" name="userInput" id="userInput" size="40" required="required">
		<button class="btn-primary">인증하기</button>
	</form>


</div>