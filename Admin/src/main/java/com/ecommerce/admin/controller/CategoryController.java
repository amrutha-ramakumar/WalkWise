package com.ecommerce.admin.controller;
// Import statements
import com.ecommerce.library.model.Category;
import com.ecommerce.library.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.security.Principal;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Method to return the categories page
    @GetMapping("/categories")
    public String categories(Model model, Principal principal) {
        // Check if the user is authenticated
        if(principal == null){
            // Redirect to the login page if not authenticated
            return "redirect:/login";
        }
        // Retrieve all categories
        List<Category> categories = categoryService.findAll();
        // Add categories, size, and title attributes to the model
        model.addAttribute("categories", categories);
        model.addAttribute("size", categories.size());
        model.addAttribute("title", "Category");
        // Add a new category object to the model for form submission
        model.addAttribute("categoryNew", new Category());
        // Return the categories view
        return "categories";
    }

    // Method to add a new category
    @PostMapping("/add-category")
    public String add(@ModelAttribute("categoryNew") Category category, RedirectAttributes attributes){
        try{
            // Save the new category
            categoryService.save(category);
            attributes.addFlashAttribute("success", "Added Successfully!");
        }
        catch (DataIntegrityViolationException e){
            // Handle data integrity violation (duplicate name)
            e.printStackTrace();
            attributes.addFlashAttribute("failed","Duplicate name");
        }
        catch (Exception e){
            // Handle other exceptions
            System.out.println("Error");
            e.printStackTrace();
            attributes.addFlashAttribute("failed","Errors");
        }
        // Redirect back to the categories page
        return "redirect:/categories";
    }

    // Method to find a category by its ID
    @RequestMapping(value = "/findById", method = {RequestMethod.PUT,RequestMethod.GET})
    @ResponseBody
    public Category findById(Long id){
        return categoryService.findById(id);
    }

    // Method to update a category
    @PostMapping("/update-category")
    public String update(Category category,RedirectAttributes attributes){
        try {
            // Update the category
            categoryService.update(category);
            attributes.addFlashAttribute("success","Updated successfully");
        }
        catch (DataIntegrityViolationException e){
            // Handle data integrity violation (duplicate name)
            e.printStackTrace();
            attributes.addFlashAttribute("failed","Failed to update because of duplicate name");
        }
        catch (Exception e1){
            // Handle other exceptions
            e1.printStackTrace();
            attributes.addFlashAttribute("failed","Error server");
        }
        // Redirect back to the categories page
        return "redirect:/categories";
    }

    // Method to delete a category
    @RequestMapping(value = "/delete-category",method = {RequestMethod.PUT,RequestMethod.GET})
    public String delete(Long id,RedirectAttributes attributes){
        try {
            // Delete the category
            categoryService.deleteById(id);
            attributes.addFlashAttribute("success","Deleted successfully");
        }
        catch(Exception e){
            // Handle exceptions
            e.printStackTrace();
            attributes.addFlashAttribute("failed","Failed to delete");
        }
        // Redirect back to the categories page
        return "redirect:/categories";
    }

    // Method to enable a category
    @RequestMapping(value = "/enable-category",method = {RequestMethod.PUT,RequestMethod.GET})
    public String enable(Long id,RedirectAttributes attributes){
        try {
            // Enable the category
            categoryService.enableById(id);
            attributes.addFlashAttribute("success","Enabled successfully");
        }
        catch(Exception e){
            // Handle exceptions
            e.printStackTrace();
            attributes.addFlashAttribute("failed","Failed to enable");
        }
        // Redirect back to the categories page
        return "redirect:/categories";
    }
}
