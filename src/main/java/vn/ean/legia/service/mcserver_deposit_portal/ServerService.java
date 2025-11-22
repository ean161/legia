package vn.ean.legia.service.mcserver_deposit_portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.ean.legia.model.User;
import vn.ean.legia.model.mcserver_deposit_portal.Server;
import vn.ean.legia.repository.mcserver_deposit_portal.ServerRepository;
import vn.ean.legia.util.Validate;

@Service("mcServerService")
public class ServerService {

    @Autowired
    ServerRepository serverRepository;
    
    public Page<Server> list(User user, String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            return serverRepository.findByUserAndTitleContainingIgnoreCase(user, search, pageable);
        }

        return serverRepository.findByUser(user, pageable);
    }

    public void save(User user, Long id, String title, String description, String banner) {
        if (title == null || title.isEmpty()) {
            throw new RuntimeException("Tên máy chủ không hợp lệ");
        } else if (!Validate.isAlnum(title) || !Validate.isMarkSafe(title) || title.length() > 25) {
            throw new RuntimeException("Tên máy chủ chỉ được phép chứa chữ cái không dấu và số, ít hơn 25 kí tự");
        } else if (description == null || description.isEmpty()) {
            throw new RuntimeException("Thông tin máy chủ không hợp lệ");
        } else if (!Validate.isMarkSafe(description) || description.length() > 100) {
            throw new RuntimeException("Thông tin máy chủ không được chứa kí tự ngoặc kép, ngoặc đơn, ít hơn 100 kí tự");
        } else if (banner == null || banner.isEmpty() || !banner.startsWith("https://") || banner.length() > 1000) {
            throw new RuntimeException("Link banner không hợp lệ, hoặc link quá dài");
        }

        if (id == null || id == 0) {
            if (serverRepository.existsByTitle(title)) {
                throw new RuntimeException("Tên máy chủ đã tồn tại");
            }

            serverRepository.save(new Server(user, title, description, banner));
        } else {
            Server server =  serverRepository.findById(id).orElseThrow();
            if (!serverRepository.existsById(id)) {
                throw new RuntimeException("Cổng nạp không tồn tại");
            } else if (server.getUser().getId() != user.getId()) {
                throw new RuntimeException("Bạn không có quyền chỉnh sửa cổng nạp này");
            }

            serverRepository.save(new Server(id, user, title, description, banner));
        }
    }

    public void delete(User user, Long id) {
        if (id == null || !serverRepository.existsById(id)) {
            throw new RuntimeException("Cổng nạp không tồn tại");
        }

        Server server =  serverRepository.findById(id).orElseThrow();
        if (server.getUser().getId() != user.getId()) {
            throw new RuntimeException("Bạn không có quyền xoá cổng nạp này");
        }


        serverRepository.deleteById(id);
    }
}
