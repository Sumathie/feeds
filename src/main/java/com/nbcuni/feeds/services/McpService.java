package com.nbcuni.feeds.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nbcuni.feeds.utils.SignatureUtils;

import static org.springframework.http.HttpMethod.POST;

@Service
public class McpService {

    long timestamp = (System.currentTimeMillis() / 1000L) + 6;
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> map = new HashMap<String, Object>();
    @Autowired
    private RestTemplate restTemplate;
    private String sampleXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "    <request>\n" +
            "        <type>list_videos</type>\n" +
            "        <params>\n" +
            "        </params>\n" +
            "    </request>";
    String requestToSign = sampleXml + timestamp;
    @Autowired
    private AWSSecretsManager awsSecretsManager;

    public Map<String, Object> getSecret() {
        String secretString = getSecret(MCPKeys.SECRET_NAME);
        try {
            map = mapper.readValue(secretString, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String getSecret(String secretName) {

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);

        GetSecretValueResult getSecretValueResponse = null;
        try {
            getSecretValueResponse = awsSecretsManager.getSecretValue(getSecretValueRequest);

        } catch (ResourceNotFoundException e) {
            System.out.println("The requested secret " + secretName + " was not found");
        } catch (InvalidRequestException e) {
            System.out.println("The request was invalid due to: " + e.getMessage());
        } catch (InvalidParameterException e) {
            System.out.println("The request had invalid params: " + e.getMessage());
        }

        if (getSecretValueResponse == null || getSecretValueResponse.getSecretString() == null) {  // no such secret found
            return null;
        }
        return getSecretValueResponse.getSecretString();
    }


    public String getData() throws Exception {
        Map<String, Object> data = getSecret();
        String signature = SignatureUtils.getSignature(requestToSign, (String) data.get(MCPKeys.NBC_PRIVATE_KEY_NAME));
        String getParametersSuffixPageNo = "id=" + data.get(MCPKeys.NBC_PUBLIC_KEY_NAME) + "&ts=" + timestamp + "&sgn=" + signature + "&page_sz=50&page_no=1";

        return apiCall(sampleXml, getParametersSuffixPageNo);
    }

    String apiCall(String payload, String getParameters) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<String>(payload, headers);
        ResponseEntity<String> entry = restTemplate.exchange("https://nbc.mcp.anvato.com/api?" + getParameters, POST, request, String.class);
        return entry.getBody();
    }
}
