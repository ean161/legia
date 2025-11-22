package vn.ean.legia.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import vn.ean.legia.model.Transaction;
import vn.ean.legia.model.User;
import vn.ean.legia.service.TransactionService;
import vn.ean.legia.service.UserService;
import vn.ean.legia.util.Response;

@Controller
@RequestMapping("/transaction")
public class TransactionController {

    private final Response res;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Value("${acb.bank}")
    private String acbBank;

    @Value("${acb.account}")
    private String acbAccount;

    @Value("${acb.name}")
    private String acbName;

    public TransactionController() {
        this.res = new Response();
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("acbBank", acbBank);
        model.addAttribute("acbAccount", acbAccount);
        model.addAttribute("acbName", acbName);

        return "pages/transaction";
    }

    @GetMapping("/banking-scan")
    @ResponseBody
    public Map<String, Object> bankingScan() {
        try {
            Map<String, Object> scan = transactionService.bankingScan();
            return res.create(1, "Quét lịch sử thành công", scan);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @GetMapping("/list")
    @ResponseBody
    public Page<Transaction> list(HttpServletRequest request, @RequestParam(required = false) String search, Pageable pageable) {
        User user = userService.extractUser(request);
        return transactionService.list(user, search, pageable);
    }

    @PostMapping("/banking-withdraw")
    @ResponseBody
    public Map<String, Object> bankingWithdraw(HttpServletRequest request, @RequestParam(required = false) String bankType, @RequestParam(required = false) String bankNum, @RequestParam(required = false) Integer amount) {
        User user = userService.extractUser(request);

        try {
            transactionService.bankingWithdraw(user, bankType, bankNum, amount);
            return res.create(1, "Yêu cầu rút tiền đã được tạo thành công", null);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @PostMapping("/card-withdraw")
    @ResponseBody
    public Map<String, Object> cardWithdraw(HttpServletRequest request, @RequestParam(required = false) String cardType, @RequestParam(required = false) Integer amount) {
        User user = userService.extractUser(request);

        try {
            transactionService.cardWithdraw(user, cardType, amount);
            return res.create(1, "Yêu cầu rút tiền đã được tạo thành công", null);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }
}
