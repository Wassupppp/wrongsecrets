package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(15)
public class Challenge15 extends Challenge {

    public Challenge15(ScoreCard scoreCard) {
        super(scoreCard);
    }

    @Override
    public Spoiler spoiler() {
        return null;
    }

    @Override
    protected boolean answerCorrect(String answer) {
        return false;
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    /**
     * Arcane:
     * [Arcane]
     * aws_access_key_id = REDACTED
     * aws_secret_access_key = REDACTED
     * output = json
     * region = us-east-2
     *
     * Arcane debug:
     * [default]
     * aws_access_key_id = REDACTED
     * aws_secret_access_key = REDACTED
     * output = json
     * region = us-east-2
     *
     * wrongsecrets debug:
     * [default]
     * aws_access_key_id = REDACTED
     * aws_secret_access_key = REDACTED
     * output = json
     * region = us-east-2
     */
}
