package vn.ean.legia.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.ean.legia.model.License;
import vn.ean.legia.model.User;
import vn.ean.legia.repository.LicenseRepository;
import vn.ean.legia.repository.UserRepository;
import vn.ean.legia.service.UserService;
import vn.ean.legia.util.Lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuthFilter implements Filter {
    
    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        if (path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        Long now = Lib.currentTimestamp();
        User user = userService.extractUser(req);
        List<String> allowedLicense = new ArrayList<>();

        List<License> freeLicense = licenseRepository.findByUserAndExpiredDateGreaterThan(userRepository.findById(1L).orElseThrow(), now);
        for (License license : freeLicense) {
            allowedLicense.add(license.getDirectory());
        }

        if (user != null) {
            List<License> userLicense = licenseRepository.findByUserAndExpiredDateGreaterThan(userRepository.findById(2L).orElseThrow(), now);
            for (License license : userLicense) {
                allowedLicense.add(license.getDirectory());
            }

            List<License> listLicense = user.getValidLicenses();
            if (listLicense != null) {
                for (License license : listLicense) {
                    allowedLicense.add(license.getDirectory());
                }
            }
        }

        if (!allowedLicense.contains(path)) {
            res.sendRedirect(user != null ? "/" : "/auth?r=" + path);
            return;
        }

        Lib.setSession(req, "user", user);
        
        chain.doFilter(request, response);
    }
}
