package com.thesaastech.note.service;

import com.thesaastech.note.domain.Note;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractService {

    public final static String USER_ID_COL = "userId";
    public final static String NOTE_ID_COL = "noteId";
    public final static String CONTENT_COL = "content";
    public final static String CREATE_AT_COL = "createdAt";
    public final static String ATTACHMENT_COL = "attachment";

    @ConfigProperty(name = "DB_TABLE_NAME")
    String tableName;

    public String getTableName() {
        return tableName;
    }

    protected PutItemRequest putItemRequest(Note note) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(USER_ID_COL, AttributeValue.builder().s(note.getUserId()).build());
        item.put(NOTE_ID_COL, AttributeValue.builder().s(note.getNoteId()).build());
        item.put(CONTENT_COL, AttributeValue.builder().s(note.getContent()).build());
        item.put(CREATE_AT_COL, AttributeValue.builder().s(note.getCreateAt()).build());
        item.put(ATTACHMENT_COL, AttributeValue.builder().s(note.getAttachment()).build());


        return PutItemRequest.builder()
                .tableName(getTableName())
                .item(item)
                .build();
    }

    protected GetItemRequest getItemRequest(String userId, String noteId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(USER_ID_COL, AttributeValue.builder().s(userId).build());
        key.put(NOTE_ID_COL, AttributeValue.builder().s(noteId).build());


        return GetItemRequest.builder()
                .tableName(getTableName())
                .key(key)
                .attributesToGet(USER_ID_COL, NOTE_ID_COL, CONTENT_COL, CREATE_AT_COL, ATTACHMENT_COL)
                .build();
    }

    protected ScanRequest scanRequest() {
        return ScanRequest.builder().tableName(getTableName())
                .attributesToGet(USER_ID_COL, NOTE_ID_COL, CONTENT_COL, CREATE_AT_COL, ATTACHMENT_COL).build();
    }

    protected DeleteItemRequest deleteRequest(String userId, String noteId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(USER_ID_COL, AttributeValue.builder().s(userId).build());
        key.put(NOTE_ID_COL, AttributeValue.builder().s(noteId).build());

        return DeleteItemRequest.builder().tableName(getTableName()).key(key).build();
    }


}
