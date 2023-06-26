package com.multicamp.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
////https://api.ncloud-docs.com/docs/ai-naver-captcha-image
@Controller
@PropertySource("classpath:apikey.properties")
public class ClovaCaptchaController {
	
	@Value("${clientId}")
	private String clientId;
	
	@Value("${clientSecret}")
	private String clientSecret;
	
	private String key="";//발급받을 키.
	
	private Logger log=LoggerFactory.getLogger(getClass());
	
	@GetMapping("/captchaform")
	public String captchaForm() {
		
		return "clova/clova_captcha";
	}
	//네이버로부터 key를 받아오는 메서드
	public String getKeyCode() {
		try {
            String code = "0"; // 키 발급시 0,  캡차 이미지 비교시 1로 세팅
            String apiURL = "https://naveropenapi.apigw.ntruss.com/captcha/v1/nkey?code=" + code;
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 오류 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            
            System.out.println(response.toString());
            String str=response.toString();
            JSONObject obj=new JSONObject(str);
            
            String key=obj.getString("key");
            return key;
           /*캡차 키 발급 요청시 응답
            * ---------------------------------
            * key string  
            * {"key":"acd12"}
            * 
            * */
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
	}
	
	@GetMapping("/captchaImage")
	public void catchaImage(HttpServletResponse res) {
		try {
		
		this.key=this.getKeyCode();
		
		String apiURL = "https://naveropenapi.apigw.ntruss.com/captcha-bin/v1/ncaptcha?key=" + key + "&X-NCP-APIGW-API-KEY-ID" + clientId;
        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        BufferedReader br;
        if(responseCode==200) { // 정상 호출
            InputStream is = con.getInputStream();
            int read = 0;
            byte[] bytes = new byte[1024];
            // 랜덤한 이름으로 파일 생성
            String tempname = Long.valueOf(new Date().getTime()).toString();
            File f = new File(tempname + ".jpg");
            f.createNewFile();
            //OutputStream outputStream = new FileOutputStream(f); //원본코드 수정. 파일에 이미지 데이터를 출력[x]
            // 우리는 파일에 내보내는 것이 아니라 브라우저에 이미지 데이터를 출력하자
            
            OutputStream outputStream = res.getOutputStream();//브라우저와 연결된 출력스트림을 얻어와서 해당 스트림으로 이미지 데이터를 출력하자
            while ((read =is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            log.info("tempname={}",tempname);
            log.info("파일경로: {}", f.getAbsolutePath());
            is.close();
        } else {  // 오류 발생
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            System.out.println(response.toString());
        }
		}catch(Exception e) {
			log.error("Error: {}", e.getMessage());
		}
	}//------------------------------------
	
	@PostMapping("/captchaCheck")
	public ModelAndView capchaCheck(@RequestParam("userInput") String userInput) {
		ModelAndView mv=new ModelAndView();
		try {
            String code = "1"; // 키 발급시 0,  캡차 이미지 비교시 1로 세팅
            //String key = "CAPTCHA_KEY"; // 캡차 키 발급시 받은 키값
            //이미 멤버변수 key에 할당함
            
            String value = userInput; // 사용자가 입력한 캡차 이미지 글자값
            String apiURL = "https://naveropenapi.apigw.ntruss.com/captcha/v1/nkey?code=" + code +"&key="+ key + "&value="+ value;

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 오류 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            System.out.println(response.toString());
            //{"result":true,"responseTime":84.6}
            JSONObject obj=new JSONObject(response.toString());
            boolean result=obj.getBoolean("result");
            if(result) {//인증 받은 경우
            	mv.addObject("result","success");
            	mv.setViewName("clova/clova_captchaResult");
            }else {//인증받지 못한 경우
            	mv.setViewName("redirect:captchaform");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
		return mv;
	}
	
	

}




