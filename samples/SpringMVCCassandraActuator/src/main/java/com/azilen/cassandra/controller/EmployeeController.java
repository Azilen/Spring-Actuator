package com.azilen.cassandra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.azilen.cassandra.config.EmployeeRepository;
import com.azilen.cassandra.entity.Employee;

@Controller
public class EmployeeController {


	@Autowired
	EmployeeRepository employeeRepository;

	@RequestMapping(value = "/person", method = RequestMethod.GET)
	public String getPersonList(ModelMap model) {
		model.addAttribute("personList", employeeRepository.findAll());
		return "output";
	}

	@RequestMapping(value = "/person/save", method = RequestMethod.POST)
	public View createPerson(@ModelAttribute Employee employee, ModelMap model) {
		
		employeeRepository.save(employee);

		return new RedirectView("/SpringMVCCassandraActuator/person");
	}

}
