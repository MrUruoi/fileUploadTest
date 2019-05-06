package com.example.demo.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.util.MediaUtils;
import com.example.demo.util.UploadFileUtil;


@Controller
public class FileController {
		
	private final String UPLOADPATH = "/Users/dopamine100/desktop";
	
	
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String fileUpload() {

        return "uploadForm";
    }
    
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<String> fileUpload(MultipartFile uploadfile){
    	ResponseEntity<String> entity = null;
    	
    	try {
    		String fileName = uploadfile.getOriginalFilename();
    		String filePath = UPLOADPATH + File.separator + fileName;
    		
    		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
    		bos.write(uploadfile.getBytes());
    		bos.close();
    		
    		entity = new ResponseEntity<String>("SUCCESS" ,HttpStatus.OK);
    	}catch (Exception e) {
    		entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    	
    	return entity;
    }
    
    @RequestMapping(value = "/upload3", method = RequestMethod.POST)
    public ResponseEntity<String> fileUpload3(MultipartFile file) throws Exception{
    	
    	return new ResponseEntity<>(UploadFileUtil.UploadFile(UPLOADPATH, file.getOriginalFilename(), file.getBytes() ), HttpStatus.CREATED);
    }
    
    @RequestMapping("/displayFile")
    public ResponseEntity<byte[]> displayFile(String fileName) throws Exception {
    	
    	InputStream in = null;
    	ResponseEntity<byte[]> entity = null;
    	
    	try {
    		String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
    		
    		MediaType mType = MediaUtils.getMediaType(formatName);
    		
    		HttpHeaders headers = new HttpHeaders();
    		
    		in = new FileInputStream(UPLOADPATH+fileName);
    		
    		if(mType != null)
    			headers.setContentType(mType);
    		else {
    			fileName = fileName.substring(fileName.indexOf("_")+1);
    			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    			headers.add("Content-Disposition", "attachment; filename=\"" +new String(fileName.getBytes("UTF-8"), "ISO-8859-1")+"\"");
    		}
    		
    		entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in) , headers, HttpStatus.CREATED);
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    		entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
    	}finally {
    		in.close();
    	}
    	return entity;
    	
    }
    
    @RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
    public ResponseEntity<String> deleteFile(String fileName) throws Exception {
    	String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
    	
    	MediaType mType = MediaUtils.getMediaType(formatName);
    	
    	if(mType != null) {
    		String front = fileName.substring(0,12);
    		String end = fileName.substring(14);
    		new File(UPLOADPATH + (front+end).replace('/', File.separatorChar)).delete();
    	}
    	
    	new File(UPLOADPATH + fileName.replace('/', File.separatorChar)).delete();
    	
    	return new ResponseEntity<String>("deleted", HttpStatus.OK);
    	
    }
    
    
	
}
