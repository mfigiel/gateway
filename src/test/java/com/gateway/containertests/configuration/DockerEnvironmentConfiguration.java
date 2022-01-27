package com.gateway.containertests.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DockerEnvironmentConfiguration {

    protected static final int GATEWAY_PORT = 8081;
    protected static final int WAREHOUSE_PORT = 8089;
    protected static final int CLIENTS_PORT = 8083;
    protected static final int ORDERS_PORT = 8082;

    protected static DockerComposeContainer localDockerEnvironment;
    protected static final String LOCAL_HOST = "localhost";

    protected static final Map<String, Dependency> dependencyInfo = new ConcurrentHashMap<>();

    protected static final int TIMEOUT = 180;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Dependency {
        private String host;
        private int port;
    }

    static {
        File dockerFile = new File("D:/programowanie/docker-compose-test.yml");

        try {
            if (isLocal() && !useExistingEnv() && dockerFileExists(dockerFile)) {

                DockerEnvironmentConfiguration.localDockerEnvironment = new DockerComposeContainer(dockerFile)
                        .withExposedService("gateway", GATEWAY_PORT,
                                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(TIMEOUT)))
                        .withExposedService("warehouse", WAREHOUSE_PORT,
                                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(TIMEOUT)))
                        .withExposedService("clients", CLIENTS_PORT,
                                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(TIMEOUT)))
                        .withExposedService("orders", ORDERS_PORT,
                                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(TIMEOUT)));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static boolean dockerFileExists(File dockerfile) throws FileNotFoundException {
         if (!dockerfile.exists()) {
             throw new FileNotFoundException("docker file not exists, path: " + dockerfile.getPath());
         }
         return true;
    }

    protected static void local() {
        dependencyInfo.put("integrationtest.gateway", localDependency(getMappedPort("gateway_1", GATEWAY_PORT)));
        dependencyInfo.put("integrationtest.warehouse", localDependency(getMappedPort("warehouse_1", WAREHOUSE_PORT)));
        dependencyInfo.put("integrationtest.clients", localDependency(getMappedPort("clients_1", CLIENTS_PORT)));
        dependencyInfo.put("integrationtest.orders", localDependency(getMappedPort("orders_1", ORDERS_PORT)));
    }

    private static Integer getMappedPort(String serviceName, Integer fromPort) {
        return useExistingEnv() ? fromPort : localDockerEnvironment.getServicePort(serviceName, fromPort);
    }

    private static Dependency localDependency(Integer port) {
        return new Dependency(LOCAL_HOST, port);
    }

    public static boolean isLocal() {
        return true;
    }

    public static boolean useExistingEnv() {
        return System.getProperty("useExistingEnv") != null
                || Objects.equals(System.getenv("useExistingEnv"), "true");
    }

    public static boolean runWithoutContainers() {
        return "true".equals(System.getProperty("noContainers", "false"))
                || Objects.equals(System.getenv("noContainers"), "true");
    }
}
