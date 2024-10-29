package net.artux.ailingo.server.service;

public interface ChatService {

    void setContext(String context);

    String getResponse(String message);

    void setAIRole(String userRole);

    void setUserRole(String aiRole);
}
