package org.owasp.wrongsecrets.challenges.docker;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;
import org.junit.jupiter.api.Test;

class Challenge59Test {

  @Test
  void answerCorrectWithValidWebhookUrl() {
    // Create a properly obfuscated Slack webhook URL
    String originalUrl =
        System.getenv().getOrDefault("CHALLENGE59_TEST_WEBHOOK", "https://hooks.slack.com/services/TEST/TEST/TEST");
    String firstEncode = Base64.getEncoder().encodeToString(originalUrl.getBytes());
    String doubleEncoded = Base64.getEncoder().encodeToString(firstEncode.getBytes());

    Challenge59 challenge = new Challenge59(doubleEncoded);
    assertTrue(challenge.answerCorrect(originalUrl));
  }

  @Test
  void answerIncorrectWithWrongUrl() {
    String originalUrl =
        System.getenv().getOrDefault("CHALLENGE59_TEST_WEBHOOK", "https://hooks.slack.com/services/TEST/TEST/TEST");
    String firstEncode = Base64.getEncoder().encodeToString(originalUrl.getBytes());
    String doubleEncoded = Base64.getEncoder().encodeToString(firstEncode.getBytes());

    Challenge59 challenge = new Challenge59(doubleEncoded);
    assertFalse(challenge.answerCorrect("https://wrong-webhook-url.com"));
  }

  @Test
  void answerIncorrectWithEmptyString() {
    String originalUrl =
        System.getenv().getOrDefault("CHALLENGE59_TEST_WEBHOOK", "https://hooks.slack.com/services/TEST/TEST/TEST");
    String firstEncode = Base64.getEncoder().encodeToString(originalUrl.getBytes());
    String doubleEncoded = Base64.getEncoder().encodeToString(firstEncode.getBytes());

    Challenge59 challenge = new Challenge59(doubleEncoded);
    assertFalse(challenge.answerCorrect(""));
  }

  @Test
  void getSlackWebhookUrlReturnsDeobfuscatedUrl() {
    String originalUrl =
        System.getenv().getOrDefault("CHALLENGE59_TEST_WEBHOOK", "https://hooks.slack.com/services/TEST/TEST/TEST");
    String firstEncode = Base64.getEncoder().encodeToString(originalUrl.getBytes());
    String doubleEncoded = Base64.getEncoder().encodeToString(firstEncode.getBytes());

    Challenge59 challenge = new Challenge59(doubleEncoded);
    assertEquals(originalUrl, challenge.getSlackWebhookUrl());
  }

  @Test
  void handlesInvalidObfuscatedUrl() {
    // Test with invalid base64 input
    Challenge59 challenge = new Challenge59("invalid-base64-url");

    // Should return the default URL when deobfuscation fails
    String defaultUrl =
        System.getenv().getOrDefault("CHALLENGE59_DEFAULT_WEBHOOK", "https://hooks.slack.com/services/TEST/TEST/TEST");
    assertEquals(defaultUrl, challenge.getAnswer());
  }

  @Test
  void answerCorrectWithDefaultUrl() {
    // Test with invalid input that falls back to default
    Challenge59 challenge = new Challenge59("invalid-input");
    String defaultUrl =
        System.getenv().getOrDefault("CHALLENGE59_DEFAULT_WEBHOOK", "https://hooks.slack.com/services/TEST/TEST/TEST");
    assertTrue(challenge.answerCorrect(defaultUrl));
  }
}
