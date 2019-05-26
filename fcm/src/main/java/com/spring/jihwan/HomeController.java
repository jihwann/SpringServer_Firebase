package com.spring.jihwan;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;




/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private static final String FCM_TOPIC_FOR_NOTICE = "notice-for-all";
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		try {
			fcmTest();
			logger.info("fcmTest() is successed");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("fcmTest() is failed");
		}
		
		return "home";
	}
	
	
	//@RequestMapping(value="/fcmTest", method=RequestMethod.GET, produces="text/plain;charset=UTF-8")
	@RequestMapping(value="/fcmtest", method=RequestMethod.POST, produces="text/plain;charset=UTF-8")
    public void fcmTest() throws Exception {
        try {    
        	
            // C:/Users/LG/eclipse-workspace/Spring_FCM_Fush_Server/src/main/webapp/resources/google/seoulsky-c5295-firebase-adminsdk-9enbm-6973550e3b.json
        	// path OK
        	//String path = "C:/Users/LG/eclipse-workspace/fcm/src/main/webapp/resources/google/seoulsky-c5295-firebase-adminsdk-9enbm-6973550e3b.json";
        	// path for aws
        	String path = "/var/lib/tomcat9/webapps/fcm/resources/google/seoulsky-c5295-firebase-adminsdk-9enbm-6973550e3b.json";
            String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
            String[] SCOPES = { MESSAGING_SCOPE };
            
            GoogleCredential googleCredential = GoogleCredential
                                .fromStream(new FileInputStream(path))
                                .createScoped(Arrays.asList(SCOPES));
            googleCredential.refreshToken();
                                
            HttpHeaders headers = new HttpHeaders();
            headers.add("content-type" , MediaType.APPLICATION_JSON_VALUE);
            headers.add("Authorization", "Bearer " + googleCredential.getAccessToken());
           
            JSONObject notification = new JSONObject();
            notification.put("body", "TEST");
            notification.put("title", "TEST");
            
            // 테스트 용도로 추가
            notification.put("message", "background");
            
            JSONObject message = new JSONObject();
            
            //message.put("notification", notification);
            message.put("data", notification);
            
            //message.put("token", "dD1QrDbk-70:APA91bE4rJR3fGY6s79_GLR8Wj-NL_cReXZBa1EUF6fqiG0kL8FtvXdDkK6KrMYuDT6rVIWZg0bPhuvNg3qig_LzZkv7ZHLrZ7SQiobkVnORI6UPivwN5217Ub3FHibGkJXNko92dczZ");
            message.put("topic", FCM_TOPIC_FOR_NOTICE);
            
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("message", message);
            
            //HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(jsonParams, headers);
            // JSONObject 타입 캐스팅 오류나서 아래처럼 바꿈
            HttpEntity httpEntity = new HttpEntity(jsonParams.toJSONString(), headers);
            RestTemplate rt = new RestTemplate();    
            logger.info("httpEntity : " + jsonParams.toJSONString() + ", " + headers);
            
            ResponseEntity<String> res = rt.exchange("https://fcm.googleapis.com/v1/projects/seoulsky-c5295/messages:send"
                    , HttpMethod.POST
                    , httpEntity
                    , String.class);
            
        
            if (res.getStatusCode() != HttpStatus.OK) {
            	logger.info("fcmTest() is successed2");
                logger.info("FCM-Exception");
                logger.info(res.getStatusCode().toString());
                logger.info(res.getHeaders().toString());
                logger.info(res.getBody().toString());
                
            } else {
            	logger.info("HTTPStatus.OK");
                logger.info(res.getStatusCode().toString());
                logger.info(res.getHeaders().toString());
                logger.info(res.getBody().toLowerCase());
                
            }
        } catch (Exception e) {
        	logger.info("fcmTest() is failed2");
            e.printStackTrace();
        }
    }

	
	
}
