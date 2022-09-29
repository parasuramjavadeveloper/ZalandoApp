package com.example.reactivewebflux.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
/**
 * Service to get the Articles after Successful OAUTH2 Authentication.
 */
public class ArticleService {

    private static final String CLIENT_ID_CLIENT_SECRET = "a345f62a95a597eb235b6d5b3ac6250f:4873991d-cf90-49c8-9cbe-4db44e662fc4";

    private static final String AUTHORIZATION = "Authorization";

    private static final String BASIC = "Basic ";

    private static final String GRANT_TYPE = "grant_type";

    private static final String CLIENT_CREDENTIALS = "client_credentials";

    private static final String ACCESS_TOKEN = "access_token";

    @Value("${zalando.sandBox.platform.auth.oauth.deployed.authBaseURL}")
    private String baseURL;

    @Value("${zalando.sandBox.platform.oauth.path}")
    private String oAuthPath;

    @Value("${zalando.sandBox.platform.resource.path}")
    private String resourcePath;

    private final WebClient webClient;

    @Autowired
    public ArticleService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Invokes the provided resource endpoint after executed oauth2 endpoint and gets
     * the token as a response and pass it as an input to the resource endpoint
     *
     * @return A mono string with the response from the endpoint.
     */
    public Mono<String> getArticles() {

        String encodedClientData =
                Base64Utils.encodeToString(CLIENT_ID_CLIENT_SECRET.getBytes());
        Mono<String> resource = webClient.post()
                .uri(baseURL.concat(oAuthPath))
                .header(AUTHORIZATION, BASIC + encodedClientData)
                .body(BodyInserters.fromFormData(GRANT_TYPE, CLIENT_CREDENTIALS))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMap(tokenResponse -> {
                    String accessTokenValue = tokenResponse.get(ACCESS_TOKEN)
                            .textValue();
                    return webClient.get()
                            .uri(baseURL.concat(resourcePath))
                            .headers(h -> h.setBearerAuth(accessTokenValue))
                            .retrieve()
                            .bodyToMono(String.class);
                });
        return resource.map(res ->
                "Retrieved the resource using a webclient: " + res);
    }

}
