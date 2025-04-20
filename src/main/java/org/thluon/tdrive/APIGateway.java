package org.thluon.tdrive;

import static org.springdoc.core.utils.Constants.DEFAULT_API_DOCS_URL;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties.SwaggerUrl;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
@EnableDiscoveryClient
public class APIGateway {
  public static void main(String[] args) {
    org.springframework.boot.SpringApplication.run(APIGateway.class, args);
  }

  @Bean
  @Lazy(false)
  Set<SwaggerUrl> apis(
      RouteDefinitionLocator locator, SwaggerUiConfigProperties swaggerUiConfigProperties) {
    Set<SwaggerUrl> urls = new HashSet<>();
    List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
    Pattern p = Pattern.compile("v(\\d+)");
    definitions.stream()
        .filter(routeDefinition -> routeDefinition.getId().matches(".*-ms"))
        .forEach(
            routeDefinition -> {
              String name = routeDefinition.getId().replaceAll("-ms", "");
              String version = null;
              for (var predicate : routeDefinition.getPredicates()) {
                Matcher matcher = p.matcher(predicate.getArgs().toString());
                if (matcher.find()) {
                  version = matcher.group(1);
                  break;
                }
              }
              if (name.equals("security")) name = "auth";
              String apiVersion = version != null ? "/v" + version : "";
              String url = DEFAULT_API_DOCS_URL + "/" + name + apiVersion;
              SwaggerUrl swaggerUrl =
                  new SwaggerUrl(name, url, name.substring(0, 1).toUpperCase() + name.substring(1));
              urls.add(swaggerUrl);
            });
    swaggerUiConfigProperties.setUrls(urls);
    return urls;
  }
}
