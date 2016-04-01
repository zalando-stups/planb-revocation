package org.zalando.planb.revocation.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class ExceptionsResourceTest {

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = standaloneSetup(new FakeController()).setControllerAdvice(new ExceptionsResource())
                .alwaysDo(print()).build();
    }

    @Test
    public void invoke() throws Exception {
        mockMvc.perform(get("/exception")).andExpect(status().is5xxServerError());
    }

    @Test
    public void invokeNoMessage() throws Exception {
        mockMvc.perform(get("/exceptionNoMessage")).andExpect(status().is5xxServerError());
    }

    @RestController
    static class FakeController {

        @RequestMapping("/exception")
        public void exception() {
            throw new FakeException("Not handled by others");
        }

        @RequestMapping("/exceptionNoMessage")
        public void exceptionNoMessage() {
            throw new FakeException();
        }
    }

    static class FakeException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public FakeException(String message) {
            super(message);
        }

        public FakeException() {
            super();
        }
    }

}
