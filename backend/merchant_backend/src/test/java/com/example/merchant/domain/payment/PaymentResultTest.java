package com.example.merchant.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.merchant.MerchantApplication;
import com.example.merchant.util.credential.CredentialUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ContextConfiguration(classes = MerchantApplication.class)
@AutoConfigureMockMvc
    public class PaymentResultTest {

    private static final Logger log = LoggerFactory.getLogger(PaymentRequestTest.class);

    private final WebApplicationContext context;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CredentialUtil credentialUtil;
    private String POS_MER_API_KEY;

    @Autowired
    public PaymentResultTest(WebApplicationContext context, MockMvc mockMvc, ObjectMapper objectMapper, CredentialUtil credentialUtil) {
        this.context = context;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.credentialUtil = credentialUtil;
    }

    @BeforeEach
    public void setUp() {
        POS_MER_API_KEY = credentialUtil.getPOS_MER_API_KEY();
    }

    @AfterEach
    public void tearDown() {
    }

}
