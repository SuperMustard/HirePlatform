package com.hanxin.utils;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendEmailUtils {
    @Autowired
    private AzureEmailProperties azureEmailProperties;

    public void SendEmail(String emailAddress, String validCode) {
        String connectionString = "endpoint=" + azureEmailProperties.getEndPoint() + ";accesskey=" + azureEmailProperties.getAccessKey();

        EmailClient emailClient = new EmailClientBuilder().connectionString(connectionString).buildClient();

        EmailAddress toAddress = new EmailAddress(emailAddress);



        EmailMessage emailMessage = new EmailMessage()
                .setSenderAddress("DoNotReply@ab03d2b4-ca3b-4d3c-8661-f275ea275acf.azurecomm.net")
                .setToRecipients(toAddress)
                .setSubject("测试电子邮件")
                .setBodyPlainText("您的验证码是: " + validCode);



        SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage, null);
        PollResponse<EmailSendResult> result = poller.waitForCompletion();

//        EmailAsyncClient emailClient = new EmailClientBuilder()
//                .connectionString(connectionString)
//                .buildAsyncClient();
//
//        EmailMessage message = new EmailMessage()
//                .setSenderAddress("<donotreply@xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.azurecomm.net>")
//                .setToRecipients("<emailalias@emaildomain.com>")
//                .setSubject("Welcome to Azure Communication Services Email")
//                .setBodyPlainText("This email message is sent from Azure Communication Services Email using the Java SDK.");
//
//        try
//        {
//            SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(message).getSyncPoller(); // This will send out the initial request to send an email
//
//            PollResponse<EmailSendResult> pollResponse = null;
//
//            Duration timeElapsed = Duration.ofSeconds(0);
//            Duration POLLER_WAIT_TIME = Duration.ofSeconds(10);
//
//            // Polling is done manually to avoid blocking the application in case of an error
//            while (pollResponse == null
//                    || pollResponse.getStatus() == LongRunningOperationStatus.NOT_STARTED
//                    || pollResponse.getStatus() == LongRunningOperationStatus.IN_PROGRESS)
//            {
//                pollResponse = poller.poll();
//                // The operation ID can be retrieved as soon as .poll() is called on the poller
//                System.out.println("Email send poller status: " + pollResponse.getStatus() + ", operation id: " + pollResponse.getValue().getId());
//
//                Thread.sleep(POLLER_WAIT_TIME.toMillis());
//                timeElapsed = timeElapsed.plus(POLLER_WAIT_TIME);
//
//                if (timeElapsed.compareTo(POLLER_WAIT_TIME.multipliedBy(18)) >= 0)
//                {
//                    throw new RuntimeException("Polling timed out.");
//                }
//            }
//
//            if (poller.getFinalResult().getStatus() == EmailSendStatus.SUCCEEDED)
//            {
//                System.out.printf("Successfully sent the email (operation id: %s)", poller.getFinalResult().getId());
//            }
//            else
//            {
//                throw new RuntimeException(poller.getFinalResult().getError().getMessage());
//            }
//        }
//        catch (Exception exception)
//        {
//            System.out.println(exception.getMessage());
//        }
    }
}
