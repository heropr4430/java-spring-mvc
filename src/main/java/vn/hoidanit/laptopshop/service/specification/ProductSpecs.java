package vn.hoidanit.laptopshop.service.specification;

import java.util.List;
import java.util.ArrayList;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.Product_;

public class ProductSpecs {
    public static Specification<Product> nameLike(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Product_.NAME), "%" + name + "%");
    }

    public static Specification<Product> minPrice(double price) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.ge(root.get(Product_.PRICE), price);
    }

    public static Specification<Product> maxPrice(double price) {
        if (price == 0) {
            return minPrice(0);
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.le(root.get(Product_.PRICE), price);
    }

    public static Specification<Product> nameFactory(List<String> factoties) {
        if (factoties.isEmpty()) {
            return nameLike("");
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get(Product_.FACTORY)).value(factoties);

    }

    public static Specification<Product> nameTarget(List<String> targets) {
        if (targets.isEmpty()) {
            return nameLike("");
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get(Product_.TARGET)).value(targets);

    }

    public static Specification<Product> priceLevel(List<String> priceList) {

        if (priceList.isEmpty()) {
            return minPrice(0);
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String price : priceList) {
                double fromPrice = 0;
                double toPrice = 0;
                if (price.equals("duoi-10-trieu")) {
                    fromPrice = 0;
                    toPrice = 10000000;
                    predicates.add(criteriaBuilder.between(root.get(Product_.PRICE), fromPrice, toPrice));
                    continue;
                }
                if (price.equals("tren-20-trieu")) {
                    fromPrice = 20000000;
                    toPrice = 200000000;
                    predicates.add(criteriaBuilder.between(root.get(Product_.PRICE), fromPrice, toPrice));
                    continue;
                }
                String regex = "[^\\d]+";
                String[] str = price.split(regex);
                fromPrice = Double.parseDouble(str[0]) * 1000000;
                toPrice = Double.parseDouble(str[1]) * 1000000;
                predicates.add(criteriaBuilder.between(root.get(Product_.PRICE), fromPrice, toPrice));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };

    }
}
