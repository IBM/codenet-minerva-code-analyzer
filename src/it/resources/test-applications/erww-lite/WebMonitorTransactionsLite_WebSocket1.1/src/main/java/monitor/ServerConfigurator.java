package monitor;

import java.util.ArrayList;
import java.util.Arrays;

import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

public class ServerConfigurator extends ServerEndpointConfig.Configurator {
	@Override
	public void modifyHandshake(ServerEndpointConfig sec,
								HandshakeRequest request,
								HandshakeResponse response) {
		// New header
        response.getHeaders().put("ConfiguratorHeader", new ArrayList<String>(
                        Arrays.asList("SUCCESS")));

        // Existing header
        response.getHeaders().put("X-Powered-By", new ArrayList<String>(
                        Arrays.asList("ONE", "TWO", "THREE")));
	}
}
