package com.financred.financred.websocket.config;

import com.financred.financred.exception.AcessoNegadoException;
import com.financred.financred.infra.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;


@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() != null && accessor.getSessionAttributes() != null) {
            String token = (String) accessor.getSessionAttributes().get("token");

            if (token != null) {
                String email = tokenService.validateToken(token);

                if (email != null) {
                    accessor.setUser(() -> email);
                } else {
                    throw new AcessoNegadoException("Token JWT inválido.");
                }
            } else {
                throw new AcessoNegadoException("Token JWT ausente.");
            }
        }

        return message;
    }
}
