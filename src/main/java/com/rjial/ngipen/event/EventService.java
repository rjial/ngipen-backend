package com.rjial.ngipen.event;

import com.rjial.ngipen.auth.Level;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import com.rjial.ngipen.tiket.JenisTiket;
import com.rjial.ngipen.tiket.JenisTiketRepository;
import com.rjial.ngipen.tiket.Tiket;
import com.rjial.ngipen.tiket.TiketRepository;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private JenisTiketRepository jenisTiketRepository;
    @Autowired
    private TiketRepository tiketRepository;

    public Response<Page<Event>> getAllEvents(int page, int size) throws Exception {
        Response<Page<Event>> response = new Response<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Event> eventList = eventRepository.findAll(pageable);
            response.setData(eventList);
            response.setStatusCode((long) HttpStatus.OK.value());
            response.setMessage("Returning list of events successfully!");
        } catch (Exception exc) {
            throw new Exception("Returning list of events failed! : " + exc.getMessage(), exc);
        }
        return response;
    }

    public Page<Event> getPemegangAcaraEvent(int page, int size, User user) throws BadRequestException {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Event> allByPemegangEvent;
            if(user.getLevel().equals(Level.PEMEGANG_ACARA)) {
                allByPemegangEvent = eventRepository.findAllByPemegangEvent(user.getUuid(), pageable);
            } else if(user.getLevel().equals(Level.ADMIN)) {
                allByPemegangEvent  = eventRepository.findAll(pageable);
            } else {
                throw new AuthorizationServiceException("Anda bukan pemegang event atau admin");
            }
            return allByPemegangEvent;
        } catch (Exception exc) {
            throw exc;
        }
    }

    public Response<EventItemResponse> getEventByUUID(UUID uuid) throws Exception {
        Response<EventItemResponse> response = new Response<>();
        try {
            Event event = eventRepository.findEventByUuid(uuid);
            EventItemResponse eventItemResponse = new EventItemResponse();
            eventItemResponse.setEvent(event);
            response.setData(eventItemResponse);
            response.setStatusCode((long) HttpStatus.OK.value());
            response.setMessage(String.format("Returning %s successfully!", event.getName()));
        } catch (Exception exc) {
            throw new Exception("Returning list of events failed! : " + exc.getMessage(), exc);
        }
        return response;
    }

    public Response<EventJenisTiketResponse> getJenisTiketByUUID(UUID uuid) throws Exception {
        Response<EventJenisTiketResponse> response = new Response<>();
        try {
            List<JenisTiket> jenisTiketByUuid = eventRepository.findJenisTiketByUuid(uuid);
            EventJenisTiketResponse eventJenisTiketResponse = new EventJenisTiketResponse();
            eventJenisTiketResponse.setJenisTikets(jenisTiketByUuid);
            response.setData(eventJenisTiketResponse);
            response.setStatusCode((long) HttpStatus.OK.value());
            response.setMessage("Returning list of jenis tiket successfully!");
        } catch (Exception exc) {
            throw new Exception("Returning list of jenis tiket failed! : " + exc.getMessage(), exc);
        }
        return response;
    }

    public JenisTiket getJenisTiketDetail(String uuid, Long id) throws Exception {
        Event eventByUuid = eventRepository.findEventByUuid(UUID.fromString(uuid));
        JenisTiket jenisTiket = jenisTiketRepository.findById(id).orElseThrow();
        if (jenisTiket.getEvent().getId().equals(eventByUuid.getId())) {
            return jenisTiket;
        } else {
            throw new BadRequestException("Jenis tiket ini bukan dari event ini");
        }
    }

    public Response<EventItemResponse> insertEvent(AddEventRequest request, User user) throws Exception {
        Response<EventItemResponse> response = new Response<>();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        if (user.getLevel() != Level.ADMIN) {
            if (user.getLevel() != Level.PEMEGANG_ACARA) {
                throw new BadCredentialsException("Membuat event harus mempunyai role Admin atau Pemegang Event");
            }
        }
        try {
            Event event = new Event();
            event.setName(request.getName());
            event.setUuid(UUID.randomUUID());
            event.setTanggalAwal(LocalDate.parse(request.getTanggalAwal(), dateFormat));
            event.setWaktuAwal(LocalTime.parse(request.getWaktuAwal(), timeFormat));
            event.setLokasi(request.getLokasi());
            event.setDesc(request.getDesc());
            event.setPersen(request.getPersen());
            event.setPemegangEvent(user);
            event.setVerifyEvent(false);
            Event eventSaved = eventRepository.save(event);
            if (eventSaved.getId() > 0) {
                response.setMessage("Event successfully created");
                response.setStatusCode((long) HttpStatus.OK.value());
            } else {
                throw new DataIntegrityViolationException("Event failed created");
            }
            EventItemResponse eventItemResponse = new EventItemResponse();
            eventItemResponse.setEvent(event);
            response.setData(eventItemResponse);
            return response;
        } catch (Exception exc) {
            throw new Exception("Event failed created", exc);
        }
    }

    public EventItemResponse verifyEvent(String uuid, User user) throws Exception {
        if (!user.getLevel().equals(Level.ADMIN)) throw new BadCredentialsException("Hanya administrator saja yang boleh menverifikasi event ini");
        try {
            Event eventByUuid = eventRepository.findEventByUuid(UUID.fromString(uuid));
            eventByUuid.setVerifyEvent(true);
            EventItemResponse eventItemResponse = new EventItemResponse();
            eventItemResponse.setEvent(eventRepository.save(eventByUuid));
            return eventItemResponse;
        } catch (Exception exc) {
            throw new Exception("Gagal menverifikasi event", exc);
        }
    }

    public JenisTiket insertJenisTiket(String uuid, User user, AddJenisTiketRequest addJenisTiketRequest) throws Exception {
        if (user.getLevel() == Level.PEMEGANG_ACARA || user.getLevel() == Level.ADMIN) {
            Event eventUUID = eventRepository.findEventByUuid(UUID.fromString(uuid));
            if (eventUUID.getPemegangEvent().getId().equals(user.getId())) {
                JenisTiket jenisTiket = new JenisTiket();
                jenisTiket.setEvent(eventUUID);
                jenisTiket.setNama(addJenisTiketRequest.getName());
                jenisTiket.setHarga(addJenisTiketRequest.getHarga());
                JenisTiket savedJenisTiket = jenisTiketRepository.save(jenisTiket);
                if (savedJenisTiket.getId() > 0) {
                    return savedJenisTiket;
                } else {
                    throw new DataIntegrityViolationException("Tambah jenis tiket gagal!");
                }
            } else {
                throw new BadRequestException("Anda bukan pemegang acara ini!");
            }
        } else {
            throw new Exception("Anda bukan pemegang acara!");
        }
    }

    public void deleteJenisTiket(String uuid, Long id, User user) throws Exception {
        if (user.getLevel() == Level.PEMEGANG_ACARA || user.getLevel() == Level.ADMIN) {
            Event event = eventRepository.findEventByUuid(UUID.fromString(uuid));
            if (event.getPemegangEvent().getId().equals(user.getId())) {
                JenisTiket jenisTiket = jenisTiketRepository.findById(id).orElseThrow();
                jenisTiketRepository.delete(jenisTiket);
            } else {
                throw new BadRequestException("Anda bukan pemegang acara ini!");
            }
        } else {
            throw new BadRequestException("Anda bukan pemegang acara atau admin!");
        }
    }

    public JenisTiket updateJenisTiket(String uuid, User user, Long id, UpdateJenisTiketRequest updateJenisTiketRequest) throws Exception {
        if (user.getLevel() == Level.PEMEGANG_ACARA || user.getLevel() == Level.ADMIN) {
            Event eventUUID = eventRepository.findEventByUuid(UUID.fromString(uuid));
            if (eventUUID.getPemegangEvent().getId().equals(user.getId())) {
                JenisTiket jenisTiket = jenisTiketRepository.findById(id).orElseThrow();
                jenisTiket.setEvent(eventUUID);
                jenisTiket.setNama(updateJenisTiketRequest.getName());
                jenisTiket.setHarga(updateJenisTiketRequest.getHarga());
                JenisTiket savedJenisTiket = jenisTiketRepository.save(jenisTiket);
                if (savedJenisTiket.getId() > 0) {
                    return savedJenisTiket;
                } else {
                    throw new DataIntegrityViolationException("Update jenis tiket gagal!");
                }
            } else {
                throw new BadRequestException("Anda bukan pemegang acara ini!");
            }
        } else {
            throw new Exception("Anda bukan pemegang acara!");
        }
    }

    public Page<Tiket> getTiketFromPemegangAcara(UUID uuid, int page, int size,  User user) throws AuthorizationServiceException {
        if (user.getLevel() == Level.PEMEGANG_ACARA) {
            Pageable pageable = PageRequest.of(page, size);
            Event event = eventRepository.findEventByUuid(uuid);
            if (event.getPemegangEvent().getId().equals(user.getId())) {
                log.info(event.getName());
                return tiketRepository.findTiketByPemegangAcara(event, pageable);
            } else {
                throw new AuthorizationServiceException("Anda bukan pemegang acara ini!");
            }
        } else if (user.getLevel() == Level.ADMIN) {
            Pageable pageable = PageRequest.of(page, size);
            Event event = eventRepository.findEventByUuid(uuid);
            log.info(event.getName());
            return tiketRepository.findTiketByAdmin(event, pageable);
        } else {
            throw new AuthorizationServiceException("Anda bukan pemegang acara ini!");
        }
    }
}
