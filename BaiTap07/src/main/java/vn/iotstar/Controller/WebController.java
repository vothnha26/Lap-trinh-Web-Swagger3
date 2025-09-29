package vn.iotstar.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/category")
    public String category() {
        return "category";
    }
    
    @GetMapping("/product")
    public String product() {
        return "product";
    }
}
