/***************************************************
 * Objetivo: Serviço responsável pelo envio de e-mails
 * do sistema Vizinhança, incluindo boas-vindas com
 * credenciais de acesso para novos moradores.
 * Data: 10/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 ***************************************************/

package com.tcc_vizinhanca.vizinhanca.service.email;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private final EmailClient emailClient;
    private final String sender;

    public EmailService(
            @Value("${azure.communication.connection-string}") String connectionString,
            @Value("${azure.communication.sender}") String sender
    ) {

        this.emailClient = new EmailClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        this.sender = sender;
    }

    @Async
    public CompletableFuture<Boolean> sendWelcomeEmail(String to, String name, String password) {
        try {
            EmailMessage message = new EmailMessage()
                    .setSenderAddress(sender)
                    .setToRecipients(new EmailAddress(to))
                    .setSubject("Bem-vindo ao Vizinhança! Suas credenciais de acesso.")
                    .setBodyHtml(buildHtml(name, to, password));

            emailClient.beginSend(message).waitForCompletion();

            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            System.out.println("Erro ao enviar email: " +e.getMessage());

            return CompletableFuture.completedFuture(false);
        }

    }

    private String buildHtml(String name, String email, String password) {
        return """
        <!DOCTYPE html>
        <html lang="pt-BR">
        <head>
          <meta charset="UTF-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
          <style>
            body { margin: 0; padding: 0; background-color: #f4f6f9; font-family: Arial, sans-serif; }
            .wrapper { max-width: 600px; margin: 32px auto; background: #ffffff; border-radius: 12px; overflow: hidden; border: 1px solid #e0e6ef; }
            .banner { background-color: #1a4fa3; text-align: center; }
            .banner img { width: 100%%; height:100%%; object-fit: fill; }
            .body { padding: 32px 40px; }
            .greeting { font-size: 20px; font-weight: 600; color: #1a1a2e; margin: 0 0 12px; }
            .text { font-size: 15px; color: #4a5568; line-height: 1.7; margin: 0 0 20px; }
            .credentials { background-color: #e6f1fb; border-radius: 8px; padding: 20px 24px; margin: 24px 0; }
            .credential-row { display: flex; align-items: flex-start; gap: 12px; padding: 8px 0; }
            .credential-row + .credential-row { border-top: 1px solid #b5d4f4; }
            .cred-icon { font-size: 18px; color: #185fa5; margin-top: 2px; }
            .cred-label { font-size: 11px; color: #185fa5; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 2px; }
            .cred-value { font-size: 15px; color: #0c447c; font-weight: 500; }
            .cred-value.mono { font-family: 'Courier New', monospace; font-size: 18px; }
            .warning { font-size: 13px; color: #718096; line-height: 1.6; margin-top: 20px; padding-top: 20px; border-top: 1px solid #e8ecf0; }
            .footer { background-color: #f8fafc; padding: 16px 40px; text-align: center; font-size: 12px; color: #a0aec0; border-top: 1px solid #e8ecf0; }
          </style>
        </head>
        <body>
          <div class="wrapper">

            <div class="banner">
              <img
                src="https://github.com/user-attachments/assets/fc1138c1-c4a1-4a63-a09f-245e9e2a49b5"
                alt="Vizinhança"
                onerror="this.style.display='none'"
              />
            </div>

            <div class="body">
              <p class="greeting">Olá, %s! 👋</p>
              <p class="text">
                Seu cadastro no <strong>Vizinhança</strong> foi realizado com sucesso.
                Utilize as credenciais abaixo para acessar o sistema:
              </p>

              <div class="credentials">
                <div class="credential-row">
                  <div>
                    <div class="cred-label">E-mail</div>
                    <div class="cred-value">%s</div>
                  </div>
                </div>
                <div class="credential-row">
                  <div>
                    <div class="cred-label">Senha</div>
                    <div class="cred-value mono">%s</div>
                  </div>
                </div>
              </div>

              <p class="warning">
                🔒 Por segurança, recomendamos que você altere sua senha após o primeiro acesso.
                Caso tenha alguma dúvida, entre em contato com o administrador do seu condomínio.
              </p>
            </div>

            <div class="footer">
              © 2026 Vizinhança · Todos os direitos reservados<br/>
              Você recebeu este e-mail pois foi cadastrado no sistema.
            </div>

          </div>
        </body>
        </html>
        """.formatted(name, email, password);
    }
}