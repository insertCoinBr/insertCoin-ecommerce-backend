package org.insertcoin.insertcoinpaymentservice.dtos;

public class EmailAttachmentDTO {
    private String id;
    private String type;
    private String content; // Base64
    private boolean inline;

    public EmailAttachmentDTO(String id, String type, String content, boolean inline) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.inline = inline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isInline() {
        return inline;
    }

    public void setInline(boolean inline) {
        this.inline = inline;
    }
}