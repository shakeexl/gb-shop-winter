package ru.gb.api.configuration;

import feign.*;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import feign.optionals.OptionalDecoder;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.cloud.sleuth.instrument.web.client.feign.OkHttpFeignClientBeanPostProcessor;
import org.springframework.cloud.sleuth.instrument.web.client.feign.SleuthFeignBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gb.api.category.api.CategoryGateway;
import ru.gb.api.manufacturer.api.ManufacturerGateway;
import ru.gb.api.product.api.ProductGateway;

import static feign.FeignException.errorStatus;


@Configuration
@EnableFeignClients(clients = {CategoryGateway.class,
        ProductGateway.class})
@EnableConfigurationProperties(GbApiProperties.class)
@RequiredArgsConstructor
public class FeignConfiguration {

    private final GbApiProperties gbApiProperties;
    private final ObjectFactory<HttpMessageConverters> messageConverters;
    private final BeanFactory beanFactory;

    @Bean
    public ManufacturerGateway manufacturerGateway() {

        //only tracing without connection pool
        return SleuthFeignBuilder.builder(beanFactory)
                .encoder(new SpringEncoder(this.messageConverters))
                .decoder(new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters))))
                .options(new Request.Options(
                        gbApiProperties.getConnection().getConnectTimeoutMillis(),
                        gbApiProperties.getConnection().getReadTimeoutMillis()
                ))
                .logger(new Slf4jLogger(ManufacturerGateway.class))
                .logLevel(Logger.Level.FULL)
                .retryer(new Retryer.Default(
                        gbApiProperties.getConnection().getPeriod(),
                        gbApiProperties.getConnection().getMaxPeriod(),
                        gbApiProperties.getConnection().getMaxAttempts()
                ))
                .errorDecoder(errorDecoder())
                .contract(new SpringMvcContract())
                //.client(getClient())
                .target(ManufacturerGateway.class, gbApiProperties.getEndpoint().getManufacturerUrl());
    }

    private ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            FeignException feignException = errorStatus(methodKey, response);
            if (feignException.status() == 500 || feignException.status() == 503) {
                return new RetryableException(
                        response.status(),
                        feignException.getMessage(),
                        response.request().httpMethod(),
                        feignException,
                        null,
                        response.request());
            }
            return feignException;
        };
    }
//when you don't need tracing and you must set up connection pool
//    private Client getClient() {
//
//        HttpClientBuilder httpClientBuilder = HttpClients.custom()
//                .setMaxConnPerRoute(gbApiProperties.getConnection().getMaxConnections())
//                .setMaxConnTotal(gbApiProperties.getConnection().getMaxConnections());
//
//        return new ApacheHttpClient(httpClientBuilder.build());
//    }

    // i to i eto
//    @Bean
//    public OkHttpClient okHttpClient() {
//        return new Builder()
//                .connectTimeout(properties.getHttpTimeoutMillis(), TimeUnit.MILLISECONDS)
//                .readTimeout(properties.getHttpTimeoutMillis(), TimeUnit.MILLISECONDS)
//                .writeTimeout(properties.getHttpTimeoutMillis(), TimeUnit.MILLISECONDS)
//                .connectionPool(new ConnectionPool(properties.getHttpPoolSize(),
//                        properties.getHttpKeepAliveMillis(), TimeUnit.MILLISECONDS))
//                .build();
//    }
//
//    @Bean
//    static OkHttpFeignClientBeanPostProcessor okHttpFeignClientBeanPostProcessor(
//            BeanFactory beanFactory) {
//        return new OkHttpFeignClientBeanPostProcessor(beanFactory);
//    }
}
