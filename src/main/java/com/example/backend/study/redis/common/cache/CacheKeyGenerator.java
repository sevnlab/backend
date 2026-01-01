package com.example.backend.study.redis.common.cache;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * 캐시 키 생성 클래스(파라미터 값을 추출해 유니크한 캐시 키를 만들어줌)
 */
@Component
public class CacheKeyGenerator {

    // #userId 같은 표현식을 실제 값으로 바꿔주는 도구
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 각 전략에 대해서 캐시이름별로 키를 유니크하게 식별하게 위해서 키를 만들어주는 과정
     * @return {cacheStrategy}:{cacheName}:{key}
     */
    // joinPoint: AOP에서 메서드 실행 정보를 담은 객체
    // cacheStrategy: 캐시 전략 (LRU, FIFO 등)
    // cacheName: 캐시 이름
    // keySpel: 키 표현식 (예: #userId)
    public String genKey(JoinPoint joinPoint, CacheStrategy cacheStrategy, String cacheName, String keySpel) {

        // SpEL 표현식을 평가할 환경 생성
        EvaluationContext context = new StandardEvaluationContext();

        // 파라미터 이름 가져오기
        // 예시데이터
        // public User findUser(Long userId, String name) {...}
        // parameterNames = ["userId", "name"]
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        // 파라미터 값 가져오기
        // findUser(123L, "세븐") 호출했다면
        // args = [123L, "세븐"]
        Object[] args = joinPoint.getArgs();

        // Context에 변수 등록(파라미터 이름+값 매핑)
        for(int i =0; i < args.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // 키 생성
        return cacheStrategy + ":" + cacheName + ":"
                + parser.parseExpression(keySpel).getValue(context, String.class);
    }
}
