package vn.ean.legia.service.sports_store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ean.legia.model.sports_store.Deploy;
import vn.ean.legia.repository.UserRepository;
import vn.ean.legia.repository.sports_store.DeployRepository;

@Service("sportsStoreDeployService")
public class DeployService {

    @Autowired
    private DeployRepository deployRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<Deploy> history(String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            if (userRepository.existsByUsername(search)) {
                return deployRepository.findByUser(userRepository.findByUsername(search).orElseThrow(), pageable);
            }
        }

        return deployRepository.findAll(pageable);
    }
}
