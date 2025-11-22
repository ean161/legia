package vn.ean.legia.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import vn.ean.legia.model.User;
import vn.ean.legia.repository.UserRepository;
import vn.ean.legia.util.Lib;
import vn.ean.legia.util.Validate;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, Lib.hashMD5(password));
    }

    public User register(String username, String password) {
        if (username == null || username.isEmpty() || !Validate.isValidUsername(username)) {
            throw new RuntimeException("Tài khoản chứa chữ cái alpha và số, độ dài từ 6 - 30 kí tự");
        } else if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Tài khoản này đã tồn tại");
        } else if (password == null || password.isEmpty() || !Validate.isValidPassword(password)) {
            throw new RuntimeException("Mật khẩu phải có độ dài từ 6 - 100 kí tự");
        }

        User newUser = new User(username.toLowerCase(), Lib.hashMD5(password), Lib.randStr(100));
        return userRepository.save(newUser);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        userService.revokeUser(request, response);
    }

    public void giveAccess(String username, HttpServletResponse response) {
        String token = Lib.randStr(100);

        User user = userRepository.findByUsername(username).orElseThrow();
        user.setToken(token);
        userRepository.save(user);

        Lib.setCookie(response, "token", token, 86400);
    }
}
