package cl.duoc.usuarios.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class RecoverySender {
  private final JavaMailSender mail;

  public RecoverySender(JavaMailSender mail) {
    this.mail = mail;
  }

  public void send(String email, String token) {
    var msg = new SimpleMailMessage();
    msg.setFrom("no-reply@foro.local");
    msg.setTo(email);
    msg.setSubject("Foro Nexo - recuperación de cuenta");
    msg.setText(
        "Código de recuperación de un solo uso (válido 15 minutos):\n"
            + token
            + "\nSi no lo solicitaste, ignora este mensaje.");
    mail.send(msg);
  }
}
