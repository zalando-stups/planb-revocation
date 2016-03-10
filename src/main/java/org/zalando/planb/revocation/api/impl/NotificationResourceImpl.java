package org.zalando.planb.revocation.api.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.planb.revocation.api.NotificationResource;
import org.zalando.planb.revocation.domain.NotificationType;
import org.zalando.planb.revocation.persistence.RevocationStore;

@RestController
@RequestMapping(path = "/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationResourceImpl implements NotificationResource {

    @Autowired
    private RevocationStore storage;

    @Override
    @RequestMapping(value="/{type}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public HttpEntity<String> post(@PathVariable("type") NotificationType type, @RequestParam Object value) {

        // Verifies if the type can be set
        if (!type.isSettable()) throw new IllegalArgumentException("Invalid resource: " + type);

        /*
         * When path is /{type}?value= (null value) throws an IllegalArgumentException
         */
        if (value  == null) {
            throw new IllegalArgumentException("Parameter 'value' can't be null");
        }

        switch (type) {
            case REFRESH_FROM:
                Long from = null;
                try {
                    from = Long.parseLong(value.toString());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid format. " + type + " must be a valid UTC UNIX " +
                            "timestamp.");
                }

                if (!storage.storeRefresh(from)) {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
        }

        // TODO Refactor
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
