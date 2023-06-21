package com.multicamp.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
//https://api.ncloud-docs.com/docs/ai-naver-clovaspeechrecognition-stt
@RestController
@PropertySource("classpath:apikey.properties")
public class ClovaCSRController {
	@Value("${clientId}")
	private String clientId;
	
	@Value("${clientSecret}")
	private String clientSecret;
	
	private Logger log=LoggerFactory.getLogger(getClass());
	
	
	@GetMapping("/csrform")
	public ModelAndView csrform() {
		
		return new ModelAndView("clova/clova_speech");
	}
	@PostMapping(value="/csrSpeech", produces="application/json")
	public Map<String, String> speechRecognition(@RequestParam("mp3file") MultipartFile mfile,
			@RequestParam("language") String lang, HttpSession ses){
		Map<String,String> map=new HashMap<>();
		ServletContext app=ses.getServletContext();
		String path=app.getRealPath("/upload");
		File dir=new File(path);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		File voiceFile=new File(path, mfile.getOriginalFilename());
		log.info("file={}", voiceFile.getAbsolutePath());
		try {
			mfile.transferTo(voiceFile);//업로드 처리
			log.info("upload success!!");
			
			String language = lang;        // 언어 코드 ( Kor, Jpn, Eng, Chn )
            String apiURL = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=" + language;
            URL url = new URL(apiURL);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

            OutputStream outputStream = conn.getOutputStream();
            FileInputStream inputStream = new FileInputStream(voiceFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            //네이버 서버로 mp3파일 보내기 완료
            ////////////////////////////////////
            
            
            BufferedReader br = null;
            int responseCode = conn.getResponseCode();
            if(responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {  // 오류 발생
                System.out.println("error!!!!!!! responseCode= " + responseCode);
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            String inputLine;

            if(br != null) {
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
                map.put("result", response.toString());
            } else {
                System.out.println("error !!!");
                map.put("result", "error!!!");
            }
			
		} catch (Exception e) {
			log.error("error: {}", e.getMessage());
			map.put("result", "error!!! "+e.getMessage());
		}
		
		
		return map;
	}
	

}//////////////////////////////////////






