package vn.ean.legia.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import vn.ean.legia.model.License;
import vn.ean.legia.repository.UserRepository;
import vn.ean.legia.service.LicenseService;
import vn.ean.legia.util.Response;

@Controller
@RequestMapping("/license")
public class LicenseController {
    
    private final Response res;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private UserRepository userRepository;

    public LicenseController() {
        this.res = new Response();
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("userList", userRepository.findAll());
        return "pages/license";
    }

    @GetMapping("/list")
    @ResponseBody
    public Page<License> list(@RequestParam(required = false) String search, Pageable pageable) {
        return licenseService.list(search, pageable);
    }
    
    @PostMapping("/create")
    @ResponseBody
    public Map<String, Object> create(@RequestParam(required = false) Long id, @RequestParam(required = false) String directory, @RequestParam(required = false) String title, @RequestParam(required = false) String note, @RequestParam(required = false) String icon,@RequestParam(required = false) String expiredDate) {
        licenseService.create(userRepository.findById(id).orElseThrow(), directory, title, note, icon, expiredDate);
        return res.create(1, "Tạo thành công phân quyền", null);
    }
    
    @PostMapping("/revoke")
    @ResponseBody
    public Map<String, Object> revoke(@RequestParam(required = false) Long id) {
        licenseService.revoke(id);
        return res.create(1, "Thu hồi quyền thành công", null);
    }
}
