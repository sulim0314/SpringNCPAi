package com.multicamp.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
/*
ClientID
wvaof3v62r

ClientSecret
ywMnwBxqGfNu2hdE70KUEsZ23J7ZuOQkyduBCK6C
 * */
@RestController
public class ClovaCFRController {
	
	private String clientId="wvaof3v62r";
	private String clientSecret="ywMnwBxqGfNu2hdE70KUEsZ23J7ZuOQkyduBCK6C";
	
	private Logger log=LoggerFactory.getLogger(getClass());
	
	@GetMapping("/faceform")
	public ModelAndView faceform() {
		
		return new ModelAndView("clova/cfrform");
	}//---------------------
	
	@PostMapping("/cfrCelebrity")
	public Map<String, String> cfrResult(@RequestParam("image") MultipartFile mfile, HttpSession ses){
		Map<String, String> map=new HashMap<>();
		ServletContext ctx=ses.getServletContext();
		String upDir=ctx.getRealPath("/upload");
		
		log.info("upDir={}", upDir);
		
		File dir=new File(upDir);
		if(!dir.exists()) {
			dir.mkdirs();//upload디렉토리 생성
		}
		//첨부파일명 알아내기
		String fname=mfile.getOriginalFilename();
		
		try {
			mfile.transferTo(new File(upDir, fname));//업로드 처리
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return map;
	}//------------------------

}////////////////////////////////////////











