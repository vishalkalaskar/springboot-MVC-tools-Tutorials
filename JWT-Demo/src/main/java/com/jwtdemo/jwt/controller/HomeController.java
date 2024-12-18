package com.jwtdemo.jwt.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.jwtdemo.jwt.model.JwtRequest;
import com.jwtdemo.jwt.model.JwtResponse;
import com.jwtdemo.jwt.service.UserService;
import com.jwtdemo.jwt.utility.JWTUtility;


/* @Controller */
@RestController
public class HomeController {

	@Autowired
	private JWTUtility jwtUtility;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	 @Value("${jwt.url}")
	  private String url;
	//private String url= "http://localhost:8080/decodedCanData.json";

	@GetMapping("/")
	public String home() {
		return "this is json data";
	}

	@GetMapping(value ="/candata")
	public List<Object> getsingledata() {
		//String url= "http://localhost:8080/decodedCanData.json";
		RestTemplate rest = new RestTemplate();
		Object[] js= rest.getForObject(url, Object[].class);
		return Arrays.asList(js);
	}

	/*
	 * @GetMapping("/candatadisplay")
	 *  public String showEmployees(Model model)
	 * 
	 * { model.addAttribute("candata", getsingledata());
	 *  return "temp"; }
	 */
	
//	public List<String> search(String username)
//	{
//		return ldapTemplate.search
//		return null;
//	}

	@PostMapping("/authenticate")
	public JwtResponse authenticate(@RequestBody JwtRequest jwtRequest) throws Exception {

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}

		final UserDetails userDetails = userService.loadUserByUsername(jwtRequest.getUsername());

		final String token = jwtUtility.generateToken(userDetails);

		return new JwtResponse(token);
	}
}
