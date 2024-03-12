package com.rjial.ngipen.event;

import com.rjial.ngipen.common.Response;
import com.rjial.ngipen.tiket.JenisTiket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
}
