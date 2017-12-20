package com.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@EnableScheduling
@SpringBootApplication
public class WebsocketApplication {
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@GetMapping("/")
	public String index() {
		return "socket";
	}

	@GetMapping("/websocket")
	public String webscoket() {
		return "websocket";
	}

	@MessageMapping("/send")
	@SendTo("/topic/send")
	public SocketMessage send(SocketMessage message) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		message.date = df.format(new Date());
		return message;
	}

	@Scheduled(fixedRate = 1000)
	@SendTo("/topic/callback")
	public Object callback() throws Exception {
		// 发现消息
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		messagingTemplate.convertAndSend("/topic/callback", df.format(new Date()));
		return "callback";
	}
	public static void main(String[] args) {
		SpringApplication.run(WebsocketApplication.class, args);
	}
}
