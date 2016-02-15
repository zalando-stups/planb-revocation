package org.zalando.planb.revocation.api.exception;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
public class YamlParsingException extends RuntimeException {

    private static final String MESSAGE = "Error parsing YAML file";

    public YamlParsingException() {
        super(MESSAGE);
    }
}
