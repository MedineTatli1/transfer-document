package transfer.api;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/")
public class TestController {

    @GetMapping(value = "/asd",produces = APPLICATION_JSON_VALUE)
    public String getIOUs() {
        return "asd";
    }

}
