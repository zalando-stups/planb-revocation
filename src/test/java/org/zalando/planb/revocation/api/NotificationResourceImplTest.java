package org.zalando.planb.revocation.api;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.zalando.planb.revocation.api.impl.NotificationResourceImpl;
import org.zalando.planb.revocation.domain.NotificationType;
import org.zalando.planb.revocation.persistence.RevocationStore;

public class NotificationResourceImplTest {

    private MockMvc mockMvc;
    private RevocationStore revocationStore;

    @Before
    public void setUp() {
        revocationStore = Mockito.mock(RevocationStore.class);
        mockMvc = standaloneSetup(new NotificationResourceImpl(revocationStore))
                .setControllerAdvice(new ExceptionsResource())
                .alwaysDo(print()).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void typeNotSettableThrowsException() {
        NotificationResourceImpl resource = new NotificationResourceImpl(revocationStore);
        resource.post(NotificationType.REFRESH_TIMESTAMP, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueIsNullThrowsException() {
        NotificationResourceImpl resource = new NotificationResourceImpl(revocationStore);
        resource.post(NotificationType.REFRESH_FROM, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueNotParseableIntegerThrowsException() {
        NotificationResourceImpl resource = new NotificationResourceImpl(revocationStore);
        resource.post(NotificationType.REFRESH_FROM, "NotAnInteger");
    }

    @Test
    public void storageUnableToStoreFromThrowsException() {
        NotificationResourceImpl resource = new NotificationResourceImpl(revocationStore);
        Mockito.when(revocationStore.storeRefresh(Mockito.eq(12))).thenReturn(Boolean.FALSE);
        ResponseEntity<String> response = (ResponseEntity) resource.post(NotificationType.REFRESH_FROM, "12");
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
