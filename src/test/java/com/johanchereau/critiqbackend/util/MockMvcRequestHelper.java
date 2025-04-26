package com.johanchereau.critiqbackend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Helper class for simplifying MockMvc HTTP requests in integration tests.
 * <p>
 * Provides utility methods for common HTTP operations: GET, POST, PUT, PATCH, DELETE
 * with JSON content type and standard settings.
 */
public class MockMvcRequestHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Perform a basic GET request.
     *
     * @param mockMvc the MockMvc instance
     * @param url     the endpoint URL
     * @return the ResultActions for assertions
     * @throws Exception if an error occurs during request
     */
    public static ResultActions performGet(MockMvc mockMvc, String url) throws Exception {
        return mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));
    }

    /**
     * Perform a paginated GET request with default pagination (page=0, size=10).
     *
     * @param mockMvc the MockMvc instance
     * @param url     the endpoint URL
     * @return the ResultActions for assertions
     * @throws Exception if an error occurs during request
     */
    public static ResultActions performGetPaginated(MockMvc mockMvc, String url) throws Exception {
        return mockMvc.perform(get(url)
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON));
    }

    /**
     * Perform a POST request with a JSON body.
     *
     * @param mockMvc the MockMvc instance
     * @param url     the endpoint URL
     * @param body    the request body to serialize to JSON
     * @return the ResultActions for assertions
     * @throws Exception if an error occurs during request
     */
    public static ResultActions performPost(MockMvc mockMvc, String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    /**
     * Perform a POST request without body.
     *
     * @param mockMvc the MockMvc instance
     * @param url     the endpoint URL
     * @return the ResultActions for assertions
     * @throws Exception if an error occurs during request
     */
    public static ResultActions performPostWithoutBody(MockMvc mockMvc, String url) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Perform a PUT request with a JSON body.
     *
     * @param mockMvc the MockMvc instance
     * @param url     the endpoint URL
     * @param body    the request body to serialize to JSON
     * @return the ResultActions for assertions
     * @throws Exception if an error occurs during request
     */
    public static ResultActions performPut(MockMvc mockMvc, String url, Object body) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    /**
     * Perform a PATCH request with a JSON body.
     *
     * @param mockMvc the MockMvc instance
     * @param url     the endpoint URL
     * @param body    the request body to serialize to JSON
     * @return the ResultActions for assertions
     * @throws Exception if an error occurs during request
     */
    public static ResultActions performPatch(MockMvc mockMvc, String url, Object body) throws Exception {
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    /**
     * Perform a DELETE request.
     *
     * @param mockMvc the MockMvc instance
     * @param url     the endpoint URL
     * @return the ResultActions for assertions
     * @throws Exception if an error occurs during request
     */
    public static ResultActions performDelete(MockMvc mockMvc, String url) throws Exception {
        return mockMvc.perform(delete(url)
                .accept(MediaType.APPLICATION_JSON));
    }
}