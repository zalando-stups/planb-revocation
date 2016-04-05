package org.zalando.planb.revocation.api.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.planb.revocation.api.NotificationResource;
import org.zalando.planb.revocation.domain.NotificationType;
import org.zalando.planb.revocation.persistence.RevocationStore;

@RestController
@RequestMapping(path = "/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationResourceImpl implements NotificationResource {


    private final RevocationStore storage;

    @Autowired
    public NotificationResourceImpl(RevocationStore storage) {
        this.storage = storage;
    }

    @Override
    @RequestMapping(value="/{type}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void post(@PathVariable("type") NotificationType type, @RequestParam Object value) {

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
                Integer from = null;
                try {
                    from = Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Type mismatch. 'value' must be a valid UTC UNIX timestamp.");
                }

                storage.storeRefresh(from);
                break;
        }
    }
}
