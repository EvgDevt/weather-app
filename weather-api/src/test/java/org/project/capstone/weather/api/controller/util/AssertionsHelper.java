package org.project.capstone.weather.api.controller.util;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssertionsHelper {

    public static void assertBadRequestForInvalidPathVariable(MockMvc mockMvc, String endpoint, String invalidId) throws Exception {
        mockMvc.perform(get(endpoint, invalidId))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.type").value("about:blank"),
                        jsonPath("$.title").value("Bad Request"),
                        jsonPath("$.status").value(400),
                        jsonPath("$.detail").value(String.format("Failed to convert 'userId' with value: '%s'", invalidId)),
                        jsonPath("$.instance").value(endpoint.replace("{userId}", invalidId))
                );
    }
}
