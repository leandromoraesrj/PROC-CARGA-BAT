package br.com.proccarga.core.util;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import br.com.proccarga.core.enumeration.PropriedadesEnum;

/**
 * Classe utilitária para envio de e-mails
 * 
 * @author Leandro Moraes
 */
public final class EmailUtil {

	private static final Logger LOGGER = Logger.getLogger(EmailUtil.class);

	private static Session sessaoSMTP;

	/**
	 * Construtor privado vazio para evitar que a classe seja indevidamente /
	 * desnecessariamente instanciada
	 */
	private EmailUtil() {
	}

	static {
		Properties configuracaoSMTP = new Properties();
		configuracaoSMTP.put("mail.smtp.host", PropriedadesEnum.EMAIL_SMTP_ENDERECO.get());
		configuracaoSMTP.put("mail.smtp.port", PropriedadesEnum.EMAIL_SMTP_PORTA.get());
		configuracaoSMTP.put("mail.smtp.auth", "false");
		sessaoSMTP = Session.getDefaultInstance(configuracaoSMTP, null);
		sessaoSMTP.setDebug(false);
	}

	/**
	 * Envia um e-mail com anexo
	 * 
	 * @param destinatario
	 *            - Destinatário do e-mail
	 * @param assunto
	 *            - Assunto do e-mail
	 * @param remetente
	 *            - Remetente do e-mail
	 * @param mensagem
	 *            - Mensagem do e-mail
	 * @return Verdadeiro quando o envio ocorre com sucesso
	 */
	public static boolean enviar(String destinatario, String assunto, String remetente, String mensagem) {

		LOGGER.info("ENVIANDO E-MAIL");
		try {

			Message message = new MimeMessage(sessaoSMTP);
			Multipart multipart = new MimeMultipart();

			// Corpo do email
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(mensagem, "text/plain; charset=utf-8");
			multipart.addBodyPart(messageBodyPart);

			message.setFrom(new InternetAddress(remetente));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
			message.setSubject(assunto);
			message.setContent(multipart);

			Transport.send(message);

			return Boolean.TRUE;

		} catch (MessagingException e) {

			LOGGER.error("FALHA NO ENVIO DO E-MAIL USANDO SMTP", e);
			return Boolean.FALSE;

		}

	}

}
