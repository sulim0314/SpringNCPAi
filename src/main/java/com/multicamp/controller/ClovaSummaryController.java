package com.multicamp.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;



@RestController
@PropertySource("classpath:apikey.properties")
public class ClovaSummaryController {
	
	@Value("${clientId}")
	private String clientId;
	
	@Value("${clientSecret}")
	private String clientSecret;
	
	private String apiUrl="https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize";
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	@GetMapping("/summaryform")
	public ModelAndView summaryForm() {
		log.info("clientId={}",clientId);
		log.info("clientSecret={}",clientSecret);
		return new ModelAndView("clova/clova_summary");
		//WEB-INF/view/clova/clova_summary.jsp
	}
	
	@PostMapping(value="/clovaSummary", produces="text/plain")
	public String summaryResult(@RequestParam("title") String title, @RequestParam("content") String content)
	throws Exception
	{
		log.info("title={}", title);
		log.info("content={}", content);
		
		
		URL url=new URL(apiUrl);
		
		HttpURLConnection con=(HttpURLConnection)url.openConnection();
		
		con.setRequestMethod("POST");
		con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
		con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
		con.setRequestProperty("Content-Type", "application/json");
		
		//전송할 데이터를 json형식으로 만들자
		JSONObject doc=new JSONObject();//key,value
		doc.put("title", title);
		doc.put("content", content);
		
		JSONObject option=new JSONObject();
		option.put("language", "ko");//ko,ja(일본어)
		option.put("model", "news");//news, general
		option.put("tone", "3");//0,1,2,3
		option.put("summaryCount", "3");//문장을 3문장으로 요약
		
		JSONObject root=new JSONObject();
		root.put("document", doc);
		root.put("option", option);
		
		String jsonParam=root.toString();
		log.info("jsonParam======{}",jsonParam);
		
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		
		
		OutputStream os=con.getOutputStream();
		PrintWriter pw=new PrintWriter(new OutputStreamWriter(os,"UTF-8"), true);
		pw.println(jsonParam);//네이버 클라우드 서버로 요청 파라미터 데이터를 전송
		pw.close();
		
		int resCode=con.getResponseCode();
		BufferedReader br=null;
		if(resCode==200) {
			br=new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
		}else {//error:
			log.info("error: {}", resCode);
			br=new BufferedReader(new InputStreamReader(con.getErrorStream(),"UTF-8"));
		}
		
		String line="";
		StringBuilder buf=new StringBuilder();
		while((line=br.readLine())!=null) {
			buf.append(line);
		}
		br.close();
		String str=buf.toString();
		JSONObject json=new JSONObject(str);
		String summary=json.getString("summary");
		log.info("summary===={}",summary);
		return summary;
	}

}///////////////////////////////////





