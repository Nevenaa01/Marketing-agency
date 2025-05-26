package com.bsep2024.MarketingAgency;

import com.bsep2024.MarketingAgency.notifications.NotificationService;
import com.bsep2024.MarketingAgency.utils.LogReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MarketingAgencyApplication {

	public static void main(String[] args) {
		ApplicationContext context =SpringApplication.run(MarketingAgencyApplication.class, args);

		//ne moze drugacije umro sam
		NotificationService service=context.getBean(NotificationService.class);
		LogReader l=new LogReader();
		l.setService(service);
	}

}
