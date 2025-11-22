package vn.ean.legia.controller.legia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.ean.legia.model.legia.StaticMoney;
import vn.ean.legia.service.legia.StaticMoneyService;

@Controller("legiaStaticMoneyController")
@RequestMapping("/legia/static-money")
public class StaticMoneyController {
    
    @Autowired
    private StaticMoneyService staticMoneyService;

    @GetMapping
    public String index() {
        return "pages/legia/static_money";
    }

    @GetMapping("/list")
    @ResponseBody
    public Page<StaticMoney> list(@RequestParam(required = false) String search, Pageable pageable) {
        return staticMoneyService.list(search, pageable);
    }
}
