

package com.mproduits.web.controller;

import com.mproduits.dao.ProductDao;
import com.mproduits.model.Product;
import com.mproduits.web.configuration.ApplicationPropertiesConfiguration;
import com.mproduits.web.exceptions.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;




@RestController
public class ProductController {

    private final ProductDao productDao;
    private final ApplicationPropertiesConfiguration appProperties;

    public ProductController(ProductDao productDao, ApplicationPropertiesConfiguration appProperties) {
        this.productDao = productDao;
        this.appProperties = appProperties;
    }

// Affiche la liste de tous les produits disponibles

    @GetMapping(value = "/Produits")

    public List<Product> listeDesProduits() {
        List<Product> products = productDao.findAll();

        if (products.isEmpty()) throw new ProductNotFoundException("Aucun produit n'est disponible à la vente");

        List<Product> listeLimitee = products.subList(0, appProperties.getLimitDeProduits());

        return listeLimitee;
    }
}