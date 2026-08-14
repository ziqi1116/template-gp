package com.gp.framework.aspect;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp.common.annotation.Log;
import com.gp.common.utils.SecurityUtils;
import com.gp.framework.domain.SysOperLog;
import com.gp.framework.mapper.SysOperLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysOperLogMapper operLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, 4, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            r -> {
                Thread t = new Thread(r);
                t.setName("oper-log-pool");
                t.setDaemon(true);
                return t;
            }
    );

    @Around("@annotation(com.gp.common.annotation.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        SysOperLog operLog = new SysOperLog();
        operLog.setTitle(logAnnotation.title());
        operLog.setOperType(logAnnotation.operType());
        operLog.setOperTime(new Date());

        Operation operation = method.getAnnotation(Operation.class);
        if (operation != null) {
            operLog.setTitle(operation.summary());
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            operLog.setOperUrl(request.getRequestURI());
            operLog.setOperMethod(request.getMethod());
            operLog.setOperIp(getRemoteIp(request));
        }

        String username = SecurityUtils.getUsername();
        operLog.setOperName(username != null ? username : "anonymous");

        try {
            Object result = point.proceed();
            operLog.setStatus("0");
            operLog.setJsonResult(toJson(result));
            return result;
        } catch (Throwable e) {
            operLog.setStatus("1");
            operLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            operLog.setCostTime(costTime);
            saveAsync(operLog);
        }
    }

    private String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    private void saveAsync(SysOperLog operLog) {
        CompletableFuture.runAsync(() -> operLogMapper.insert(operLog), executor);
    }

}