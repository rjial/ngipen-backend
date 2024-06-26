package com.rjial.ngipen.payment;

import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.httpclient.CoreApi;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentMidtransComponent {

    public String clientKey;

    public String serverKey;

    public String midtransNotificationUrl;

    private final MidtransCoreApi coreApi = new ConfigFactory(Config.builder()
            .setServerKey("SB-Mid-server-Fq6NnVTKcsXINGi9bqX1y-Na")
            .setClientKey(clientKey)
            .setIsProduction(false)
            .enableLog(true)
            .build()).getCoreApi();

    public PaymentMidtransComponent(Environment env) {
        clientKey = env.getProperty("midtrans.clientkey");
        log.info(clientKey);
        assert clientKey != null;
        serverKey = env.getProperty("midtrans.serverkey");
        log.info(serverKey);
        assert serverKey != null;
        this.midtransNotificationUrl = env.getProperty("midtrans.notification-url");
        assert midtransNotificationUrl != null;
    }

    public Config snapConfig() {
        assert midtransNotificationUrl != null;
        assert serverKey != null;
        assert clientKey != null;
        return Config.builder()
                .setServerKey(serverKey)
                .setClientKey(clientKey)
                .setIsProduction(false)
                .enableLog(true)
                .setPaymentOverrideNotification(midtransNotificationUrl)
                .build();
    }

    public JSONObject checkTransaction(String transactionId) throws MidtransError {
        return coreApi.checkTransaction(transactionId);
    }

}
