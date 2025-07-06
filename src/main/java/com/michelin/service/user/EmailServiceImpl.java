package com.michelin.service.user;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender mailSender;

//    public void sendPasswordResetEmail(String toEmail, String token) {
//        String resetLink = "http://localhost:5173/reset-password?token=" + token;
//        String subject = "비밀번호 재설정 링크";
//        String text = "다음 링크를 클릭해서 비밀번호를 재설정하세요:\n" + resetLink;
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject(subject);
//        message.setText(text);
//
//        mailSender.send(message);
//    }
    
    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        String subject = "비밀번호 재설정 링크";

        String htmlContent = """
            <html>
                <body>
                    <p>안녕하세요, 비밀번호 재설정을 요청하셨습니다.</p>
                    <p>
                        아래 버튼을 클릭하여 비밀번호를 재설정하세요:
                    </p>
                    <p>
                        <a href="%s" style="padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;">
                            비밀번호 재설정하러 가기
                        </a>
                    </p>
                    <p>이 링크는 1시간 동안 유효합니다.</p>
                </body>
            </html>
        """.formatted(resetLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // HTML로 보내기

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 중 오류 발생", e);
        }
    }

}