<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script>
	$(function(){
		
		$('#btnSummary').click(function(){
			let title=$('#title').val();
			let content=$('#news_content').val();
			console.log(content);
			if(!title||!content){
				alert('뉴스 제목과 내용을 입력하세요');
				return;
			}
			let url="clovaSummary";
			$.ajax({
				type:'post',
				url:url,
				dataType:'text',
				data:{
					title:title,
					content:content
				}				
			})
			.done((res)=>{
				//alert(typeof(res));
				
				$('#result').html("<h4>"+res+"</h4>");
			})
			.fail((err)=>{
				alert(err.status)
			})
			
			
		})//----------------
		
		
	})//$() end---------------
</script>    
    
<div id="wrap" class="container text-center">
	<h1>Naver Clova Summary</h1>
	제 목 : <input type="text" id="title" name="title" style="width:70%">
	<br><br>
	글내용:<textarea rows="8" cols="100" name="content" id="news_content" style="height:300px"></textarea>
	<br><br>
	<button id="btnSummary">문장 요약하기</button>
	<hr color='red'>
	<div id="result"></div>
</div>    