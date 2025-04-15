import org.gradle.kotlin.dsl.*
plugins {
    id("java")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}
group = "org.thluon.tdrive"
version = "1.0-SNAPSHOT"
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}
dependencies {
    implementation(platform(libs.spring.cloud.dependencies))
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation(libs.thluon.rest)
    implementation(libs.bundles.jjwt)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    constraints {
        implementation("com.thoughtworks.xstream:xstream:1.4.21")
        implementation("commons-io:commons-io:2.14.0")
        implementation("org.apache.httpcomponents:httpclient:4.5.13")
    }

}
tasks.bootJar {
    archiveFileName.set("api-gateway.jar")
}
tasks.bootRun{
  doFirst {
        file(".env").readLines().forEach {
            val parts = it.trim().split("=", limit = 2)
            if (parts.size == 2 && parts[0].isNotEmpty()) {
                val (key, value) = parts
                environment(key, value)
            }
        }
    }
}
