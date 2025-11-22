package vn.ean.legia.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ean.legia.model.User;
import vn.ean.legia.repository.UserRepository;
import vn.ean.legia.util.Lib;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User extractUser(HttpServletRequest request) {
        String token = Lib.getCookie(request, "token");

        if (token != null && userRepository.existsByToken(token)) {
            return userRepository.findByToken(token);
        }

        return null;
    }

    public void revokeUser(HttpServletRequest request, HttpServletResponse response) {
        User user = extractUser(request);

        if (user == null) {
            return;
        }

        userRepository.updateTokenByUsername(user.getUsername(), generateToken());
        Lib.setCookie(response, "token", null, 0);
        Lib.setSession(request, "user", null);
    }

    public String generateToken() {
        return Lib.randStr(100);
    }
}
