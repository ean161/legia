package vn.ean.legia.controller.mcserver_deposit_portal;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import vn.ean.legia.model.User;
import vn.ean.legia.model.mcserver_deposit_portal.Server;
import vn.ean.legia.service.UserService;
import vn.ean.legia.service.mcserver_deposit_portal.ServerService;
import vn.ean.legia.util.Response;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller("mcServerDepositPortalController")
@RequestMapping("/mcserver-deposit-portal")
public class DefaultController {

    private final Response res;

    @Autowired
    private ServerService serverService;

    @Autowired
    private UserService userService;
    
    public DefaultController() {
        this.res = new Response();
    }


    @GetMapping
    public String index(HttpServletRequest request) {
        return "pages/mcserver_deposit_portal/index";
    }

    @GetMapping("/list")
    @ResponseBody
    public Page<Server> list(HttpServletRequest request, @RequestParam(required = false) String search, Pageable pageable) {
        User user = userService.extractUser(request);
        return serverService.list(user, search, pageable);
    }

    @PostMapping("/create")
    @ResponseBody
    public Map<String, Object> create(HttpServletRequest request, @RequestParam(required = false) String title, @RequestParam(required = false) String description, @RequestParam(required = false) String banner) {
        User user = userService.extractUser(request);

        try {
            serverService.save(user, 0l, title, description, banner);
            return res.create(1, "Thêm cổng nạp thành công", null);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public Map<String, Object> edit(HttpServletRequest request, @RequestParam(required = false) Long id, @RequestParam(required = false) String title, @RequestParam(required = false) String description, @RequestParam(required = false) String banner) {
        User user = userService.extractUser(request);

        try {
            serverService.save(user, id, title, description, banner);
            return res.create(1, "Sửa cổng nạp thành công", null);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> delete(HttpServletRequest request, @RequestParam Long id) {
        User user = userService.extractUser(request);
        
        try {
            serverService.delete(user, id);
            return res.create(1, "Xoá cổng nạp thành công", null);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }
}
