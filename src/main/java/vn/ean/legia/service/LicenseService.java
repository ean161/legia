package vn.ean.legia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.ean.legia.model.License;
import vn.ean.legia.model.User;
import vn.ean.legia.repository.LicenseRepository;
import vn.ean.legia.repository.UserRepository;
import vn.ean.legia.util.Lib;

@Service
public class LicenseService {
    
    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<License> list(String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            if (userRepository.existsByUsername(search)) {
                return licenseRepository.findByUserOrDirectoryContainingIgnoreCase(userRepository.findByUsername(search).orElseThrow(), search, pageable);
            } else {
                return licenseRepository.findByDirectoryContainingIgnoreCase(search, pageable);
            }
        }

        return licenseRepository.findAll(pageable);
    }

    public void create(User user, String directory, String title, String note, String icon, String expiredDate) {
        Long expiredDateTsm;

        if (expiredDate == null || expiredDate.isEmpty()) {
            expiredDateTsm = 9999999999L;
        } else {
            expiredDateTsm = Lib.dateFormConvert(expiredDate);
            if (expiredDateTsm < Lib.currentTimestamp()) {
                expiredDateTsm = 9999999999L;
            }
        }

        if (directory.contains("\n")) {
            String[] directories = directory.split("\n");
            for (String dir : directories) {
                if (dir != null && !dir.isEmpty()) {
                    licenseRepository.save(new License(user, title, dir, note, icon, expiredDateTsm));
                }
            }
        } else {
            licenseRepository.save(new License(user, title, directory, note, icon, expiredDateTsm));
        }
    }

    public void revoke(Long id) {
        licenseRepository.updateExpiredDateById(Lib.currentTimestamp(), id);
    }
}
