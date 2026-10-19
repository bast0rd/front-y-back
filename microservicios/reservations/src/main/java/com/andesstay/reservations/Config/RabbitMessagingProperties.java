package com.andesstay.reservations.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Exchange y routing keys con los que reservations PUBLICA mensajes (prefijo app.rabbit). */
@ConfigurationProperties(prefix = "app.rabbit")
public class RabbitMessagingProperties {

    private final Exchanges exchanges = new Exchanges();
    private final RoutingKeys routingKeys = new RoutingKeys();

    public Exchanges getExchanges() { return exchanges; }
    public RoutingKeys getRoutingKeys() { return routingKeys; }

    public static class Exchanges {
        private String direct;
        public String getDirect() { return direct; }
        public void setDirect(String direct) { this.direct = direct; }
    }

    public static class RoutingKeys {
        private String emailDirect;
        private String housekeepingDirect;
        private String voucherDirect;
        public String getEmailDirect() { return emailDirect; }
        public void setEmailDirect(String emailDirect) { this.emailDirect = emailDirect; }
        public String getHousekeepingDirect() { return housekeepingDirect; }
        public void setHousekeepingDirect(String housekeepingDirect) { this.housekeepingDirect = housekeepingDirect; }
        public String getVoucherDirect() { return voucherDirect; }
        public void setVoucherDirect(String voucherDirect) { this.voucherDirect = voucherDirect; }
    }
}
