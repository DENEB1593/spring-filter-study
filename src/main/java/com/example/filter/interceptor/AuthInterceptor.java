package com.example.filter.interceptor;

import com.example.filter.annotation.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Objects;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    // filter단에서 cachewrapper 타입으로 전달하면 request 손실이 안됨
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String url = request.getRequestURI();
        URI uri =
                UriComponentsBuilder.fromUriString(request.getRequestURI())
                        .build()
                        .toUri();

        log.info("interceptor uri : {}", url);


        boolean hashAnnotation = checkAnnotation(handler, Auth.class);
        log.info("has annotaiotn : {}", hashAnnotation);
        
        // 권한을 가졌는가
        if (hashAnnotation) {
            // 권한 체크
            String query = uri.getQuery();
            log.info("query : {}", query);
            return "name=steve".equals(query);
        }

        return true; // false면 Controller까지 안감
    }

    private boolean checkAnnotation(Object handler, Class clazz) {
        // resource, js, html
        if (handler instanceof ResourceHttpRequestHandler)
            return true;

        // annotation check
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (Objects.nonNull(handlerMethod.getMethodAnnotation(clazz)) ||
            Objects.nonNull(handlerMethod.getBeanType().getAnnotation(clazz))) {
            // Auth가 있으면
            return true;
        }

        return false;
    }
}
