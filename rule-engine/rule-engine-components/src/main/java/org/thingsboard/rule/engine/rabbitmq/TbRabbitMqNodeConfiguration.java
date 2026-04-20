/**
 * Copyright © 2016-2026 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.rule.engine.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import lombok.Data;
import org.thingsboard.rule.engine.api.NodeConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class TbRabbitMqNodeConfiguration implements NodeConfiguration<TbRabbitMqNodeConfiguration> {

    private String exchangeNamePattern;
    private String routingKeyPattern;
    private String messageProperties;
    private List<String> addresses;
    private String host;
    private int port;
    private String virtualHost;
    private String username;
    private String password;
    private boolean automaticRecoveryEnabled;
    private Boolean topologyRecoveryEnabled;
    private Integer requestedHeartbeat;
    private Integer networkRecoveryInterval;
    private int connectionTimeout;
    private int handshakeTimeout;
    private Map<String, String> clientProperties;

    @Override
    public TbRabbitMqNodeConfiguration defaultConfiguration() {
        TbRabbitMqNodeConfiguration configuration = new TbRabbitMqNodeConfiguration();
        configuration.setExchangeNamePattern("");
        configuration.setRoutingKeyPattern("");
        configuration.setMessageProperties(null);
        configuration.setAddresses(Collections.emptyList());
        configuration.setPort(ConnectionFactory.DEFAULT_AMQP_PORT);
        configuration.setVirtualHost(ConnectionFactory.DEFAULT_VHOST);
        configuration.setUsername(ConnectionFactory.DEFAULT_USER);
        configuration.setPassword(ConnectionFactory.DEFAULT_PASS);
        configuration.setAutomaticRecoveryEnabled(false);
        configuration.setTopologyRecoveryEnabled(true);
        configuration.setRequestedHeartbeat(30);
        configuration.setNetworkRecoveryInterval(5000);
        configuration.setConnectionTimeout(ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT);
        configuration.setHandshakeTimeout(ConnectionFactory.DEFAULT_HANDSHAKE_TIMEOUT);
        configuration.setClientProperties(Collections.emptyMap());
        return configuration;
    }
}
