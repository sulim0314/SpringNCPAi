<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<script>
	$(function(){
		
		$('#frm').submit(function(){
			//alert('a')
			event.preventDefault();
			let file=$('#mp3file')[0];
			if(file.files.length==0){
				alert('음성 파일을 업로드 하세요')
				return;
			}
		
			let form=$('#frm')[0];
			let formData=new FormData(form);
			let fname=formData.get("mp3file").name;
			//alert(fname);
			
			let url="csrSpeech";
			$.ajax({
				type:'post',
				data:formData,
				dataType:'json',
				url:url,
				processData:false,
				contentType:false
			})
			.done((res)=>{
				alert(JSON.stringify(res));
				console.log(typeof res);//string
				let jsonObj=JSON.parse(res.result);
				$('#result').html("<h3>"+jsonObj.text+"</h3>");
				$('#sttAudio').prop('src','/upload/'+fname);
				$('#sttAudio').prop("autoplay","autoplay");//오디오 자동 플레이
			})
			.fail((err)=>{
				alert('err: '+err.status);
			})
		
		})
		
		
	})//$() end----
</script>
    
<div class="container text-center">
	<br>
	<h1>Clova Speech Recognition</h1>
	<br>
	<form method="post" enctype="multipart/form-data" id="frm">
		<select name="language">
			<option value="Kor">한국어</option>
			<option value="Eng">영어</option>
			<option value="Jpn">일본어</option>
			<option value="Chn">중국어</option>
		</select><br><br>
		
		<label for="mp3file">MP3파일</label>
		<input type="file" name="mp3file" id="mp3file">
		<button class="btn btn-primary">업로드</button>
	</form>
	<hr>
	<div>
		<h2>STT: 음성을 텍스트로 변환한 결과</h2>
		<audio id="sttAudio" preload="auto" controls="controls"></audio>
	</div>
	<div id="result">
	
	</div>
</div>    