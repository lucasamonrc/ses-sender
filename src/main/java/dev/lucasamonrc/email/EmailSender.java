package dev.lucasamonrc.email;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.regions.Regions;

import java.util.Date;

public class EmailSender {
    public EmailResult handleRequest(EmailRequest request, Context context) {
        EmailResult response = new EmailResult();
        LambdaLogger logger = context.getLogger();
        logger.log("Entering send_email");

        try {
            AmazonSimpleEmailService client =
                    AmazonSimpleEmailServiceClientBuilder.standard()
                            .withRegion(Regions.US_WEST_2).build();

            Destination destination = new Destination().withToAddresses(request.to);
            Content subject = new Content().withCharset("UTF-8").withData(request.subject);
            Content htmlContent = new Content().withCharset("UTF-8").withData(request.htmlBody);
            Content textContent = new Content().withCharset("UTF-8").withData(request.textBody);

            SendEmailRequest email = new SendEmailRequest()
                    .withSource(request.from)
                    .withDestination(destination)
                    .withMessage(new Message()
                                .withSubject(subject)
                                .withBody(new Body()
                                        .withHtml(htmlContent)
                                        .withText(textContent)));

            client.sendEmail(email);

            Date now = new Date();
            logger.log("Email sent!");

            response.message = "Email sent to " + request.to + " from " + request.from;
            response.timestamp = now.toString();
            return response;
        } catch (Exception ex) {
            logger.log("The email was not sent. Error message: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
        finally {
            logger.log("Leaving send_email");
        }
    }

}