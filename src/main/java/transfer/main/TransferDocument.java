package transfer.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransferDocument {
	private static final Logger logger = LoggerFactory.getLogger(TransferDocument.class);

	public static void main(String[] args) {
		SpringApplication.run(TransferDocument.class, args);
//		if (args.length != 3) throw new IllegalArgumentException("Usage: Client <node address> <rpc username> <rpc password>");
//		final NetworkHostAndPort nodeAddress = parse("localhost:10009");
//		final String rpcUsername = "user";
//		final String rpcPassword = "test";
//		final CordaRPCClient client = new CordaRPCClient(nodeAddress);
//		final CordaRPCConnection clientConnection = client.start(rpcUsername, rpcPassword);
//		final CordaRPCOps proxy = clientConnection.getProxy();
//
//		// Interact with the node.
//		// Example #1, here we print the nodes on the network.
//		final List<NodeInfo> nodes = proxy.networkMapSnapshot();
//		System.out.println("\n-- Here is the networkMap snapshot --");
//		logger.info("{}", nodes);
//
//		// Example #2, here we print the PartyA's node info
//		CordaX500Name name = proxy.nodeInfo().getLegalIdentities().get(0).getName();//nodeInfo().legalIdentities.first().name
//		System.out.println("\n-- Here is the node info of the node that the client connected to --");
//		logger.info("{}", name);
//
//		//Close the client connection
//		clientConnection.close();
	}

}
