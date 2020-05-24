package com.thesaastech.note.service;

import com.thesaastech.note.domain.Note;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class NoteService extends AbstractService {

    @Inject
    DynamoDbClient dynamoDbClient;

    public String add(Note note) {
        dynamoDbClient.putItem(putItemRequest(note));

        return note.getNoteId();
    }

    public Note get(String userId, String noteId) {
        return Note.from(dynamoDbClient.getItem(getItemRequest(userId, noteId)).item());
    }

    public List<Note> findAll() {
        return dynamoDbClient.scanPaginator(scanRequest()).items().stream()
                .map(Note::from)
                .collect(Collectors.toList());
    }

    public String delete(String userId, String noteId) {
        dynamoDbClient.deleteItem(deleteRequest(userId, noteId));

        return noteId;
    }
}
