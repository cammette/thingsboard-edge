/**
 * Copyright © 2016-2024 The Thingsboard Authors
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
package org.thingsboard.edge.rpc;

import org.thingsboard.server.common.data.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CloudRpcTargetProvider {

    private final List<HostPort> targets;
    private final AtomicInteger index = new AtomicInteger(0);
    private final AtomicBoolean isFirst = new AtomicBoolean(true);

    private CloudRpcTargetProvider(List<HostPort> targets) {
        this.targets = Collections.unmodifiableList(new ArrayList<>(targets));
    }

    public static CloudRpcTargetProvider fromConfig(String targetsConfig, String defaultHost, int defaultPort) {
        List<HostPort> targets = parseTargets(targetsConfig, defaultPort);
        if (targets.isEmpty()) {
            targets = List.of(new HostPort(defaultHost, defaultPort));
        }
        return new CloudRpcTargetProvider(targets);
    }

    public HostPort current() {
        if (targets.size() == 1) {
            return targets.get(index.get());
        }
        if (isFirst.get() && isFirst.compareAndSet(true, false)) {
            int randomIndex = ThreadLocalRandom.current().nextInt(targets.size());
            index.set(randomIndex);
            return targets.get(randomIndex);
        }
        return targets.get(index.get());
    }

    public HostPort rotate() {
        if (targets.size() == 1) {
            return current();
        }
        int nextIndex = index.updateAndGet(current -> {
            int next = current + 1;
            return next >= targets.size() ? 0 : next;
        });
        return targets.get(nextIndex);
    }

    public boolean hasMultipleTargets() {
        return targets.size() > 1;
    }

    public List<HostPort> getTargets() {
        return targets;
    }

    private static List<HostPort> parseTargets(String targetsConfig, int defaultPort) {
        if (StringUtils.isBlank(targetsConfig)) {
            return Collections.emptyList();
        }
        List<HostPort> targets = new ArrayList<>();
        for (String rawTarget : targetsConfig.split(",")) {
            String trimmed = rawTarget.trim();
            if (StringUtils.isBlank(trimmed)) {
                continue;
            }
            HostPort parsed = parseTarget(trimmed, defaultPort);
            if (parsed != null) {
                targets.add(parsed);
            }
        }
        return targets;
    }

    private static HostPort parseTarget(String value, int defaultPort) {
        String host = null;
        int port = defaultPort;
        if (value.startsWith("[")) {
            int closing = value.indexOf(']');
            if (closing > 1) {
                host = value.substring(1, closing);
                if (closing + 1 < value.length() && value.charAt(closing + 1) == ':') {
                    String portPart = value.substring(closing + 2);
                    if (StringUtils.isNumeric(portPart)) {
                        port = Integer.parseInt(portPart);
                    }
                }
            }
        } else {
            int firstColon = value.indexOf(':');
            int lastColon = value.lastIndexOf(':');
            if (firstColon > 0 && firstColon == lastColon) {
                host = value.substring(0, firstColon).trim();
                String portPart = value.substring(firstColon + 1).trim();
                if (StringUtils.isNumeric(portPart)) {
                    port = Integer.parseInt(portPart);
                }
            } else {
                host = value;
            }
        }
        if (StringUtils.isBlank(host)) {
            return null;
        }
        return new HostPort(host, port);
    }

    public static final class HostPort {
        private final String host;
        private final int port;

        public HostPort(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            HostPort other = (HostPort) obj;
            return port == other.port && Objects.equals(host, other.host);
        }

        @Override
        public int hashCode() {
            return Objects.hash(host, port);
        }

        @Override
        public String toString() {
            return host + ":" + port;
        }
    }
}