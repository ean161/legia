package vn.ean.legia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ean.legia.repository.ServiceCategoryRepository;

import vn.ean.legia.model.ServiceCategory;

@Service
public class ServiceCategoryService {

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    public List<ServiceCategory> list() {
        List<ServiceCategory> categories = serviceCategoryRepository.findAll();
        return categories;
    }
}
