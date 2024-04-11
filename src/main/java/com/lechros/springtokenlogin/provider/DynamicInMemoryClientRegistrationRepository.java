package com.lechros.springtokenlogin.provider;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicInMemoryClientRegistrationRepository implements ClientRegistrationRepository, Iterable<ClientRegistration> {

    private final InMemoryClientRegistrationRepository delegate;
    private final Map<String, ClientSecretGenerator> secretGenerators;

    public DynamicInMemoryClientRegistrationRepository(Map<String, ClientRegistration> registrations, List<ClientSecretGenerator> secretGenerators) {
        this.delegate = new InMemoryClientRegistrationRepository(registrations);
        this.secretGenerators = createSecretGeneratorsMap(secretGenerators);
    }

    private Map<String, ClientSecretGenerator> createSecretGeneratorsMap(List<ClientSecretGenerator> secretGenerators) {
        Assert.notEmpty(secretGenerators, "secretGenerators cannot be empty");
        ConcurrentHashMap<String, ClientSecretGenerator> result = new ConcurrentHashMap<>();
        for (ClientSecretGenerator generator : secretGenerators) {
            Assert.state(!result.containsKey(generator.getRegistrationId()), () -> String.format("Duplicate key %s", generator.getRegistrationId()));
            result.put(generator.getRegistrationId(), generator);
        }
        return Collections.unmodifiableMap(result);
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        ClientRegistration clientRegistration = delegate.findByRegistrationId(registrationId);
        ClientSecretGenerator clientSecretGenerator = secretGenerators.get(registrationId);

        if (clientSecretGenerator == null) {
            return clientRegistration;
        } else {
            return ClientRegistration.withClientRegistration(clientRegistration)
                .clientSecret(clientSecretGenerator.generate())
                .build();
        }
    }

    @Override
    public Iterator<ClientRegistration> iterator() {
        return delegate.iterator();
    }
}
