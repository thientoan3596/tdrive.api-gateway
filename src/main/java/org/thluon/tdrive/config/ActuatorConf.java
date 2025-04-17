package org.thluon.tdrive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ActuatorConf {
  @Value("${spring.application.name}")
  private String service_endpoint;

  @Bean
  @Primary
  WebEndpointProperties webEndpointProperties() {
    WebEndpointProperties properties = new WebEndpointProperties();
    properties.setBasePath("/" + service_endpoint + "/actuator");
    return properties;
  }
}
