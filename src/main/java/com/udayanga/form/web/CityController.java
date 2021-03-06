package com.udayanga.form.web;

import com.udayanga.form.model.City;
import com.udayanga.form.model.User;
import com.udayanga.form.service.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class CityController {
    private final Logger logger = LoggerFactory.getLogger(CityController.class);

    private CityService cityService;

    @Autowired
    public void setCityService(CityService cityService) {
        this.cityService = cityService;
    }

    // list page
    @RequestMapping(value = "/cities", method = RequestMethod.GET)
    public String showAllCities(Model model) {

        logger.debug("showAllCities()");
        model.addAttribute("cities", cityService.findAll());
        return "city/list";

    }

    @RequestMapping(value = "/cities", method = RequestMethod.POST)
    public String saveOrUpdateCity(@ModelAttribute("cityForm") @Validated City city, BindingResult result,
                                   Model model, final RedirectAttributes redirectAttributes) {
        logger.debug("saveOrUpdateCity() : {})", city);
        if (result.hasErrors()) {
            populateDefaultModel(model);
            return "city/cityform";
        } else {

            redirectAttributes.addFlashAttribute("css", "success");
            if (city.isNew()) {
                redirectAttributes.addFlashAttribute("msg", "City added successfully!");
            } else {
                redirectAttributes.addFlashAttribute("msg", "City updated successfully!");
            }

            cityService.saveOrUpdate(city);

            // POST/REDIRECT/GET
            return "redirect:/cities/" + city.getCityID();

            // POST/FORWARD/GET
            // return "user/list";

        }
    }
    // show add user form
    @RequestMapping(value = "/cities/add", method = RequestMethod.GET)
    public String showAddUserForm(Model model) {

        logger.debug("showAddCityForm()");

        City city = new City();

        // set default value

        city.setCityName("Default Name");
        city.setCityDescription("Default Description");
        city.setRate(5);
        city.setCityImgUrl("Image URL");

        model.addAttribute("cityForm", city);

        populateDefaultModel(model);

        return "city/cityform";

    }

    // show update form
    @RequestMapping(value = "/cities/{id}/update", method = RequestMethod.GET)
    public String showUpdateUserForm(@PathVariable("id") int id, Model model) {

        logger.debug("showUpdateCityForm() : {}", id);

        City city = cityService.findById(id);
        model.addAttribute("cityForm", city);

        populateDefaultModel(model);

        return "city/cityform";

    }

    // delete user
    @RequestMapping(value = "/cities/{id}/delete", method = RequestMethod.POST)
    public String deleteUser(@PathVariable("id") int id, final RedirectAttributes redirectAttributes) {

        logger.debug("deleteCity() : {}", id);

        cityService.delete(id);

        redirectAttributes.addFlashAttribute("css", "success");
        redirectAttributes.addFlashAttribute("msg", "User is deleted!");

        return "redirect:/cities";

    }

    // show user
    @RequestMapping(value = "/cities/{id}", method = RequestMethod.GET)
    public String showUser(@PathVariable("id") int id, Model model) {

        logger.debug("showCity() id: {}", id);

        City city = cityService.findById(id);
        if (city == null) {
            model.addAttribute("css", "danger");
            model.addAttribute("msg", "User not found");
        }
        model.addAttribute("city", city);

        return "city/show";

    }
    private void populateDefaultModel(Model model) {

        List<String> frameworksList = new ArrayList<String>();
        frameworksList.add("Spring MVC");
        frameworksList.add("Struts 2");
        frameworksList.add("JSF 2");
        frameworksList.add("GWT");
        frameworksList.add("Play");
        frameworksList.add("Apache Wicket");
        model.addAttribute("frameworkList", frameworksList);

        Map<String, String> skill = new LinkedHashMap<String, String>();
        skill.put("Hibernate", "Hibernate");
        skill.put("Spring", "Spring");
        skill.put("Struts", "Struts");
        skill.put("Groovy", "Groovy");
        skill.put("Grails", "Grails");
        model.addAttribute("javaSkillList", skill);

        List<Integer> numbers = new ArrayList<Integer>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        numbers.add(5);
        model.addAttribute("numberList", numbers);

        Map<String, String> country = new LinkedHashMap<String, String>();
        country.put("US", "United Stated");
        country.put("CN", "China");
        country.put("SG", "Singapore");
        country.put("MY", "Malaysia");
        model.addAttribute("countryList", country);

    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ModelAndView handleEmptyData(HttpServletRequest req, Exception ex) {

        logger.debug("handleEmptyData()");
        logger.error("Request: {}, error ", req.getRequestURL(), ex);

        ModelAndView model = new ModelAndView();
        model.setViewName("city/show");
        model.addObject("msg", "City not found");

        return model;

    }

}
