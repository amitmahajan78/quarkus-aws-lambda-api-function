package com.thesaastech.note.function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesaastech.note.domain.Note;
import com.thesaastech.note.service.NoteService;
import org.apache.commons.codec.binary.Base64;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named("processing")
public class NoteProcessing implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOGGER = Logger.getLogger(NoteProcessing.class);
    @Inject
    NoteService noteService;
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        Map<String, String> query = request.getQueryStringParameters();

        LOGGER.info(String.format("[%s] Processed data", request));

        String httpMethod = request.getHttpMethod();
        String result = "";
        Note note;
        List<Note> noteList;
        int httpCode = 500;

        switch (httpMethod) {

            case "GET":
                Map<String, String> queryStringParameters = request.getPathParameters();

                String noteId = null;

                if (queryStringParameters != null) {
                    noteId = queryStringParameters.get("noteId");
                }

                if (noteId == null || noteId.length() == 0) {

                    LOGGER.info("Getting all notes");
                    noteList = noteService.findAll();
                    LOGGER.info("GET: " + noteList);
                    try {
                        result = mapper.writeValueAsString(noteList);
                    } catch (JsonProcessingException exc) {
                        LOGGER.error(exc);
                    }

                } else {
                    LOGGER.info("Getting note for noteId : " + noteId);
                    note = noteService.get(request.getRequestContext().getIdentity().getUser() == null ? "Local-User" : request.getRequestContext().getIdentity().getUser(),
                            noteId);
                    LOGGER.info("GET: " + note);

                    try {
                        result = mapper.writeValueAsString(note);
                    } catch (JsonProcessingException exc) {
                        LOGGER.error(exc);
                    }
                }
                httpCode = 200;
                break;
            case "POST":
                String body = request.getIsBase64Encoded() ? decodeBase64RequestBody(request.getBody()) : request.getBody();
                LOGGER.info("Request Body :  " + body);
                try {
                    Note tempNote = mapper.readValue(body, Note.class);
                    tempNote.setUserId(request.getRequestContext().getIdentity().getUser() == null ? "Local-User" : request.getRequestContext().getIdentity().getUser());
                    tempNote.setNoteId(UUID.randomUUID().toString());
                    tempNote.setCreateAt(LocalDateTime.now().toString());

                    LOGGER.info("POST: " + tempNote);
                    String tmpId = noteService.add(tempNote);

                    result = mapper.writeValueAsString(tempNote);
                } catch (JsonProcessingException exc) {
                    LOGGER.error(exc);
                }
                httpCode = 200;
                break;
            case "DELETE":
                Map<String, String> pathParameters = request.getPathParameters();

                if (pathParameters != null) {
                    String id = pathParameters.get("noteId");
                    String tmpId = noteService.delete(request.getRequestContext().getIdentity().getUser() == null
                                    ? "Local-User"
                                    : request.getRequestContext().getIdentity().getUser(),
                            id);

                    LOGGER.info("DELETE: " + tmpId);

                    result = "{\n" +
                            "    \"status\": true\n" +
                            "}";
                }
                httpCode = 200;
                break;
        }

        return new APIGatewayProxyResponseEvent().withBody(result).withStatusCode(httpCode);
    }

    private String decodeBase64RequestBody(String requestBody) {

        byte[] decodedBytes = Base64.decodeBase64(requestBody);

        return new String(decodedBytes);
    }
}
