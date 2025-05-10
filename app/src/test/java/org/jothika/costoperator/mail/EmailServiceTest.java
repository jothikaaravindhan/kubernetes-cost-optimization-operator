package org.jothika.costoperator.mail;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import org.jothika.costoperator.CostOptimizationRule;
import org.jothika.costoperator.TestMockUtils;
import org.jothika.costoperator.metrics.MetricType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@EnableKubernetesMockClient
class EmailServiceTest {

    TestMockUtils testMockUtils;
    @Mock private JavaMailSender mailSender;
    @Mock private TemplateEngine templateEngine;
    @Mock private MimeMessage mimeMessage;
    @InjectMocks private EmailService emailService;
    private KubernetesMockServer mockServer;
    private CostOptimizationRule rule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailService(mailSender, templateEngine, "from@test.com");
        testMockUtils = new TestMockUtils(mockServer);
        String namespace = "test-namespace";
        String podName = "test-pod";
        String ruleName = "test-rule";
        int threshold = 80;
        rule =
                testMockUtils.getCostOptimizationRule(
                        ruleName, namespace, podName, MetricType.CPU, threshold);
        rule.getSpec().setNotificationEmail("to@mail.com");
    }

    @Test
    void sendAlertOnPodThreshold_sendsEmailSuccessfully() throws Exception {

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("pod-alert"), any(Context.class)))
                .thenReturn("<html>Email</html>");

        emailService.sendAlertOnPodThreshold(rule, 42.0);

        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("pod-alert"), any(Context.class));
    }

    @Test
    void sendAlertOnPodThreshold_handlesMessagingException() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html>Email</html>");
        doThrow(new MailSendException("fail")).when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> emailService.sendAlertOnPodThreshold(rule, 42.0));
    }

    @Test
    void sendAlertOnPodThreshold_handlesUnsupportedEncodingException() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html>Email</html>");

        // Spy to throw UnsupportedEncodingException from sendSimpleEmail
        EmailService spyService = Mockito.spy(emailService);
        doThrow(new UnsupportedEncodingException("fail"))
                .when(spyService)
                .sendSimpleEmail(anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> spyService.sendAlertOnPodThreshold(rule, 42.0));
    }
}
