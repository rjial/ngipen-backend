package com.rjial.ngipen;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.auth.UserRepository;
import com.rjial.ngipen.event.Event;
import com.rjial.ngipen.event.EventRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import net.datafaker.providers.entertainment.DetectiveConan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@SpringBootApplication
@Slf4j
public class BackendApplication {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {
//			Faker faker = new Faker();
//			Event event = new Event();
//			User adminUser = userRepository.findByEmail("admin@rjial.dev").orElseThrow();
//			DetectiveConan detectiveConan = faker.detectiveConan();
//			event.setName(detectiveConan.gadgets());
//			event.setUuid(UUID.randomUUID());
//			event.setTanggalAwal(LocalDate.now());
//			event.setWaktuAwal(LocalTime.now());
//			event.setWaktuAkhir(LocalTime.now().plusHours(8L));
//			event.setLokasi(detectiveConan.vehicles());
//			event.setPersen(faker.number().randomNumber());
//			event.setDesc(detectiveConan.characters());
//			event.setPemegangEvent(adminUser);
//			Event save = eventRepository.save(event);
//			if (save.getId() > 0) {
//				log.info("Adding event - {}", detectiveConan.gadgets());
//			}
		};
	}
}
