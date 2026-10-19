package com.andesstay.notify.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nombres de exchanges, colas y routing keys leídos desde application.properties (prefijo app.rabbit).
 * Es la ÚNICA fuente de nombres: ninguna otra clase debe escribir "q.cmd.email" o "cmd.direct" a mano.
 */
@ConfigurationProperties(prefix = "app.rabbit")
public class MessagingProperties {

    private boolean quorumQueues = false;
    private final Exchanges exchanges = new Exchanges();
    private final Queues queues = new Queues();
    private final RoutingKeys routingKeys = new RoutingKeys();
    private final Retry retry = new Retry();

    public boolean isQuorumQueues() { return quorumQueues; }
    public void setQuorumQueues(boolean quorumQueues) { this.quorumQueues = quorumQueues; }
    public Exchanges getExchanges() { return exchanges; }
    public Queues getQueues() { return queues; }
    public RoutingKeys getRoutingKeys() { return routingKeys; }
    public Retry getRetry() { return retry; }

    public static class Exchanges {
        private String direct;
        private String topic;
        private String dlx;
        public String getDirect() { return direct; }
        public void setDirect(String direct) { this.direct = direct; }
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public String getDlx() { return dlx; }
        public void setDlx(String dlx) { this.dlx = dlx; }
    }

    public static class Queues {
        private String email;
        private String emailDlq;
        private String housekeeping;
        private String housekeepingDlq;
        private String voucher;
        private String voucherDlq;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getEmailDlq() { return emailDlq; }
        public void setEmailDlq(String emailDlq) { this.emailDlq = emailDlq; }
        public String getHousekeeping() { return housekeeping; }
        public void setHousekeeping(String housekeeping) { this.housekeeping = housekeeping; }
        public String getHousekeepingDlq() { return housekeepingDlq; }
        public void setHousekeepingDlq(String housekeepingDlq) { this.housekeepingDlq = housekeepingDlq; }
        public String getVoucher() { return voucher; }
        public void setVoucher(String voucher) { this.voucher = voucher; }
        public String getVoucherDlq() { return voucherDlq; }
        public void setVoucherDlq(String voucherDlq) { this.voucherDlq = voucherDlq; }
    }

    public static class RoutingKeys {
        private String emailDirect;
        private String emailTopic;
        private String housekeepingDirect;
        private String housekeepingTopic;
        private String voucherDirect;
        private String voucherTopic;
        public String getEmailDirect() { return emailDirect; }
        public void setEmailDirect(String emailDirect) { this.emailDirect = emailDirect; }
        public String getEmailTopic() { return emailTopic; }
        public void setEmailTopic(String emailTopic) { this.emailTopic = emailTopic; }
        public String getHousekeepingDirect() { return housekeepingDirect; }
        public void setHousekeepingDirect(String housekeepingDirect) { this.housekeepingDirect = housekeepingDirect; }
        public String getHousekeepingTopic() { return housekeepingTopic; }
        public void setHousekeepingTopic(String housekeepingTopic) { this.housekeepingTopic = housekeepingTopic; }
        public String getVoucherDirect() { return voucherDirect; }
        public void setVoucherDirect(String voucherDirect) { this.voucherDirect = voucherDirect; }
        public String getVoucherTopic() { return voucherTopic; }
        public void setVoucherTopic(String voucherTopic) { this.voucherTopic = voucherTopic; }
    }

    public static class Retry {
        private int maxAttempts = 3;
        private long initialIntervalMs = 1000;
        private double multiplier = 2.0;
        public int getMaxAttempts() { return maxAttempts; }
        public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
        public long getInitialIntervalMs() { return initialIntervalMs; }
        public void setInitialIntervalMs(long initialIntervalMs) { this.initialIntervalMs = initialIntervalMs; }
        public double getMultiplier() { return multiplier; }
        public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
    }
}
