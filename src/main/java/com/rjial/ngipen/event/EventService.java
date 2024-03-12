package com.rjial.ngipen.event;

import com.rjial.ngipen.auth.Level;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import com.rjial.ngipen.tiket.JenisTiket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Response<ListEventResponse> getAllEvents() throws Exception {
        Response<ListEventResponse> response = new Response<>();
        try {
            List<Event> eventList = eventRepository.findAll();
            ListEventResponse listEventResponse = new ListEventResponse(eventList);
            response.setData(listEventResponse);
            response.setStatusCode((long) HttpStatus.OK.value());
            response.setMessage("Returning list of events successfully!");
        } catch (Exception exc) {
            throw new Exception("Returning list of events failed! : " + exc.getMessage(), exc);
        }
        return response;
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
}
