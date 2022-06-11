package transfer.api;

import lombok.Getter;
import lombok.Setter;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.utilities.NetworkHostAndPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Wraps an RPC connection to a Corda node.
 *
 * The RPC connection is configured using command line arguments.
 */
@Component
@Getter
@Setter
public class NodeRPCConnection implements AutoCloseable {
    // The host of the node we are connecting to.
    @Value("localhost")
    private String host;
    // The RPC port of the node we are connecting to.
    @Value("user1")
    private String username;
    // The username for logging into the RPC client.
    @Value("test1")
    private String password;
    // The password for logging into the RPC client.
    @Value("10009")
    private int rpcPort;

    private CordaRPCConnection rpcConnection;
    CordaRPCOps proxy;

    @PostConstruct
    public void initialiseNodeRPCConnection() {
        NetworkHostAndPort rpcAddress = new NetworkHostAndPort(host, rpcPort);
        CordaRPCClient rpcClient = new CordaRPCClient(rpcAddress);
        rpcConnection = rpcClient.start(username, password);
        proxy = rpcConnection.getProxy();
    }

    @PreDestroy
    public void close() {
        rpcConnection.notifyServerAndClose();
    }
}