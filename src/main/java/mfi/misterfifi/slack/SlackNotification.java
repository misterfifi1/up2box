package mfi.misterfifi.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;

import java.io.IOException;

public class SlackNotification {
    private String slackWebHook;
    private String chanel;
    private String userName;

    public SlackNotification(String slackWebHook, String chanel, String userName) {
        this.slackWebHook = slackWebHook;
        this.chanel = chanel;
        this.userName = userName;
    }

    public boolean sendMessage(String message) throws IOException {
        Payload payload = Payload.builder()
                .channel(this.chanel)
                .username(this.userName)
                .iconEmoji(":floppy_disk:")
                .text(message)
                .build();

        Slack slack = Slack.getInstance();
        WebhookResponse response = slack.send(this.slackWebHook, payload);
        return response.getCode().equals(200);
    }

    public String getSlackWebHook() {
        return slackWebHook;
    }

    public String getChanel() {
        return chanel;
    }

    public String getUserName() {
        return userName;
    }
}
