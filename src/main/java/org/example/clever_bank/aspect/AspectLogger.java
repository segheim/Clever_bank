package org.example.clever_bank.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;

@Aspect
public class AspectLogger {

    public static final Logger logger = LogManager.getLogger(AspectLogger.class);

    @Pointcut("@annotation(org.example.clever_bank.entity.Loggable)")
    public void loggableMethod() {
    }

    @Around("loggableMethod()")
    public Object handleRetries(final ProceedingJoinPoint point) {
        long start = System.currentTimeMillis();
        System.out.println("LOGGGGGGGERRERERRERERE");
        logger.info(String.format("START %s #%s(%s)",
                start,
                ((MethodSignature) point.getSignature()).getMethod().getName(),
                Arrays.toString(point.getArgs())));

        Object result;
        try {
            result = point.proceed();
        } catch (Throwable e) {
            logger.error(String.format("ERROR #%s(%s) Message %s",
                    System.currentTimeMillis(),
                    ((MethodSignature) point.getSignature()).getMethod().getName(),
                    Arrays.toString(point.getArgs())),
                    e.getMessage());
            throw new RuntimeException(e);
        }

        logger.info(String.format("FINISH %s #%s(%s): %s",
                System.currentTimeMillis() - start,
                ((MethodSignature) point.getSignature()).getMethod().getName(),
                Arrays.toString(point.getArgs()),
                result));

        return result;
    }
}
