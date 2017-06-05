package spring.springmvc01.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import spring.springmvc01.bean.RedisPan;
import spring.springmvc01.service.HttpRedisService;
import spring.springmvc01.service.RmiRedisService;

@Controller
@RequestMapping(value = "/")
public class HomeController{

	@Autowired
	private RmiRedisService rmiRedisService; 
	@Autowired
	private HttpRedisService httpRedisService; 
	
	@RequestMapping(method = RequestMethod.GET)
	public String home() {
		return "redirect:/file";
	}


	@RequestMapping(method = RequestMethod.GET, value = "file")
	public String getFile(Model model) {
		model.addAttribute("welcome", "welcome");
		return "home";
	}



	// spring-data-redis

	
	@RequestMapping(method = RequestMethod.POST, value = "springdataredissave")
	public String springdataredissave(@RequestParam(name = "id") int id,
			@RequestParam(name = "name") String name, @RequestParam(name = "password") String password,
			@RequestParam(name = "enabled") int enabled) {
		RedisPan pan = new RedisPan(id,name, password, enabled);
		rmiRedisService.redissave(pan);
		return "redirect:/file";
	}

	
	
	@RequestMapping(method = RequestMethod.POST, value = "springdataredisFindOne")
	public String springdataredisFindOne(@RequestParam(name = "id", defaultValue = "-1") int id,
			 RedirectAttributes redirectAttributes) {
		RedisPan pan = rmiRedisService.redisget(id);
		redirectAttributes.addFlashAttribute("springdataredispan", pan);
		return "redirect:/file";
	}
	
	//http - redis
	
	@RequestMapping(method = RequestMethod.POST, value = "httpspringdataredissave")
	public String httpspringdataredissave(@RequestParam(name = "id") int id,
			@RequestParam(name = "name") String name, @RequestParam(name = "password") String password,
			@RequestParam(name = "enabled") int enabled) {
		RedisPan pan = new RedisPan(id,name, password, enabled);
		httpRedisService.redissave(pan);
		return "redirect:/file";
	}

	
	
	@RequestMapping(method = RequestMethod.POST, value = "httpspringdataredisFindOne")
	public String httpspringdataredisFindOne(@RequestParam(name = "id", defaultValue = "-1") int id,
			 RedirectAttributes redirectAttributes) {
		RedisPan pan = httpRedisService.redisget(id);
		redirectAttributes.addFlashAttribute("httpspringdataredispan", pan);
		return "redirect:/file";
	}
	
	//rest客户端
	@RequestMapping(method = RequestMethod.POST, value = "restFindOne")
	public String restFindOne(@RequestParam(name = "id", defaultValue = "-1") int id,
			 RedirectAttributes redirectAttributes) {
		RestTemplate restTemplate = new RestTemplate();
		RedisPan pan = restTemplate.getForObject("http://localhost:8080/SpringmvcWithoutXml/rest/restredisFindOne/{id}", RedisPan.class,id);
		redirectAttributes.addFlashAttribute("restFindOnepan", pan);
		return "redirect:/file";
	}
}
