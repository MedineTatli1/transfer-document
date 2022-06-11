package transfer.api;

import transfer.flows.ExampleFlow;
import transfer.states.IOUState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping("/")
public class CordaIntegrationController {

    private final CordaRPCOps proxy;
    private final CordaX500Name me;


    public CordaIntegrationController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();
    }

    @GetMapping(value = "/ious",produces = APPLICATION_JSON_VALUE)
    public List<StateAndRef<IOUState>> getIOUs() {
        // Filter by state type: IOU.
        return proxy.vaultQuery(IOUState.class).getStates();
    }

    //alıcı firma alıcı id bu dosya şifrelenerek blokceinde tutulcak. alıcı firma ve alıcı id ile alakalı blokchainde bir dosya varsa onu gelen dosyalarda görebilcek.
    //inaktif bir dosya ise invalid olduğu yazar yine görüntüler.
    //

    @PostMapping(value = "create-iou" , produces =  TEXT_PLAIN_VALUE , headers =  "Content-Type=application/x-www-form-urlencoded" )
    public ResponseEntity<String> issueIOU(HttpServletRequest request) throws IllegalArgumentException {

        int amount = Integer. valueOf(request.getParameter("iouValue"));
        String party = request.getParameter("partyName");
        // Get party objects for myself and the counterparty.

        CordaX500Name partyX500Name = CordaX500Name.parse(party);
        Party otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name);

        // Create a new IOU state using the parameters given.
        try {
            // Start the IOUIssueFlow. We block and waits for the flow to return.
            SignedTransaction result = proxy.startTrackedFlowDynamic(ExampleFlow.Initiator.class, amount,otherParty).getReturnValue().get();
            // Return the response.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Transaction id "+ result.getId() +" committed to ledger.\n " + result.getTx().getOutput(0));
            // For the purposes of this demo app, we do not differentiate by exception type.
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    /**
     * Displays all IOU states that only this node has been involved in.
     */
    @GetMapping(value = "my-ious",produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StateAndRef<IOUState>>> getMyIOUs() {
        List<StateAndRef<IOUState>> myious = proxy.vaultQuery(IOUState.class).getStates().stream().filter(
                it -> it.getState().getData().getLender().equals(proxy.nodeInfo().getLegalIdentities().get(0))).collect(Collectors.toList());
        return ResponseEntity.ok(myious);
    }
}
