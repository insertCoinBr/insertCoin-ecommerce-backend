package org.insertcoin.insertcoinpaymentservice.dtos;

import java.util.List;
import java.util.Map;

public class EmailMessageDTO {
    private String type;
    private String to;
    private String subject;
    private String template;
    private Map<String, Object> variables;
    private List<EmailAttachmentDTO> attachments;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public List<EmailAttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<EmailAttachmentDTO> attachments) {
        this.attachments = attachments;
    }
}
