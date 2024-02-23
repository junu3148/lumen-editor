package com.lumeneditor.www.domain.auth.email;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmailMessage {
    private String to;
    private String subject;
    private String message;
    private String pdfFilePath;
    private byte[] attachment; // 첨부 파일의 바이트 배열
    private String filename;   // 첨부 파일명
}