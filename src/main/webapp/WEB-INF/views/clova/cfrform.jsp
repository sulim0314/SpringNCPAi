<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<script>
	$(function(){
		$('#uploadForm').submit(function(e){
			//alert('a');
			e.preventDefault();
			//업로드 파일이 없을 경우 유효성 체크
			let image=$('#image')[0];
			if(image.files.length==0){
				alert('이미지 파일을 첨부하세요');
				return;
			}
			
			let form=$('#uploadForm')[0];//form객체
			//alert(form)
			let data=new FormData(form);
			let url="cfrCelebrity";
			$.ajax({
				type:'post',
				url:url,
				dataType:'json',
				data:data,//FormData전달
				processData:false,
				contentType:false,//multipart/form-data로 전송되도록 false로 지정				
			}).done((res)=>{
				alert(JSON.stringify(res));
			}).fail((err)=>{
				alert(err.status)
			})
			
			
		})
		
	})//$() end-----
</script>    

<div class="container text-center">
	<h1>Clova Face Recognition - Celebrity</h1>

	<p>
		입력받은 이미지로부터 얼굴을 감지하고 감지한 얼굴이 어떤 유명인과 닮았는지 분석하여 그 결과를 반환하는 REST API입니다.
		이미지에서 다음과 같은 정보를 분석합니다. <br>
		<br> 감지된 얼굴의 수<br> 감지된 각 얼굴을 분석한 정보<br> 닮은 유명인 이름<br>
		해당 유명인을 닮은 정도<br>
		https://api.ncloud-docs.com/docs/ai-naver-clovafacerecognition-celebrity
	</p>
	<form method="post" enctype="multipart/form-data" id="uploadForm">
		<label for="image">이미지 선택</label>
		<input type="file" name="image" id="image">
		<button>확 인</button>
	</form>
</div>