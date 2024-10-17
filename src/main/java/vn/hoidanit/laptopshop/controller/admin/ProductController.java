package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UploadService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProductController {
    private final ProductService productService;
    private final UploadService uploadService;

    public ProductController(ProductService productService,
            UploadService uploadService) {
        this.productService = productService;
        this.uploadService = uploadService;
    }

    @GetMapping("/admin/product")
    public String getProductPage(Model model,
            @RequestParam("page") Optional<String> pageOptional) {

        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                // convert from String to int
                page = Integer.parseInt(pageOptional.get());
            } else {

            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        Pageable pageable = PageRequest.of(page - 1, 5);
        Page<Product> products = this.productService.fetchProducts(pageable);
        List<Product> listProducts = products.getContent();
        model.addAttribute("products", listProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        return "admin/product/show";
    }

    @GetMapping("/admin/product/create")
    public String getCreateProductPage(Model model) {
        model.addAttribute("newProduct", new Product());
        return "admin/product/create";
    }

    @PostMapping(value = "/admin/product/create")
    public String createUserPage(Model model,
            @ModelAttribute("newProduct") @Valid Product product,
            BindingResult newProductBindingResult,
            @RequestParam("productFileName") MultipartFile file) {
        List<FieldError> errors = newProductBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(error.getField() + " - " + error.getDefaultMessage());
        }
        // validate
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/create";
        }
        String productImage = this.uploadService.handleSaveUploadFile(file, "product");
        product.setImage(productImage);
        product.setSold(0);
        this.productService.createProduct(product);
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/{id}")
    public String getProductDetailPage(Model model, @PathVariable long id) {
        Optional<Product> productInfor = this.productService.fetchProductById(id);
        model.addAttribute("product", productInfor.get());
        return "admin/product/detail";
    }

    @RequestMapping("/admin/product/update/{id}")
    public String updateProductPage(Model model, @PathVariable long id) {
        // TODO: process POST request
        Product productInfor = this.productService.fetchProductById(id).get();
        model.addAttribute("newProduct", productInfor);
        return "admin/product/update";
    }

    @PostMapping("/admin/product/update")
    public String postUpdateProduct(Model model,
            @ModelAttribute("newProduct") @Valid Product product,
            BindingResult newProductBindingResult,
            @RequestParam("productFileName") MultipartFile file) {
        // TODO: process POST request
        List<FieldError> errors = newProductBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(error.getField() + " - " + error.getDefaultMessage());
        }
        // validate
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/update";
        }

        Product productInfor = this.productService.fetchProductById(product.getId()).get();
        if (productInfor != null) {
            if (!file.isEmpty()) {
                String productImage = this.uploadService.handleSaveUploadFile(file, "product");
                productInfor.setImage(productImage);
            }
            productInfor.setName(product.getName());
            productInfor.setDetailDesc(product.getDetailDesc());
            productInfor.setFactory(product.getFactory());
            productInfor.setPrice(product.getPrice());
            productInfor.setQuantity(product.getQuantity());
            productInfor.setShortDesc(product.getShortDesc());
            this.productService.createProduct(productInfor);
        }
        return "redirect:/admin/product";
    }

    @RequestMapping("/admin/product/delete/{id}")
    public String ProductDeletePage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("newProduct", new User());
        return "admin/product/delete";
    }

    @PostMapping("/admin/product/delete")
    public String postDeleteProduct(Model model, @ModelAttribute("newProduct") Product product) {
        this.productService.deleteProduct(product.getId());
        return "redirect:/admin/product";
    }

}