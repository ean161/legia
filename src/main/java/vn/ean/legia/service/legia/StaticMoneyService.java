package vn.ean.legia.service.legia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.ean.legia.model.legia.StaticMoney;
import vn.ean.legia.repository.legia.StaticMoneyRepository;

@Service("legiaStaticMoneyService")
public class StaticMoneyService {
    
    @Autowired
    private StaticMoneyRepository staticMoneyRepository;

    public Page<StaticMoney> list(String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            return staticMoneyRepository.findByStaff_NameContainingIgnoreCase(search, pageable);
        }

        return staticMoneyRepository.findAll(pageable);
    }
}
