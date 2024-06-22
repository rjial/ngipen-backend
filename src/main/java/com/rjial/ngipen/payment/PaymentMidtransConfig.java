package com.rjial.ngipen.payment;

import com.midtrans.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentMidtransConfig {

    public String clientKey;

    public String serverKey;

    public String midtransNotificationUrl;

    public PaymentMidtransConfig(Environment env) {
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

}
