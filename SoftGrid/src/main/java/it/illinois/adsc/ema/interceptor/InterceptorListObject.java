package it.illinois.adsc.ema.interceptor;

/**
 * Created by prageethmahendra on 5/5/2017.
 */
public interface InterceptorListObject {
    InterceptorListObject nextInterceptor();
    InterceptorListObject previousInterceptor();
}
