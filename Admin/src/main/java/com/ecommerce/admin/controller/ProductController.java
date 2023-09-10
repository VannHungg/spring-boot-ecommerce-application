package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.ProductService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/products")
    public String products(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        List<ProductDto> productDtos = productService.findAll();
        model.addAttribute("products", productDtos);
        model.addAttribute("title", "Manage Product");
        model.addAttribute("size", productDtos.size());
        return "products";
    }

    @GetMapping("/add-product")
    public String addProduct(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        model.addAttribute("newProduct", new ProductDto());
        model.addAttribute("categories", categoryService.findAllByActivatedTrue());
        return "add-product";
    }

    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("newProduct") ProductDto productDto,
                              @RequestParam("imageProduct") MultipartFile imageProduct,
                              RedirectAttributes redirectAttributes) {
        try {
            productService.save(imageProduct, productDto);
            redirectAttributes.addFlashAttribute("success", "Add product sucessfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("failed", "Can not add new product");
        }
        return "redirect:/products";
    }

    @GetMapping("/update-product/{id}")
    public String updateProductForm(Model model, @PathVariable("id")Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        ProductDto productDto = productService.findById(id);
        List<Category> categories = categoryService.findAllByActivatedTrue();
        model.addAttribute("product", productDto);
        model.addAttribute("categories", categories);
        model.addAttribute("title", "Update product");
        return "update-product";
    }

    @PostMapping("/update-product/{id}")
    public String updateProduct(@ModelAttribute("product")ProductDto productDto,
                                @RequestParam("imageProduct")MultipartFile file,
                                RedirectAttributes redirectAttributes){
        try{
            productService.update(productDto, file);
            redirectAttributes.addFlashAttribute("success", "Update product successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("failed", "Can not update product right now");
        }
        return "redirect:/products";
    }

    @GetMapping("/enable-product/{id}")
    public String enableProduct(@PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        try{
            productService.enableById(id);
            redirectAttributes.addFlashAttribute("success", "Enable product successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("failed", "Can not enable product");
        }
         return "redirect:/products";
    }

    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        try{
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Delete product successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("failed", "Can not delete product");
        }
        return "redirect:/products";
    }
}
