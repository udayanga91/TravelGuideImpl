package com.udayanga.form.web;

import com.udayanga.form.model.Package;
import com.udayanga.form.model.Place;
import com.udayanga.form.service.PackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class PackageController {
    private final Logger logger = LoggerFactory.getLogger(PackageController.class);

    PackageService packageService;

    @Autowired
    public void setPackageService(PackageService packageService) {
        this.packageService = packageService;
    }

    // list page
    @RequestMapping(value = "/packages", method = RequestMethod.GET)
    public String showAllPackages(Model model) {

        logger.debug("showAllPackages()");
        model.addAttribute("packages", packageService.findAll());
        return "package/list";
    }

    // show package
    @RequestMapping(value = "/packages/{id}", method = RequestMethod.GET)
    public String showPackage(@PathVariable("id") int id, Model model) {

        logger.debug("showPackage() id: {}", id);

        Package aPackage = packageService.findById(id);
        List<Place> places = packageService.findPlacesByPackage(id);

        if (aPackage == null) {
            model.addAttribute("css", "danger");
            model.addAttribute("msg", "Package not found");
        }
        model.addAttribute("package", aPackage);
        model.addAttribute("places", places);

        return "package/show";

    }

    // delete place
    @RequestMapping(value = "/packages/{packageId}/{placeId}/delete", method = RequestMethod.POST)
    public String deleteUser(@PathVariable("packageId") int packageId, @PathVariable("placeId") int placeId, final RedirectAttributes redirectAttributes) {

        logger.debug("deletePlaceByPackage() : {} {}", packageId, placeId);

        packageService.deletePlaceByPackage(packageId, placeId);
        redirectAttributes.addFlashAttribute("css", "success");
        redirectAttributes.addFlashAttribute("msg", "Place is deleted!");

        return "redirect:/packages/" + packageId;

    }
}
