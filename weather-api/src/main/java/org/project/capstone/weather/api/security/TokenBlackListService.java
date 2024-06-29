package org.project.capstone.weather.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TokenBlackListService {

    private final Set<String> blackList = ConcurrentHashMap.newKeySet();


    public void addToken(String token) {
        blackList.add(token);
    }

    public Boolean isTokenBlackListed(String token) {
        return blackList.contains(token);
    }
}
