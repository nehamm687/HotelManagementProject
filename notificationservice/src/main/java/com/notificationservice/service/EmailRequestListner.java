package com.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notificationservice.constants.AppConstants;
import com.notificationservice.dto.EmailRequest;
@Service
public class EmailRequestListner {
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	 @KafkaListener(topics = AppConstants.TOPIC, groupId = "group_email")
	    public void consumeMessage(String emailRequest) {
	       ObjectMapper mapper = new ObjectMapper();
	       try {
			EmailRequest emailContent = mapper.readValue(emailRequest, EmailRequest.class);
			SimpleMailMessage sm = new SimpleMailMessage();
			sm.setTo(emailContent.getTo());
			sm.setSubject(emailContent.getSubject());
			sm.setText(emailContent.getBody());
			
			javaMailSender.send(sm);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
}
