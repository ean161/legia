package vn.ean.legia.controller.sports_store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.ean.legia.model.sports_store.Deploy;
import vn.ean.legia.service.sports_store.DeployService;

import java.io.File;
import java.util.Objects;

@Controller("sportsStoreDeployController")
@RequestMapping("/sports-store")
public class DeployController {

    @Autowired
    private DeployService deployService;

    @GetMapping("/deploy")
    public String deploy() {
        return "pages/sports_store/deploy";
    }

    @PostMapping("/deploy")
    @ResponseBody
    public String uploadDeploy(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = System.getProperty("java.io.tmpdir");
            File dest = new File(uploadDir, Objects.requireNonNull(file.getOriginalFilename()));
            file.transferTo(dest);

            return ServletUriComponentsBuilder.fromCurrentContextPath().path("/").path(file.getOriginalFilename()).toUriString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/deploy-history")
    @ResponseBody
    public Page<Deploy> history(@RequestParam(required = false) String search, Pageable pageable) {
        return deployService.history(search, pageable);
    }
}
