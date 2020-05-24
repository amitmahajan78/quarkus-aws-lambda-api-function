package com.thesaastech.note.domain;

import com.thesaastech.note.service.AbstractService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Objects;

@RegisterForReflection
public class Note {

    String userId;
    String noteId;
    String content;


    String createAt;
    String attachment;

    public Note() {
    }

    public Note(String userId, String noteId, String content, String createAt, String attachment) {
        this.userId = userId;
        this.noteId = noteId;
        this.content = content;
        this.createAt = createAt;
        this.attachment = attachment;
    }

    public static Note from(Map<String, AttributeValue> item) {
        Note note = new Note();

        if (item != null && !item.isEmpty()) {

            note.setUserId(item.get(AbstractService.USER_ID_COL).s());
            note.setNoteId(item.get(AbstractService.NOTE_ID_COL).s());
            note.setContent(item.get(AbstractService.CONTENT_COL).s());
            note.setCreateAt(item.get(AbstractService.CREATE_AT_COL).s());
            note.setAttachment(item.get(AbstractService.ATTACHMENT_COL).s());
        }
        return note;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(userId, note.userId) &&
                Objects.equals(noteId, note.noteId) &&
                Objects.equals(content, note.content) &&
                Objects.equals(createAt, note.createAt) &&
                Objects.equals(attachment, note.attachment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, noteId, content, createAt, attachment);
    }

    @Override
    public String toString() {
        return "Note{" +
                "userId='" + userId + '\'' +
                ", noteId='" + noteId + '\'' +
                ", content='" + content + '\'' +
                ", createAt='" + createAt + '\'' +
                ", attachment='" + attachment + '\'' +
                '}';
    }
}
